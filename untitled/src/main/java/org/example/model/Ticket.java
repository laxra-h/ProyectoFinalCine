package org.example.model;

import org.example.model.funcionDecorator.Funcion;

public class Ticket {

    private String codigo;
    private double precio;
    private String codigoAsiento;
    private Funcion theFuncion;


    /**
     * Metodo constructor para el ticket
     * @param codigo
     * @param precio
     * @param codigoAsiento
     */
    public Ticket(String codigo, double precio, String codigoAsiento) {
        this.codigo = codigo;
        this.precio = precio;
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
}
