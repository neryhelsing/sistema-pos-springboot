package com.casaleo.sistema_pos.services;

import com.casaleo.sistema_pos.dto.PagoCreateDTO;
import com.casaleo.sistema_pos.dto.PagoDetalleCreateDTO;
import com.casaleo.sistema_pos.dto.PagoResponseDTO;
import com.casaleo.sistema_pos.models.Cliente;
import com.casaleo.sistema_pos.models.DetallePago;
import com.casaleo.sistema_pos.models.Factura;
import com.casaleo.sistema_pos.models.MetodoPago;
import com.casaleo.sistema_pos.models.Pago;
import com.casaleo.sistema_pos.repositories.ClienteRepository;
import com.casaleo.sistema_pos.repositories.FacturaRepository;
import com.casaleo.sistema_pos.repositories.PagoRepository;
import com.casaleo.sistema_pos.models.Correlativo;
import com.casaleo.sistema_pos.repositories.CorrelativoRepository;
import com.casaleo.sistema_pos.repositories.DetallePagoRepository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;


import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

@Service
public class PagoService {

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private DetallePagoRepository detallePagoRepository;

    @Autowired
    private CorrelativoRepository correlativoRepository;


    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    public Page<PagoResponseDTO> listarPagos(String query, Pageable pageable) {
        Page<Pago> pagos = pagoRepository.buscarPorNPago(query, pageable);

        return pagos.map(p -> new PagoResponseDTO(
                p.getId(),
                p.getNPago(),
                p.getTotalPagado(),
                p.getCreadoEn(),
                p.getMetodo(),
                p.getEstado()
        ));
    }

    @Transactional
    public PagoResponseDTO crearPago(PagoCreateDTO dto) {

        // ===== Validaciones básicas =====
        if (dto == null) throw new RuntimeException("Datos inválidos.");
        if (dto.getClienteId() == null) throw new RuntimeException("clienteId es obligatorio.");
        if (dto.getMetodo() == null) throw new RuntimeException("El método de pago es obligatorio.");
        if (dto.getDetalles() == null || dto.getDetalles().isEmpty())
            throw new RuntimeException("Debe seleccionar al menos una factura.");

        // ===== Buscar cliente =====
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado: " + dto.getClienteId()));

        // ===== Calcular total desde detalles =====
        BigDecimal total = dto.getDetalles().stream()
                .map(d -> d.getMontoAplicado() == null ? BigDecimal.ZERO : d.getMontoAplicado())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (total.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("El total pagado debe ser mayor a 0.");
        }

        // ===== Crear pago (sin nPago definitivo todavía) =====
        Pago pago = new Pago();
        pago.setCliente(cliente);
        pago.setMetodo(dto.getMetodo());
        pago.setTotalPagado(total);
        pago.setEstado(com.casaleo.sistema_pos.models.EstadoPago.ACTIVO);

        // Normalizar valores
        BigDecimal montoEntregado = nz(dto.getMontoEntregado());
        BigDecimal montoTransferido = nz(dto.getMontoTransferido());

        // Nota: efectivoDevuelto en tu BD lo usás como "vuelto de transferencia".
        // Por eso lo calculamos nosotros (no confiamos en el frontend).
        BigDecimal efectivoDevuelto = BigDecimal.ZERO;

        // ===== Validaciones por método + seteo de campos =====
        if (dto.getMetodo() == MetodoPago.EFECTIVO) {

            // EFECTIVO: monto_entregado debe venir y alcanzar
            if (dto.getMontoEntregado() == null) {
                throw new RuntimeException("Debe indicar el monto entregado en EFECTIVO.");
            }
            if (montoEntregado.compareTo(total) < 0) {
                throw new RuntimeException("El monto entregado no puede ser menor al total a pagar.");
            }

            BigDecimal vuelto = montoEntregado.subtract(total);

            // Guardar campos de EFECTIVO
            pago.setMontoEntregado(montoEntregado);
            pago.setVuelto(vuelto);

            // En EFECTIVO, no hay "vuelto de transferencia"
            pago.setEfectivoDevuelto(BigDecimal.ZERO);

            // Limpiar campos de transferencia
            pago.setBanco(null);
            pago.setNOperacion(null);
            pago.setMontoTransferido(BigDecimal.ZERO);

        } else if (dto.getMetodo() == MetodoPago.TRANSFERENCIA) {

            // TRANSFERENCIA: banco y nOperacion obligatorios
            if (isBlank(dto.getBanco())) {
                throw new RuntimeException("Debe seleccionar banco para transferencia.");
            }
            if (isBlank(dto.getNOperacion())) {
                throw new RuntimeException("Debe indicar el N° de operación para transferencia.");
            }

            // Debe venir montoTransferido
            if (dto.getMontoTransferido() == null) {
                throw new RuntimeException("Debe indicar el monto transferido.");
            }
            if (montoTransferido.compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("El monto transferido debe ser mayor a 0.");
            }

            // ✅ Permitimos que transfiera de más, pero NO de menos
            if (montoTransferido.compareTo(total) < 0) {
                throw new RuntimeException("El monto transferido no puede ser menor al total a pagar.");
            }

            // Excedente (si transfirió de más) -> efectivo_devuelto
            BigDecimal excedente = montoTransferido.subtract(total);
            if (excedente.compareTo(BigDecimal.ZERO) > 0) {
                efectivoDevuelto = excedente;
            }

            // Guardar campos de TRANSFERENCIA
            pago.setBanco(dto.getBanco());
            pago.setNOperacion(dto.getNOperacion());
            pago.setMontoTransferido(montoTransferido);
            pago.setEfectivoDevuelto(efectivoDevuelto);

            // En transferencia, no hay efectivo entregado ni vuelto "de caja"
            pago.setMontoEntregado(BigDecimal.ZERO);
            pago.setVuelto(BigDecimal.ZERO);

        } else {
            throw new RuntimeException("Método de pago no válido.");
        }





        // ===== Guardar pago para obtener ID =====
        // ✅ Asignar n_pago correlativo REAL + reintento si choca UNIQUE
        if (pago.getNPago() == null || pago.getNPago().trim().isEmpty() || "0".equals(pago.getNPago())) {

            int intentos = 0;

            while (true) {
                intentos++;
                if (intentos > 5) {
                    throw new RuntimeException("No se pudo asignar un número de pago único. Intenta nuevamente.");
                }

                Correlativo cor = correlativoRepository.findByClaveForUpdate("PAGO");
                if (cor == null) {
                    throw new RuntimeException("No existe correlativo configurado para PAGO.");
                }

                Long numero = cor.getSiguienteNumero();

                // Reservar siguiente número
                cor.setSiguienteNumero(numero + 1);
                correlativoRepository.save(cor);

                // Intentar asignar
                pago.setNPago(String.valueOf(numero));

                try {
                    // Forzar flush para validar UNIQUE acá
                    pagoRepository.saveAndFlush(pago);
                    break; // ✅ ok
                } catch (DataIntegrityViolationException ex) {
                    pago.setNPago(null);
                }
            }
        }





        // ===== Crear detalles + actualizar facturas =====
        List<DetallePago> detalles = new ArrayList<>();

        for (PagoDetalleCreateDTO det : dto.getDetalles()) {

            if (det.getFacturaId() == null) continue;
            BigDecimal monto = det.getMontoAplicado();
            if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) continue;

            Factura factura = facturaRepository.findById(det.getFacturaId())
                    .orElseThrow(() -> new RuntimeException("Factura no encontrada: " + det.getFacturaId()));




            // Validar que la factura pertenezca al cliente
            if (factura.getCliente() == null || !factura.getCliente().getId().equals(cliente.getId())) {
                throw new RuntimeException("La factura " + factura.getNumeroFactura() + " no pertenece al cliente seleccionado.");
            }





            BigDecimal saldoActual = factura.getSaldo();
            if (saldoActual == null) saldoActual = BigDecimal.ZERO;

            if (monto.compareTo(saldoActual) > 0) {
                throw new RuntimeException("Monto supera saldo en factura: " + factura.getNumeroFactura());
            }

            BigDecimal nuevoSaldo = saldoActual.subtract(monto);

            if (nuevoSaldo.compareTo(BigDecimal.ZERO) <= 0) {
                factura.setSaldo(BigDecimal.ZERO);
                factura.setEstadoPago("PAGADA");
            } else {
                factura.setSaldo(nuevoSaldo);
                factura.setEstadoPago("PARCIAL");
            }

            facturaRepository.save(factura);

            DetallePago dp = new DetallePago();
            dp.setPago(pago);
            dp.setFactura(factura);
            dp.setMontoAplicado(monto);

            detalles.add(dp);
        }

        if (detalles.isEmpty()) {
            throw new RuntimeException("No hay montos válidos para aplicar.");
        }

        // Guardar detalle_pago (cascade)
        if (pago.getDetalles() == null) {
            pago.setDetalles(new ArrayList<>());
        } else {
            pago.getDetalles().clear();
        }

        for (DetallePago dp : detalles) {
            dp.setPago(pago);      // relación bidireccional
            pago.getDetalles().add(dp);
        }

        pago = pagoRepository.save(pago);


        // Respuesta (refrescar)
        Pago pagoRefrescado = pagoRepository.findById(pago.getId())
                .orElseThrow(() -> new RuntimeException("Pago no encontrado tras guardar."));

        return new PagoResponseDTO(
                pagoRefrescado.getId(),
                pagoRefrescado.getNPago(),
                pagoRefrescado.getTotalPagado(),
                pagoRefrescado.getCreadoEn(),
                pagoRefrescado.getMetodo(),
                pagoRefrescado.getEstado()
        );
    }


    @Transactional(readOnly = true)
    public com.casaleo.sistema_pos.dto.PagoViewDTO obtenerPago(Integer pagoId) {

        Pago pago = pagoRepository.findById(pagoId)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado: " + pagoId));

        // Traer detalles
        List<DetallePago> detalles = detallePagoRepository.findByPago_Id(pagoId);

        List<com.casaleo.sistema_pos.dto.PagoDetalleResponseDTO> detDTO = new ArrayList<>();
        for (DetallePago d : detalles) {
            Factura f = d.getFactura();

            // OJO: acá uso campos que ya tenés en tu Factura JSON: id, numeroFactura, fechaEmision, total, saldo
            detDTO.add(new com.casaleo.sistema_pos.dto.PagoDetalleResponseDTO(
                    f.getId(),
                    f.getNumeroFactura(),
                    f.getFechaEmision() != null ? f.getFechaEmision().toString() : null,
                    f.getTotal(),
                    f.getSaldo(),
                    d.getMontoAplicado()
            ));
        }

        Cliente c = pago.getCliente();

        return new com.casaleo.sistema_pos.dto.PagoViewDTO(
                pago.getId(),
                pago.getNPago(),
                pago.getEstado(),
                pago.getCreadoEn(),
                pago.getMetodo(),
                c.getId(),
                c.getNombre(),
                c.getRuc(),
                pago.getBanco(),
                pago.getNOperacion(),
                pago.getTotalPagado(),
                pago.getMontoEntregado(),
                pago.getVuelto(),
                pago.getMontoTransferido(),
                pago.getEfectivoDevuelto(),
                detDTO
        );
    }




    @Transactional
    public void anularPago(Integer pagoId, String motivo) {

        Pago pago = pagoRepository.findById(pagoId)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado: " + pagoId));

        if (pago.getEstado() == com.casaleo.sistema_pos.models.EstadoPago.ANULADO) {
            throw new RuntimeException("El pago ya está ANULADO.");
        }

        List<DetallePago> detalles = detallePagoRepository.findByPago_Id(pagoId);

        if (detalles.isEmpty()) {
            throw new RuntimeException("El pago no tiene detalles para revertir.");
        }

        // Revertir saldos de facturas
        for (DetallePago d : detalles) {
            Factura factura = d.getFactura();
            BigDecimal monto = d.getMontoAplicado() == null ? BigDecimal.ZERO : d.getMontoAplicado();

            BigDecimal saldo = factura.getSaldo() == null ? BigDecimal.ZERO : factura.getSaldo();
            BigDecimal totalFactura = factura.getTotal() == null ? BigDecimal.ZERO : factura.getTotal();

            BigDecimal nuevoSaldo = saldo.add(monto);

            // No pasarse del total (por seguridad)
            if (totalFactura.compareTo(BigDecimal.ZERO) > 0 && nuevoSaldo.compareTo(totalFactura) > 0) {
                nuevoSaldo = totalFactura;
            }

            factura.setSaldo(nuevoSaldo);

            // Estado pago factura
            if (totalFactura.compareTo(BigDecimal.ZERO) > 0 && nuevoSaldo.compareTo(totalFactura) == 0) {
                factura.setEstadoPago("PENDIENTE");
            } else if (nuevoSaldo.compareTo(BigDecimal.ZERO) > 0) {
                factura.setEstadoPago("PARCIAL");
            } else {
                factura.setEstadoPago("PAGADA");
            }

            facturaRepository.save(factura);
        }

        // Marcar pago como anulado
        pago.setEstado(com.casaleo.sistema_pos.models.EstadoPago.ANULADO);
        pago.setAnuladoEn(java.time.LocalDateTime.now());
        pago.setMotivoAnulacion(motivo);

        pagoRepository.save(pago);
    }
}
