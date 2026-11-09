package org.example.model.consumibleComposite;

import java.util.List;

public class Combo implements Consumible{

    private String nombre;
    private String descripcion;
    private List<Consumible> listaConsumibles;


    /**
     * Metodo que calcula el costo del combo por sus elementos
     * @return
     */
    @Override
    public double calcularCosto() {
        double costo = 0;
        for (Consumible consumible : listaConsumibles) {
            costo += consumible.calcularCosto();
        }
        return costo;
    }


    /**
     * Metodo para calcular los puntos que otorga el combo por sus elemento
     * @return
     */
    @Override
    public int calcularPuntos() {
        int puntos = 0;
        for (Consumible consumible : listaConsumibles) {
            puntos += consumible.calcularPuntos();
        }
        return puntos;
    }
}
