package com.casaleo.sistema_pos.controllers;

import com.casaleo.sistema_pos.models.Cliente;
import com.casaleo.sistema_pos.services.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    /**
     * Obtener clientes con paginación y búsqueda opcional por nombre o RUC.
     *
     * @param query  Nombre o RUC a buscar (opcional).
     * @param page   Número de página (por defecto 0).
     * @param size   Cantidad de clientes por página (por defecto 10).
     * @return Página de clientes.
     */
    @GetMapping
    public ResponseEntity<Page<Cliente>> obtenerClientes(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Cliente> clientes;
        if (query != null && !query.isEmpty()) {
            clientes = clienteService.buscarPorNombreORuc(query, page, size);
        } else {
            clientes = clienteService.obtenerClientesPaginados(page, size);
        }

        return ResponseEntity.ok(clientes);
    }

    /**
     * Obtener un cliente por ID.
     *
     * @param id ID del cliente a buscar.
     * @return Cliente encontrado o código 404 si no existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtenerClientePorId(@PathVariable int id) {
        return clienteService.buscarClientePorId(id)
                .map(cliente -> new ResponseEntity<>(cliente, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Crear un nuevo cliente.
     *
     * @param cliente Datos del cliente a registrar.
     * @return Cliente creado con código 201 o error 400 si los datos son inválidos.
     */
    @PostMapping
    public ResponseEntity<?> crearCliente(@RequestBody Cliente cliente) {
        try {
            Cliente nuevoCliente = clienteService.guardarCliente(cliente);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCliente);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al registrar el cliente.");
        }
    }

    /**
     * Actualizar un cliente existente.
     *
     * @param id      ID del cliente a actualizar.
     * @param cliente Datos actualizados del cliente.
     * @return Cliente actualizado con código 200 o error 404 si no existe.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCliente(@PathVariable int id, @RequestBody Cliente cliente) {
        try {
            Cliente clienteActualizado = clienteService.actualizarCliente(id, cliente);
            return clienteActualizado != null ? ResponseEntity.ok(clienteActualizado) : ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el cliente.");
        }
    }

    /**
     * Eliminar un cliente por ID.
     *
     * @param id ID del cliente a eliminar.
     * @return Código 204 si se eliminó correctamente, 404 si no se encontró.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable int id) {
        if (clienteService.eliminarCliente(id)) {
            return ResponseEntity.noContent().build(); // Código 204 si se eliminó con éxito
        } else {
            return ResponseEntity.notFound().build(); // Código 404 si no se encontró
        }
    }
}
