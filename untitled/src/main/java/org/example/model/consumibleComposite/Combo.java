package org.example.model.consumibleComposite;

import java.util.ArrayList;
import java.util.List;

public class Combo implements Consumible {

    private String nombre;
    private String descripcion;
    private List<Consumible> listaConsumibles;

    /**
     * Metodo constructor del combo
     * @param nombre
     * @param descripcion
     */
    public Combo(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.listaConsumibles = new ArrayList<>();
    }

    public void agregarConsumible(Consumible consumible) {
        this.listaConsumibles.add(consumible);
    }

    public void eliminarConsumible(Consumible consumible) {
        this.listaConsumibles.remove(consumible);
    }

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

    public List<Consumible> getListaConsumibles() {
        return listaConsumibles;
    }
}
