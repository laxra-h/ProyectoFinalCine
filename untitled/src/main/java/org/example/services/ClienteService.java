package org.example.services;

import org.example.model.Cine;
import org.example.model.Ticket;
import org.example.model.compra.Compra;
import org.example.model.compra.estadoCompra.EstadoCancelada;
import org.example.model.compra.estadoCompra.EstadoPagada;
import org.example.model.compra.estrategiaReembolso.EstrategiaReembolso;
import org.example.model.funcionDecorator.EstadoFuncion;
import org.example.model.funcionDecorator.Funcion;
import org.example.model.salaPrototype.Asiento;
import org.example.model.usuarios.Cliente;

import java.util.List;

public class ClienteService {

    Cine cine = Cine.getInstance();

    /**
     * Metodo para realizar un reembolso a traves de la solicitud del cliente
     * @param cliente
     * @param compra
     */
    public void solicitarReembolso(Cliente cliente, Compra compra) {
        if (compra.getTheEstadoCompra() instanceof EstadoPagada) {
            EstrategiaReembolso theEstrategiaReembolso = compra.getTheEstrategiaReembolso();
            theEstrategiaReembolso.calcularReembolso(compra.getTotal());
            compra.setTheEstadoCompra(new EstadoCancelada());
        }

    }

    /**
     * Metodo paraa asignar asiento a un ticket
     * @param asiento
     * @param ticket
     */
    public void asignarAsiento (Asiento asiento, Ticket ticket) {
        ticket.setCodigo(asiento.getCodigo());

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
        c.definirEstrategiaReembolso();
        if (c.getListaTickets().get(0).getTheFuncion().getEstado() == EstadoFuncion.CANCELADA) {
            return c.calcularTotal();
        }
        return c.calcularTotalReembolso();
    }

    /**
     * Metodo para cancelar una compra gestionando su estado con el patron State
     * @param codigoCompra
     */
    public void cancelarCompra(String codigoCompra) {
        Compra c = buscarCompra(codigoCompra);
        if (c != null) {
            c.cancelar();
        }
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
