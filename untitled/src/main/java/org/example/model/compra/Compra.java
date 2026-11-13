package org.example.model.compra;

import java.time.LocalDate;

public class Compra {

    private String codigo;
    private LocalDate fechaCompra;
    private double total;
    private int puntosCompra;
  //  private Reembolso theReembolso;
  //  private EstrategiaReembolso estrategiaReembolso;

    /**
     * Metodo cosntructor de la cosa Compra
     * @param codigo
     * @param fechaCompra
     * @param total
     * @param puntosCompra
     */
    public Compra(String codigo, LocalDate fechaCompra, double total, int puntosCompra) {
        this.codigo = codigo;
        this.fechaCompra = fechaCompra;
        this.total = total;
        this.puntosCompra = puntosCompra;
    }
}
