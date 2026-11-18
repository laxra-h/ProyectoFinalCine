package org.example.model.compra.estadoCompra;

import org.example.exceptions.CancelacionNoPermitidaException;
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
    public void cancelar(Compra compra) throws CancelacionNoPermitidaException {
        throw new CancelacionNoPermitidaException("La compra " + compra.getCodigo() + " ya se encuentra cancelada.");
    }

    @Override
    public String getNombre() {
        return "CANCELADA";
    }
}
