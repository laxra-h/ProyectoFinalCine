package org.example.model.compra.estrategiaReembolso;

import org.example.model.Ticket;
import org.example.model.compra.Compra;

import java.util.List;

public class ReembolsoCompleto implements EstrategiaReembolso {


    /**
     * Metodo que calcula el valor completo del reembolso
     * @param valor
     * @return
     */
    @Override
    public double calcularReembolso(double valor) {
        return valor;
    }

}
