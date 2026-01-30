package com.casaleo.sistema_pos.controllers;

import com.casaleo.sistema_pos.dto.FacturaDTO;
import com.casaleo.sistema_pos.dto.DetalleFacturaDTO;
import com.casaleo.sistema_pos.models.Factura;
import com.casaleo.sistema_pos.models.DetalleFactura;
import com.casaleo.sistema_pos.dto.FacturaResponseDTO;
import com.casaleo.sistema_pos.services.FacturaService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.ArrayList;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/facturas")
@CrossOrigin(origins = "*")
public class FacturaController {

    @Autowired
    private FacturaService facturaService;

    @GetMapping
    public Page<FacturaResponseDTO> listarFacturasResumen(
            @RequestParam(defaultValue = "") String query,
            Pageable pageable
    ) {
        return facturaService.listarDTO(query, pageable);
    }


    @PostMapping
    public ResponseEntity<Integer> crearFactura(@RequestBody FacturaDTO dto) {
        Factura factura = facturaService.guardarFactura(dto);
        return ResponseEntity.ok(factura.getId());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarFactura(@PathVariable Integer id, @RequestBody FacturaDTO facturaDTO) {
        try {
            facturaService.actualizarFactura(id, facturaDTO);
            return ResponseEntity.ok().build(); // ✅ Confirmación sin contenido extra
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar la factura: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<FacturaDTO> obtenerFacturaPorId(@PathVariable Integer id) {
        Factura factura = facturaService.obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

        FacturaDTO dto = new FacturaDTO();
        dto.setNumeroFactura(factura.getNumeroFactura());
        dto.setFechaEmision(factura.getFechaEmision());
        dto.setClienteId(factura.getCliente().getId());
        dto.setTotal(factura.getTotal());
        dto.setSaldo(factura.getSaldo()); // ✅ agregado
        dto.setMontoAplicado(facturaService.obtenerMontoAplicado(id)); // ✅ NUEVO (Pagado)
        dto.setEstado(factura.getEstado());
        dto.setEstadoPago(factura.getEstadoPago());
        dto.setTipo(factura.getTipo());

        List<DetalleFacturaDTO> detallesDTO = new ArrayList<>();
        for (DetalleFactura detalle : factura.getDetalles()) {
            DetalleFacturaDTO det = new DetalleFacturaDTO();
            det.setProductoId(detalle.getProducto().getId());
            det.setCantidad(detalle.getCantidad());
            det.setPrecioUnitario(detalle.getPrecioUnitario());
            detallesDTO.add(det);
        }

        dto.setDetalles(detallesDTO);
        return ResponseEntity.ok(dto);
    }




    @GetMapping("/pendientes")
    public ResponseEntity<List<FacturaResponseDTO>> listarFacturasPendientes(@RequestParam Integer clienteId) {
        return ResponseEntity.ok(facturaService.buscarFacturasPendientesPorCliente(clienteId));
    }



    @PostMapping("/{id}/emitir-credito")
    public ResponseEntity<?> emitirCredito(@PathVariable Integer id) {
        try {
            Factura factura = facturaService.emitirCredito(id);
            return ResponseEntity.ok(factura.getNumeroFactura());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }



    @PostMapping("/{id}/emitir-contado")
    public ResponseEntity<?> emitirContado(@PathVariable Integer id) {
        try {
            Factura factura = facturaService.emitirContado(id);
            return ResponseEntity.ok(factura.getNumeroFactura());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
}
