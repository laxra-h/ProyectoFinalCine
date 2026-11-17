package org.example.model.compra.estadoCompra;

import org.example.model.compra.Compra;

public class EstadoCancelada implements EstadoCompra {

    /**
     * Una compra cancelada no puede ser pagada
     * @param compra
     */
    @Override
    public void pagar(Compra compra) {
        // La compra esta cancelada, no se puede pagar
    }

    /**
     * Una compra cancelada no puede volver a cancelarse
     * @param compra
     */
    @Override
    public void cancelar(Compra compra) {
        // La compra ya esta cancelada
    }

    @Override
    public String getNombre() {
        return "CANCELADA";
    }
}
