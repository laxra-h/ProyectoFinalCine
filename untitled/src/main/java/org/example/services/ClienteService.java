package org.example.services;

import org.example.model.Cine;
import org.example.model.compra.Compra;
import org.example.model.compra.estrategiaReembolso.EstrategiaReembolso;
import org.example.model.funcionDecorator.EstadoFuncion;

public class ClienteService {

    Cine cine = Cine.getInstance();


    public double calcularTotalSolicitud(String codigoCompra) {
        Compra c = buscarCompra(codigoCompra);
        c.definirEstrategiaReembolso(c);
        if (c.getListaTickets().get(0).getTheFuncion().getEstado() == EstadoFuncion.CANCELADA) {
            return c.calcularTotal();
        }
        return c.calcularTotalReembolso();
    }

    public void


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
