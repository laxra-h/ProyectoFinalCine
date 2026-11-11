package org.example.model;

public class Ticket {

    private String codigo;
    private double precio;


    /**
     * Metodo constructor para el ticket
     * @param codigo
     * @param precio
     */
    public Ticket(String codigo, double precio) {
        this.codigo = codigo;
        this.precio = precio;
    }
}
