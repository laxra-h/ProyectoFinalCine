package org.example;

import org.example.model.Ticket;
import org.example.model.compra.Compra;
import org.example.model.funcionDecorator.EstadoFuncion;
import org.example.model.funcionDecorator.Funcion;
import org.example.model.funcionDecorator.FuncionBase;
import org.example.model.pelicula.ClasificacionPelicula;
import org.example.model.pelicula.GeneroPelicula;
import org.example.model.pelicula.Pelicula;
import org.example.model.salaPrototype.Sala;
import org.example.model.usuarios.Cliente;
import org.example.model.usuarios.RegistroPuntos;
import org.example.services.CompraServices;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PuntosVigenciaTest {

    @Test
    void puntosVencidosNoSeCuentanNiSePuedenRedimir() {
        Cliente cliente = new Cliente.Builder("P-1", "Test", "123", 0).build();
        LocalDate haceMasDeUnAno = LocalDate.now().minusYears(1).minusDays(1);
        LocalDate vigente = LocalDate.now().minusMonths(2);

        cliente.agregarPuntos(300, haceMasDeUnAno, "C-VENCIDA");
        cliente.agregarPuntos(150, vigente, "C-VIGENTE");

        assertEquals(150, cliente.getPuntos());
        assertFalse(cliente.redimirPuntos(200));
        assertEquals(150, cliente.getPuntos());
    }

    @Test
    void redimirConsumePrimeroLosPuntosMasProximosAVencer() {
        Cliente cliente = new Cliente.Builder("P-2", "Test", "123", 0).build();
        LocalDate masAntigua = LocalDate.now().minusMonths(10);
        LocalDate masReciente = LocalDate.now().minusMonths(1);

        // Se agregan desordenados para comprobar el orden por fecha de vencimiento
        cliente.agregarPuntos(200, masReciente, "C-NUEVA");
        cliente.agregarPuntos(100, masAntigua, "C-VIEJA");

        assertTrue(cliente.redimirPuntos(120));
        assertEquals(180, cliente.getPuntos());

        RegistroPuntos nueva = cliente.getHistorialPuntos().get(0);
        RegistroPuntos vieja = cliente.getHistorialPuntos().get(1);

        assertEquals(0, vieja.getPuntosDisponibles(), "Debe agotarse primero el registro mas proximo a vencer");
        assertEquals(180, nueva.getPuntosDisponibles());
    }

    @Test
    void compraYCancelacionActualizanHistorialDePuntos() throws Exception {
        Sala sala = new Sala("Sala P");
        sala.creacionAsientos();
        Pelicula pelicula = new Pelicula("Test", "Dir", "Desc", ClasificacionPelicula.TODOS, GeneroPelicula.COMEDIA, 90);
        Funcion funcion = new FuncionBase("FP-1", pelicula, sala, LocalDateTime.now().plusDays(3), EstadoFuncion.PROGRAMADA);

        Cliente cliente = new Cliente.Builder("P-" + UUID.randomUUID(), "Test", "123", 0).build();
        cliente.getTheTarjeta().recargar(100000);
        cliente.agregarPuntos(50, LocalDate.now().minusMonths(6), "ANTERIOR");

        Compra compra = new Compra("CP-1", LocalDate.now(), 0, 0);
        compra.asignarCliente(cliente);
        Ticket ticket = new Ticket("CP-1-T1", "A1");
        ticket.setTheFuncion(funcion);
        compra.addTicket(ticket);
        compra.setUsaPuntos(true);
        compra.setTotal(compra.calcularTotal());

        CompraServices compraServices = new CompraServices();
        compraServices.procesarCompra(compra);

        assertEquals(compra.getPuntosCompra(), cliente.getPuntos());

        compra.cancelar();
        assertEquals(50, cliente.getPuntos());
    }
}
