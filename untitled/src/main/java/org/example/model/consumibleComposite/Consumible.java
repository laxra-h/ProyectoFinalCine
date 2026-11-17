package org.example.model.consumibleComposite;

public interface Consumible {

    public String getNombre();

    public String getDescripcion();

    /**
     * Metodo para definir como se calcula el costo de un consumible
     */
    public double calcularCosto();

    /**
     * Metodo para definir la manera en que se calculan los puntos de los consumibles
     */
    public int calcularPuntos();

}
