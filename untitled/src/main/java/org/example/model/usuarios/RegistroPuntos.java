package org.example.model.usuarios;

import java.time.LocalDate;

public class RegistroPuntos {

    private int puntosObtenidos;
    private int puntosDisponibles;
    private LocalDate fechaObtencion;
    private LocalDate fechaVencimiento;
    private String codigoCompra;

    /**
     * Metodo constructor del registro de puntos con vigencia de un ano
     * @param puntosObtenidos
     * @param fechaObtencion
     * @param codigoCompra
     */
    public RegistroPuntos(int puntosObtenidos, LocalDate fechaObtencion, String codigoCompra) {
        this.puntosObtenidos = puntosObtenidos;
        this.puntosDisponibles = puntosObtenidos;
        this.fechaObtencion = (fechaObtencion != null) ? fechaObtencion : LocalDate.now();
        this.fechaVencimiento = this.fechaObtencion.plusYears(1);
        this.codigoCompra = codigoCompra;
    }

    /**
     * Metodo para verificar si los puntos estan vigentes en la fecha actual
     * @return
     */
    public boolean estaVigente() {
        return estaVigente(LocalDate.now());
    }

    /**
     * Metodo para verificar si los puntos estan vigentes en una fecha dada
     * @param fecha
     * @return
     */
    public boolean estaVigente(LocalDate fecha) {
        return !fecha.isAfter(fechaVencimiento);
    }

    /**
     * Metodo para consumir puntos de este registro
     * @param cantidad
     * @return
     */
    public int consumir(int cantidad) {
        if (cantidad <= 0 || puntosDisponibles <= 0) {
            return 0;
        }
        int usados = Math.min(cantidad, puntosDisponibles);
        puntosDisponibles -= usados;
        return usados;
    }

    public int getPuntosObtenidos() {
        return puntosObtenidos;
    }

    public int getPuntosDisponibles() {
        return puntosDisponibles;
    }

    public void setPuntosDisponibles(int puntosDisponibles) {
        this.puntosDisponibles = Math.max(0, puntosDisponibles);
    }

    public LocalDate getFechaObtencion() {
        return fechaObtencion;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public String getCodigoCompra() {
        return codigoCompra;
    }
}
