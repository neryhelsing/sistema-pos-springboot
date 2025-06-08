package com.casaleo.sistema_pos;

import com.casaleo.sistema_pos.models.Cliente;
import com.casaleo.sistema_pos.services.ClienteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ClienteServiceTest {

    @Autowired
    private ClienteService clienteService;

    @Test
    public void testGuardarCliente() {
        // Crear un cliente
        Cliente cliente = new Cliente();
        cliente.setNombre("Lucia Espinoza");
        cliente.setRuc("654321");
        cliente.setTelefono("0993456455");
        cliente.setDireccion("Ruta 12 Chaco");

        // Guardar cliente
        Cliente clienteGuardado = clienteService.guardarCliente(cliente);

        // Verificar que el cliente se guardó
        assertNotNull(clienteGuardado.getId());
        assertEquals("Lucia Espinoza", clienteGuardado.getNombre());
    }

    @Test
    public void testObtenerTodosLosClientes() {
        // Obtener la lista de clientes
        List<Cliente> clientes = clienteService.obtenerTodosLosClientes();

        // Verificar que la lista no es nula
        assertNotNull(clientes);
    }
}
