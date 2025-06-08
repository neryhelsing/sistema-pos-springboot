package com.casaleo.sistema_pos;

import com.casaleo.sistema_pos.models.Cliente;
import com.casaleo.sistema_pos.services.ClienteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

@SpringBootTest
public class ClienteCrudTest {

    @Autowired
    private ClienteService clienteService;

    @Test
    public void testCrudOperations() {
        // Crear un cliente
        Cliente cliente = new Cliente();
        cliente.setNombre("Juan Pérez");
        cliente.setRuc("1234567890");
        cliente.setTelefono("0987654321");
        cliente.setDireccion("Calle Falsa 123");

        Cliente clienteGuardado = clienteService.guardarCliente(cliente);
        assertNotNull(clienteGuardado.getId(), "El cliente debería haberse guardado con un ID.");

        // Leer todos los clientes
        assertFalse(clienteService.obtenerTodosLosClientes().isEmpty(), "Debería haber al menos un cliente guardado.");

        // Buscar por ID
        Optional<Cliente> clienteBuscado = clienteService.buscarClientePorId(clienteGuardado.getId());
        assertTrue(clienteBuscado.isPresent(), "El cliente debería existir en la base de datos.");
        assertEquals("Juan Pérez", clienteBuscado.get().getNombre(), "El nombre del cliente debería coincidir.");

        // Actualizar cliente
        clienteGuardado.setTelefono("0999999999");
        Cliente clienteActualizado = clienteService.guardarCliente(clienteGuardado);
        assertEquals("0999999999", clienteActualizado.getTelefono(), "El teléfono del cliente debería haberse actualizado.");

        // Eliminar cliente
        clienteService.eliminarClientePorId(clienteGuardado.getId());
        assertTrue(clienteService.buscarClientePorId(clienteGuardado.getId()).isEmpty(), "El cliente debería haberse eliminado.");
    }
}
