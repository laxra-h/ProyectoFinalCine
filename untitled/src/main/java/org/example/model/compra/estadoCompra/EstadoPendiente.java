package org.example.model.compra.estadoCompra;

import org.example.model.Ticket;
import org.example.model.compra.Compra;
import org.example.model.usuarios.Cliente;

public class EstadoPendiente implements EstadoCompra {

    /**
     * Metodo para pasar la compra de pendiente a pagada y suscribir/notificar al cliente
     * @param compra
     */
    @Override
    public void pagar(Compra compra) {
        compra.cambiarEstado(new EstadoPagada());

        Cliente cliente = compra.getTheCliente();
        if (cliente != null) {
            if (compra.getListaTickets() != null) {
                for (Ticket ticket : compra.getListaTickets()) {
                    if (ticket.getTheFuncion() != null) {
                        ticket.getTheFuncion().agregarObservador(cliente);
                    }
                }
            }
            cliente.actualizar("Compra " + compra.getCodigo() + " pagada exitosamente.");
        }
    }

    /**
     * Metodo para cancelar una compra que aun estaba pendiente de pago
     * @param compra
     */
    @Override
    public void cancelar(Compra compra) {
        compra.cambiarEstado(new EstadoCancelada());

        Cliente cliente = compra.getTheCliente();
        if (cliente != null) {
            cliente.actualizar("Compra " + compra.getCodigo() + " ha sido cancelada.");
        }
    }

    @Override
    public String getNombre() {
        return "PENDIENTE";
    }
}
