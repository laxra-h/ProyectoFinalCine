package org.example.model;

import org.example.model.consumibleComposite.Consumible;
import org.example.model.funcionDecorator.Funcion;

import java.io.Serializable;

public class Ticket implements Consumible {

    private String codigo;
    private double precio;
    private String codigoAsiento;
    private Funcion theFuncion;


    /**
     * Metodo constructor para el ticket
     * @param codigo
     * @param codigoAsiento
     */
    public Ticket(String codigo, String codigoAsiento) {
        this.codigo = codigo;
        this.precio = theFuncion.calcularCosto();
        this.codigoAsiento = codigoAsiento;
    }


    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public Funcion getTheFuncion() {
        return theFuncion;
    }

    public void setTheFuncion(Funcion theFuncion) {
        this.theFuncion = theFuncion;
    }

    public String getCodigoAsiento() {
        return codigoAsiento;
    }

    public void setCodigoAsiento(String codigoAsiento) {
        this.codigoAsiento = codigoAsiento;
    }

    @Override
    public String getNombre() {
        return this.codigo;
    }

    @Override
    public String getDescripcion() {
        return "";
    }

    @Override
    public double calcularCosto() {
        return theFuncion.calcularCosto();
    }

    @Override
    public int calcularPuntos() {
        return theFuncion.getPuntosRedmidos();
    }
}
