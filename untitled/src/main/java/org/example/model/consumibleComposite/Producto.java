package org.example.model.consumibleComposite;

public class Producto implements Consumible{

    private String nombre;
    private String descripcion;
    private double costo;
    private int puntosRedimir;

    /**
     * Metodo que calcula el costo del producto
     * @return
     */
    @Override
    public double calcularCosto() {
        return costo;
    }

    /**
     * Metodo que calcular los puntos que otorga el producto
     * @return
     */
    @Override
    public int calcularPuntos() {
        return puntosRedimir;
    }
}
