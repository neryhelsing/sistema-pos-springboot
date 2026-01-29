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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                p.getMetodo()
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
        pago.setNPago("0"); // temporal
        pago = pagoRepository.save(pago);

        // correlativo simple (más adelante formateás tipo REC-000001)
        pago.setNPago(String.valueOf(pago.getId()));
        pago = pagoRepository.save(pago);

        // ===== Crear detalles + actualizar facturas =====
        List<DetallePago> detalles = new ArrayList<>();

        for (PagoDetalleCreateDTO det : dto.getDetalles()) {

            if (det.getFacturaId() == null) continue;
            BigDecimal monto = det.getMontoAplicado();
            if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) continue;

            Factura factura = facturaRepository.findById(det.getFacturaId())
                    .orElseThrow(() -> new RuntimeException("Factura no encontrada: " + det.getFacturaId()));

            // Validar que la factura pertenezca al cliente
            if (factura.getCliente() != null && factura.getCliente().getId() != cliente.getId()) {
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
        pago.setDetalles(detalles);
        pago = pagoRepository.save(pago);

        // Respuesta (refrescar)
        Pago pagoRefrescado = pagoRepository.findById(pago.getId())
                .orElseThrow(() -> new RuntimeException("Pago no encontrado tras guardar."));

        return new PagoResponseDTO(
                pagoRefrescado.getId(),
                pagoRefrescado.getNPago(),
                pagoRefrescado.getTotalPagado(),
                pagoRefrescado.getCreadoEn(),
                pagoRefrescado.getMetodo()
        );
    }
}
