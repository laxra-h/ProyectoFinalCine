package org.example.model.tarjeta;

import java.time.LocalDate;

public class MovimientoTarjeta {

    private LocalDate fecha;
    private TipoMovimiento tipo;
    private double monto;
    private String descripcion;

    /**
     * Metodo constructor del movimiento de la tarjeta
     * @param fecha
     * @param tipo
     * @param monto
     * @param descripcion
     */
    public MovimientoTarjeta(LocalDate fecha, TipoMovimiento tipo, double monto, String descripcion) {
        this.fecha = fecha;
        this.tipo = tipo;
        this.monto = monto;
        this.descripcion = descripcion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public TipoMovimiento getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimiento tipo) {
        this.tipo = tipo;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
