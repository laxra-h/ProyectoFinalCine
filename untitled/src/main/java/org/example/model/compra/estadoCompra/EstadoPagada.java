package org.example.model.compra.estadoCompra;

import org.example.exceptions.CancelacionNoPermitidaException;
import org.example.model.Ticket;
import org.example.model.compra.Compra;
import org.example.model.compra.estrategiaReembolso.ReembolsoCompleto;
import org.example.model.compra.estrategiaReembolso.SinReembolso;
import org.example.model.funcionDecorator.EstadoFuncion;
import org.example.model.usuarios.Cliente;

public class EstadoPagada implements EstadoCompra {

    /**
     * Una compra ya pagada no puede volver a pagarse
     * @param compra
     */
    @Override
    public void pagar(Compra compra) {
        // La compra ya se encuentra pagada, no se realiza accion
    }

    /**
     * Cancela una compra pagada aplicando la estrategia de reembolso correspondiente
     * y cambia su estado a reembolsada para impedir dobles reembolsos
     * @param compra
     * @throws CancelacionNoPermitidaException
     */
    @Override
    public void cancelar(Compra compra) throws CancelacionNoPermitidaException {
        boolean tieneFuncion = compra.getListaTickets() != null && !compra.getListaTickets().isEmpty()
                && compra.getListaTickets().get(0).getTheFuncion() != null;

        if (tieneFuncion && compra.getListaTickets().get(0).getTheFuncion().getEstado() == EstadoFuncion.CANCELADA) {
            compra.setTheEstrategiaReembolso(new ReembolsoCompleto());
        } else if (tieneFuncion) {
            compra.definirEstrategiaReembolso();
        } else {
            compra.setTheEstrategiaReembolso(new ReembolsoCompleto());
        }

        if (compra.getTheEstrategiaReembolso() instanceof SinReembolso) {
            throw new CancelacionNoPermitidaException("No es posible cancelar la compra " + compra.getCodigo()
                    + " (faltan menos de 2 horas para la función).");
        }

        double montoReembolso = compra.calcularTotalReembolso();

        Cliente cliente = compra.getTheCliente();
        if (cliente != null) {
            if (cliente.getTheTarjeta() != null) {
                cliente.getTheTarjeta().reembolsar(montoReembolso, "Reembolso compra " + compra.getCodigo());
            }
            cliente.reembolsarPuntosCompra(compra.getPuntosUsados(), compra.getPuntosCompra(), compra.getCodigo());
        }

        if (compra.getListaTickets() != null) {
            for (Ticket ticket : compra.getListaTickets()) {
                if (ticket.getTheFuncion() != null) {
                    ticket.getTheFuncion().liberarAsiento(ticket.getCodigoAsiento());
                }
            }
        }

        compra.cambiarEstado(new EstadoReembolsada());

        if (cliente != null) {
            cliente.actualizar("Compra " + compra.getCodigo() + " cancelada y reembolsada por $" + montoReembolso);
        }
    }

    @Override
    public String getNombre() {
        return "PAGADA";
    }
}
