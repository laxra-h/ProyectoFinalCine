package org.example.model.compra.estadoCompra;

import org.example.model.compra.Compra;

public interface EstadoCompra {

    /**
     * Metodo para procesar el pago de una compra segun su estado actual
     * @param compra
     */
    public void pagar(Compra compra);

    /**
     * Metodo para cancelar una compra segun su estado actual
     * @param compra
     */
    public void cancelar(Compra compra);

    /**
     * Metodo para obtener el nombre del estado actual
     * @return
     */
    public String getNombre();

}
