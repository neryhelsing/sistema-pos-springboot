package com.casaleo.sistema_pos.services;

import com.casaleo.sistema_pos.repositories.CorrelativoRepository;
import com.casaleo.sistema_pos.models.Correlativo;
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
import com.casaleo.sistema_pos.repositories.DetallePagoRepository;

import org.springframework.dao.DataIntegrityViolationException;
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
import java.math.BigDecimal;



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

    @Autowired
    private DetallePagoRepository detallePagoRepository;

    @Autowired
    private CorrelativoRepository correlativoRepository;


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
        String num = dto.getNumeroFactura();
        factura.setNumeroFactura((num == null || num.trim().isEmpty()) ? null : num.trim());
        factura.setFechaEmision(null); // BORRADOR: sin fecha de emisión
        factura.setTotal(dto.getTotal());
        factura.setSaldo(dto.getTotal());
        factura.setEstado("BORRADOR");
        factura.setEstadoPago("PENDIENTE");
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
        Factura facturaExistente = facturaRepository.findById(id).orElseThrow(() -> new RuntimeException("Factura no encontrada con ID: " + id));

        // ✅ BLOQUEO: si ya tiene pagos aplicados, no se permite modificar (aunque esté BORRADOR)
        BigDecimal pagado = obtenerMontoAplicado(id);
        if (pagado.compareTo(BigDecimal.ZERO) > 0) {
            throw new RuntimeException("No se puede actualizar la factura porque ya tiene pagos aplicados.");
        }


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

        // ✅ Como NO hay pagos (pagado = 0), el saldo debe ser igual al total
        facturaExistente.setSaldo(dto.getTotal());
        facturaExistente.setEstadoPago("PENDIENTE");


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




    public List<FacturaResponseDTO> buscarFacturasPendientesPorCliente(Integer clienteId) {
        List<Factura> pendientes = facturaRepository.findByCliente_IdAndSaldoGreaterThan(clienteId, BigDecimal.ZERO);
        return pendientes.stream()
                .map(FacturaResponseDTO::new)
                .toList();
    }







    @Transactional
    public Factura emitirCredito(Integer facturaId) {

        Factura f = facturaRepository.findById(facturaId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada con ID: " + facturaId));

        // Si ya está emitida, no repetir
        if ("EMITIDA".equalsIgnoreCase(f.getEstado())) {
            return f;
        }

        // Solo permitimos emitir si está en borrador
        if (!"BORRADOR".equalsIgnoreCase(f.getEstado())) {
            throw new RuntimeException("Solo se puede emitir una factura en estado BORRADOR.");
        }

        // ✅ Asignar número correlativo REAL + reintento si choca UNIQUE
        if (f.getNumeroFactura() == null || f.getNumeroFactura().trim().isEmpty() || "0".equals(f.getNumeroFactura())) {

            int intentos = 0;

            while (true) {
                intentos++;
                if (intentos > 5) {
                    throw new RuntimeException("No se pudo asignar un número de factura único. Intenta nuevamente.");
                }

                Correlativo cor = correlativoRepository.findByClaveForUpdate("FACTURA");
                if (cor == null) {
                    throw new RuntimeException("No existe correlativo configurado para FACTURA.");
                }

                Long numero = cor.getSiguienteNumero();

                // Reservar siguiente número
                cor.setSiguienteNumero(numero + 1);
                correlativoRepository.save(cor);

                // Intentar asignar
                f.setNumeroFactura(String.valueOf(numero));

                try {
                    // Forzamos flush para que el UNIQUE se valide acá dentro del loop
                    facturaRepository.saveAndFlush(f);
                    break; // ✅ ok, número reservado y guardado
                } catch (DataIntegrityViolationException ex) {
                    // chocó el UNIQUE (ej: alguien metió un numero manual, backup, etc.)
                    f.setNumeroFactura(null);
                }
            }
        }

        // ✅ Emitir a crédito
        f.setEstado("EMITIDA");
        f.setTipo("FCR"); // crédito
        f.setFechaEmision(new Date());

        // estado_pago no cambia por emitir, pero si está vacío lo dejamos en PENDIENTE
        if (f.getEstadoPago() == null || f.getEstadoPago().trim().isEmpty()) {
            f.setEstadoPago("PENDIENTE");
        }

        return facturaRepository.save(f);
    }



    @Transactional
    public Factura emitirContado(Integer facturaId) {

        Factura f = facturaRepository.findById(facturaId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada con ID: " + facturaId));

        // Si ya está emitida, no repetir
        if ("EMITIDA".equalsIgnoreCase(f.getEstado())) {
            return f;
        }

        // Solo permitimos emitir si está en borrador
        if (!"BORRADOR".equalsIgnoreCase(f.getEstado())) {
            throw new RuntimeException("Solo se puede emitir una factura en estado BORRADOR.");
        }

        // ✅ CONTADO: debe estar totalmente pagada
        if (f.getSaldo() == null || f.getSaldo().compareTo(BigDecimal.ZERO) != 0) {
            throw new RuntimeException("Para emitir en FCC (contado) el saldo debe ser 0 (pago completo).");
        }

        // ✅ Asignar número correlativo REAL + reintento si choca UNIQUE
        if (f.getNumeroFactura() == null || f.getNumeroFactura().trim().isEmpty() || "0".equals(f.getNumeroFactura())) {

            int intentos = 0;

            while (true) {
                intentos++;
                if (intentos > 5) {
                    throw new RuntimeException("No se pudo asignar un número de factura único. Intenta nuevamente.");
                }

                Correlativo cor = correlativoRepository.findByClaveForUpdate("FACTURA");
                if (cor == null) {
                    throw new RuntimeException("No existe correlativo configurado para FACTURA.");
                }

                Long numero = cor.getSiguienteNumero();

                // Reservar siguiente número
                cor.setSiguienteNumero(numero + 1);
                correlativoRepository.save(cor);

                // Intentar asignar
                f.setNumeroFactura(String.valueOf(numero));

                try {
                    facturaRepository.saveAndFlush(f);
                    break; // ✅ ok
                } catch (DataIntegrityViolationException ex) {
                    f.setNumeroFactura(null);
                }
            }
        }

        // ✅ Emitir en contado
        f.setEstado("EMITIDA");
        f.setTipo("FCC");
        f.setFechaEmision(new Date());

        // Si saldo es 0, dejamos PAGADA sí o sí
        f.setEstadoPago("PAGADA");

        return facturaRepository.save(f);
    }





    public Optional<Factura> obtenerPorId(Integer id) {
        return facturaRepository.findById(id);
    }



    public BigDecimal obtenerMontoAplicado(Integer facturaId) {
        BigDecimal pagado = detallePagoRepository.sumMontoAplicadoByFacturaId(facturaId);
        return pagado == null ? BigDecimal.ZERO : pagado;
    }
}
