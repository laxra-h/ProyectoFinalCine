package org.example.services;

import org.example.exceptions.CancelacionNoPermitidaException;
import org.example.model.Cine;
import org.example.model.consumibleComposite.Ticket;
import org.example.model.compra.Compra;
import org.example.model.compra.estadoCompra.EstadoPagada;
import org.example.model.compra.estrategiaReembolso.SinReembolso;
import org.example.model.funcionDecorator.EstadoFuncion;
import org.example.model.salaPrototype.Asiento;
import org.example.model.usuarios.Cliente;

public class ClienteService {

    Cine cine = Cine.getInstance();

    /**
     * Metodo para realizar un reembolso a traves de la solicitud del cliente
     * @param cliente
     * @param compra
     * @return
     */
    public boolean solicitarReembolso(Cliente cliente, Compra compra) {
        try {
            validarCancelacion(cliente, compra);
        } catch (CancelacionNoPermitidaException e) {
            return false;
        }
        return cine.registrarSolicitudReembolso(compra);
    }

    /**
     * Metodo para validar si una compra puede ser cancelada
     * @param cliente
     * @param compra
     * @throws CancelacionNoPermitidaException
     */
    public void validarCancelacion(Cliente cliente, Compra compra) throws CancelacionNoPermitidaException {
        if (cliente == null || compra == null) {
            throw new CancelacionNoPermitidaException("Datos incompletos para la solicitud.");
        }
        if (compra.getTheCliente() != cliente) {
            throw new CancelacionNoPermitidaException("La compra " + compra.getCodigo() + " no pertenece al cliente.");
        }
        if (!(compra.getTheEstadoCompra() instanceof EstadoPagada)) {
            throw new CancelacionNoPermitidaException("Solo se pueden cancelar compras pagadas.");
        }

        boolean funcionCancelada = !compra.getListaTickets().isEmpty()
                && compra.getListaTickets().get(0).getTheFuncion() != null
                && compra.getListaTickets().get(0).getTheFuncion().getEstado() == EstadoFuncion.CANCELADA;

        compra.definirEstrategiaReembolso();
        if (!funcionCancelada && compra.getTheEstrategiaReembolso() instanceof SinReembolso) {
            throw new CancelacionNoPermitidaException("No es posible cancelar la compra " + compra.getCodigo()
                    + " (faltan menos de 2 horas para la función).");
        }
    }

    /**
     * Metodo paraa asignar asiento a un ticket
     * @param asiento
     * @param ticket
     */
    public void asignarAsiento (Asiento asiento, Ticket ticket) {
        ticket.setCodigoAsiento(asiento.getCodigo());
    }

    /**
     * Metodo para consultar el saldo de la tarjeta virtual del cliente
     * @param cliente
     * @return
     */
    public double consultarSaldoTarjeta(Cliente cliente) {
       return cliente.getTheTarjeta().getSaldo();
    }

    /**
     * Metodo para calcular el costo total del reembolso de una solicitud
     * @param codigoCompra
     * @return
     */
    public double calcularTotalSolicitud(String codigoCompra) {
        Compra c = buscarCompra(codigoCompra);
        if (c == null) {
            return 0;
        }
        c.definirEstrategiaReembolso();
        if (!c.getListaTickets().isEmpty() && c.getListaTickets().get(0).getTheFuncion() != null
                && c.getListaTickets().get(0).getTheFuncion().getEstado() == EstadoFuncion.CANCELADA) {
            return c.getMontoPagado();
        }
        return c.calcularTotalReembolso();
    }

    /**
     * Metodo para cancelar una compra gestionando su estado con el patron State
     * @param codigoCompra
     * @throws CancelacionNoPermitidaException
     */
    public void cancelarCompra(String codigoCompra) throws CancelacionNoPermitidaException {
        Compra c = buscarCompra(codigoCompra);
        if (c == null) {
            throw new CancelacionNoPermitidaException("No existe la compra " + codigoCompra);
        }
        c.cancelar();
    }

    /**
     * Metodo para buscar una compra
     * @param codigoCompra
     * @return
     */
    public Compra buscarCompra(String codigoCompra){
        for (Compra c : cine.getListCompras()){
            if (c.getCodigo().equals(codigoCompra)){
                return c;
            }
        }
        return null;
    }

}
