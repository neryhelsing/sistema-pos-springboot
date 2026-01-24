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

@Service
public class PagoService {

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    public Page<PagoResponseDTO> listarPagos(String query, Pageable pageable) {
        Page<Pago> pagos = pagoRepository.findByNPagoContainingIgnoreCase(query, pageable);

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

        // ✅ Logs temporales para debug (podés borrar después)
        System.out.println("=== CREAR PAGO ===");
        System.out.println("clienteId: " + dto.getClienteId());
        System.out.println("metodo: " + dto.getMetodo());
        System.out.println("banco: '" + dto.getBanco() + "'");
        System.out.println("nOperacion: '" + dto.getNOperacion() + "'");

        // Si es transferencia, banco y nOperacion deben venir
        if (dto.getMetodo() == MetodoPago.TRANSFERENCIA) {
            if (dto.getBanco() == null || dto.getBanco().trim().isEmpty()) {
                throw new RuntimeException("Debe seleccionar banco para transferencia.");
            }
            if (dto.getNOperacion() == null || dto.getNOperacion().trim().isEmpty()) {
                throw new RuntimeException("Debe indicar el N° de operación para transferencia.");
            }
        }

        // ===== Buscar cliente =====
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado: " + dto.getClienteId()));

        // ===== Calcular total desde detalles =====
        double total = dto.getDetalles().stream()
                .mapToDouble(d -> d.getMontoAplicado() == null ? 0 : d.getMontoAplicado())
                .sum();

        if (total <= 0) {
            throw new RuntimeException("El total pagado debe ser mayor a 0.");
        }

        // ===== Crear pago (sin nPago definitivo todavía) =====
        Pago pago = new Pago();
        pago.setCliente(cliente);
        pago.setMetodo(dto.getMetodo());
        pago.setBanco(dto.getBanco());
        pago.setNOperacion(dto.getNOperacion());
        pago.setTotalPagado(total);

        pago.setNPago("0"); // temporal
        pago = pagoRepository.save(pago);

        // correlativo simple
        pago.setNPago(String.valueOf(pago.getId()));
        pago = pagoRepository.save(pago);

        // ===== Crear detalles + actualizar facturas =====
        List<DetallePago> detalles = new ArrayList<>();

        for (PagoDetalleCreateDTO det : dto.getDetalles()) {

            if (det.getFacturaId() == null) continue;
            if (det.getMontoAplicado() == null || det.getMontoAplicado() <= 0) continue;

            Factura factura = facturaRepository.findById(det.getFacturaId())
                    .orElseThrow(() -> new RuntimeException("Factura no encontrada: " + det.getFacturaId()));

            if (factura.getCliente() != null && factura.getCliente().getId() != cliente.getId()) {
                throw new RuntimeException("La factura " + factura.getNumeroFactura() + " no pertenece al cliente seleccionado.");
            }

            if (det.getMontoAplicado() > factura.getSaldo()) {
                throw new RuntimeException("Monto supera saldo en factura: " + factura.getNumeroFactura());
            }

            double nuevoSaldo = factura.getSaldo() - det.getMontoAplicado();
            if (nuevoSaldo <= 0) {
                factura.setSaldo(0.0);
                factura.setEstado("PAGADA");
            } else {
                factura.setSaldo(nuevoSaldo);
            }

            facturaRepository.save(factura);

            DetallePago dp = new DetallePago();
            dp.setPago(pago);
            dp.setFactura(factura);
            dp.setMontoAplicado(det.getMontoAplicado());

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
