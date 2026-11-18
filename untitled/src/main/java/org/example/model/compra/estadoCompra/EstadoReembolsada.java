package org.example.model.compra.estadoCompra;

import org.example.exceptions.CancelacionNoPermitidaException;
import org.example.model.compra.Compra;

public class EstadoReembolsada implements EstadoCompra {

    /**
     * Una compra reembolsada no puede volver a pagarse
     * @param compra
     */
    @Override
    public void pagar(Compra compra) {
        // La compra ya fue reembolsada
    }

    /**
     * Impide que se ejecute un doble reembolso sobre la misma compra
     * @param compra
     */
    @Override
    public void cancelar(Compra compra) throws CancelacionNoPermitidaException {
        throw new CancelacionNoPermitidaException("La compra " + compra.getCodigo() + " ya fue reembolsada.");
    }

    @Override
    public String getNombre() {
        return "REEMBOLSADA";
    }
}
