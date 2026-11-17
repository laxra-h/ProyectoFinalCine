package org.example.model.compra.estrategiaReembolso;

import org.example.model.compra.Compra;

public class ReembolsoParcial implements  EstrategiaReembolso{


    /**
     * Metodo que calcula el reembolso parcial de una compra
     * @param valor
     * @return
     */
    @Override
    public double calcularReembolso(double valor) {
        int porcentaje = 80;
        return (valor*porcentaje)/100;
    }
}
