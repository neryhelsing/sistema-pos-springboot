package com.casaleo.sistema_pos.services;

import com.casaleo.sistema_pos.dto.FacturaDTO;
import com.casaleo.sistema_pos.dto.DetalleFacturaDTO;
import com.casaleo.sistema_pos.dto.FacturaResponseDTO;
import com.casaleo.sistema_pos.models.Factura;
import com.casaleo.sistema_pos.models.Cliente;
import com.casaleo.sistema_pos.models.Producto;
import com.casaleo.sistema_pos.models.DetalleFactura;
import com.casaleo.sistema_pos.repositories.ClienteRepository;
import com.casaleo.sistema_pos.repositories.ProductoRepository;
import com.casaleo.sistema_pos.repositories.FacturaRepository;
import com.casaleo.sistema_pos.repositories.DetalleFacturaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;

import java.util.Date;
import java.util.Objects;



@Service
public class FacturaService {

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private DetalleFacturaRepository detalleFacturaRepository;

    public Page<FacturaResponseDTO> listarDTO(String query, Pageable pageable) {

        String q = (query == null) ? "" : query.trim();

        Page<Factura> facturas;

        if (q.isEmpty()) {
            facturas = facturaRepository.findAll(pageable);
        } else {
            facturas = facturaRepository
                    .findByNumeroFacturaContainingIgnoreCaseOrCliente_NombreContainingIgnoreCaseOrCliente_RucContainingIgnoreCase(
                            q, q, q, pageable
                    );
        }

        List<FacturaResponseDTO> dtoList = facturas.getContent().stream()
                .map(f -> new FacturaResponseDTO(
                        f.getId(),
                        f.getNumeroFactura(),
                        f.getFechaEmision(),
                        f.getTotal(),
                        f.getSaldo(),
                        f.getEstado(),
                        f.getCliente().getNombre(),
                        f.getCreadoEn()
                ))
                .toList();

        return new PageImpl<>(dtoList, pageable, facturas.getTotalElements());
    }


    @Transactional
    public Factura guardarFactura(FacturaDTO dto) {
        Factura factura = new Factura();
        factura.setNumeroFactura(dto.getNumeroFactura());
        factura.setFechaEmision(dto.getFechaEmision());
        factura.setTotal(dto.getTotal());
        factura.setSaldo(dto.getTotal());
        factura.setEstado("BORRADOR");
        factura.setTipo(dto.getTipo());

        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        factura.setCliente(cliente);

        Factura facturaGuardada = facturaRepository.save(factura);

        for (DetalleFacturaDTO detalle : dto.getDetalles()) {
            Producto producto = productoRepository.findById(detalle.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            DetalleFactura det = new DetalleFactura();
            det.setFactura(facturaGuardada);
            det.setProducto(producto);
            det.setCantidad(detalle.getCantidad());
            det.setPrecioUnitario(detalle.getPrecioUnitario());

            detalleFacturaRepository.save(det);
        }

        return facturaGuardada;
    }




    @Transactional
    public Factura actualizarFactura(Integer id, FacturaDTO dto) {
        // Buscar factura existente
        Factura facturaExistente = facturaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada con ID: " + id));

        // Detectar cambios
        boolean clienteCambiado = !Objects.equals(facturaExistente.getCliente().getId(), dto.getClienteId());
        boolean tipoCambiado = !Objects.equals(facturaExistente.getTipo(), dto.getTipo());
        boolean detallesCambiados = true; // por ahora, siempre consideramos cambios

        if (!clienteCambiado && !tipoCambiado && !detallesCambiados) {
            return facturaExistente;
        }

        // Actualizar datos principales
        facturaExistente.setTipo(dto.getTipo());
        facturaExistente.setCliente(new Cliente(dto.getClienteId()));
        facturaExistente.setTotal(dto.getTotal());
        facturaExistente.setFechaEmision(new Date());

        // Obtener detalles actuales
        List<DetalleFactura> detallesActuales = facturaExistente.getDetalles();
        List<DetalleFacturaDTO> detallesNuevos = dto.getDetalles();

        // Eliminar los detalles que ya no están
        detallesActuales.removeIf(detalleExistente -> {
            Integer productoId = detalleExistente.getProducto().getId();
            Optional<DetalleFacturaDTO> match = detallesNuevos.stream()
                    .filter(dtoDet -> Objects.equals(dtoDet.getProductoId(), productoId))
                    .findFirst();

            if (match.isPresent()) {
                // Actualizar cantidad o precio si cambiaron
                DetalleFacturaDTO nuevo = match.get();
                detalleExistente.setCantidad(nuevo.getCantidad());
                detalleExistente.setPrecioUnitario(nuevo.getPrecioUnitario());
                return false; // No eliminar
            } else {
                // Eliminar si no está en la nueva lista
                detalleFacturaRepository.delete(detalleExistente);
                return true; // Eliminar de la lista en memoria
            }
        });

        // Agregar nuevos detalles
        for (DetalleFacturaDTO nuevo : detallesNuevos) {
            boolean yaExiste = detallesActuales.stream()
                    .anyMatch(d -> Objects.equals(d.getProducto().getId(), nuevo.getProductoId()));

            if (!yaExiste) {
                DetalleFactura nuevoDetalle = new DetalleFactura();
                nuevoDetalle.setFactura(facturaExistente);
                nuevoDetalle.setProducto(new Producto(nuevo.getProductoId()));
                nuevoDetalle.setCantidad(nuevo.getCantidad());
                nuevoDetalle.setPrecioUnitario(nuevo.getPrecioUnitario());
                detallesActuales.add(nuevoDetalle);
            }
        }

        return facturaRepository.save(facturaExistente);
    }




    public List<FacturaResponseDTO> buscarFacturasPendientesPorCliente(Long clienteId) {
        List<Factura> pendientes = facturaRepository.findByCliente_IdAndSaldoGreaterThan(clienteId, 0.0);
        return pendientes.stream()
                .map(FacturaResponseDTO::new)
                .toList();
    }






    public Optional<Factura> obtenerPorId(Integer id) {
        return facturaRepository.findById(id);
    }
}
