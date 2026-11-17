package org.example.model.compra.estadoCompra;

import org.example.model.Ticket;
import org.example.model.compra.Compra;
import org.example.model.compra.estrategiaReembolso.ReembolsoCompleto;
import org.example.model.compra.estrategiaReembolso.SinReembolso;
import org.example.model.funcionDecorator.EstadoFuncion;
import org.example.model.salaPrototype.Asiento;
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
     */
    @Override
    public void cancelar(Compra compra) {
        if (compra.getListaTickets() != null && !compra.getListaTickets().isEmpty()
                && compra.getListaTickets().get(0).getTheFuncion() != null) {

            if (compra.getListaTickets().get(0).getTheFuncion().getEstado() == EstadoFuncion.CANCELADA) {
                compra.setTheEstrategiaReembolso(new ReembolsoCompleto());
            }

            if (compra.getTheEstrategiaReembolso() instanceof SinReembolso) {
                if (compra.getTheCliente() != null) {
                    compra.getTheCliente().actualizar("No es posible cancelar la compra " + compra.getCodigo() + " (faltan menos de 2 horas para la función).");
                }
                return;
            }
        }

        double montoReembolso = (compra.getTheEstrategiaReembolso() != null)
                ? compra.calcularTotalReembolso()
                : compra.getTotal();

        Cliente cliente = compra.getTheCliente();
        if (cliente != null && cliente.getTheTarjeta() != null) {
            cliente.getTheTarjeta().setSaldo(cliente.getTheTarjeta().getSaldo() + montoReembolso);
        }

        if (compra.getListaTickets() != null) {
            for (Ticket ticket : compra.getListaTickets()) {
                if (ticket.getTheFuncion() != null && ticket.getTheFuncion().getSala() != null
                        && ticket.getTheFuncion().getSala().getListAsientos() != null) {
                    for (Asiento asiento : ticket.getTheFuncion().getSala().getListAsientos()) {
                        if (asiento != null && asiento.getCodigo().equals(ticket.getCodigoAsiento())) {
                            asiento.setDisponible(true);
                        }
                    }
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
