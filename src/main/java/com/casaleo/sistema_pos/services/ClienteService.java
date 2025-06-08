package com.casaleo.sistema_pos.services;

import com.casaleo.sistema_pos.models.Cliente;
import com.casaleo.sistema_pos.repositories.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * Obtener clientes con paginación.
     */
    public Page<Cliente> obtenerClientesPaginados(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return clienteRepository.findAll(pageable);
    }

    /**
     * Buscar clientes por nombre o RUC con paginación.
     */
    public Page<Cliente> buscarPorNombreORuc(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return clienteRepository.findByNombreContainingIgnoreCaseOrRucContaining(query, query, pageable);
    }

    /**
     * Buscar un cliente por ID.
     */
    public Optional<Cliente> buscarClientePorId(int id) {
        return clienteRepository.findById(id);
    }

    /**
     * Guardar un nuevo cliente después de validar los datos.
     */
    public Cliente guardarCliente(Cliente cliente) {
        validarCliente(cliente);

        // Validar unicidad del RUC si no es nulo
        if (cliente.getRuc() != null && clienteRepository.existsByRuc(cliente.getRuc())) {
            throw new IllegalArgumentException("El RUC ya está registrado.");
        }

        return clienteRepository.save(cliente);
    }

    /**
     * Actualizar un cliente existente después de validar los datos.
     */
    public Cliente actualizarCliente(int id, Cliente cliente) {
        if (!clienteRepository.existsById(id)) {
            return null; // Cliente no encontrado
        }

        validarCliente(cliente);

        // Validar que el RUC no esté en uso por otro cliente
        if (cliente.getRuc() != null) {
            Optional<Cliente> clienteExistente = clienteRepository.findById(id);
            if (clienteExistente.isPresent() && !clienteExistente.get().getRuc().equals(cliente.getRuc())) {
                if (clienteRepository.existsByRuc(cliente.getRuc())) {
                    throw new IllegalArgumentException("El RUC ya está registrado por otro cliente.");
                }
            }
        }

        cliente.setId(id);
        return clienteRepository.save(cliente);
    }

    /**
     * Eliminar un cliente por ID.
     */
    public boolean eliminarCliente(int id) {
        if (clienteRepository.existsById(id)) {
            clienteRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Método privado para validar datos de clientes antes de guardarlos o actualizarlos.
     */
    private void validarCliente(Cliente cliente) {
        if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente es obligatorio.");
        }
        if (cliente.getNombre().length() > 255) {
            throw new IllegalArgumentException("El nombre del cliente no puede tener más de 255 caracteres.");
        }
        if (cliente.getRuc() != null && cliente.getRuc().trim().length() > 20) {
            throw new IllegalArgumentException("El RUC no puede tener más de 20 caracteres.");
        }
        if (cliente.getTelefono() != null && cliente.getTelefono().trim().length() > 15) {
            throw new IllegalArgumentException("El teléfono no puede tener más de 15 caracteres.");
        }
        if (cliente.getDireccion() != null && cliente.getDireccion().trim().length() > 255) {
            throw new IllegalArgumentException("La dirección no puede tener más de 255 caracteres.");
        }
        if (cliente.getCiudad() != null && cliente.getCiudad().trim().length() > 100) {
            throw new IllegalArgumentException("La ciudad no puede tener más de 100 caracteres.");
        }
    }
}
