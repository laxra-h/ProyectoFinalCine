package org.example.model.compra.estrategiaReembolso;

import org.example.model.compra.Compra;

public class SinReembolso implements EstrategiaReembolso{

    /**
     * Metodo que calcula el valor sin reembolso
     * @param compra
     * @return
     */
    @Override
    public double calcularReembolso(double compra) {
        return 0;
    }
}
