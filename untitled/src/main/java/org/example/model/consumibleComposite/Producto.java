package org.example.model.consumibleComposite;

public class Producto implements Consumible {

    private String nombre;
    private String descripcion;
    private double costo;
    private int puntosRedimir;

    /**
     * Metodo constructor del producto
     * @param nombre
     * @param descripcion
     * @param costo
     * @param puntosRedimir
     */
    public Producto(String nombre, String descripcion, double costo, int puntosRedimir) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.costo = costo;
        this.puntosRedimir = puntosRedimir;
    }

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

    @Override
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }

    public int getPuntosRedimir() {
        return puntosRedimir;
    }

    public void setPuntosRedimir(int puntosRedimir) {
        this.puntosRedimir = puntosRedimir;
    }
}
