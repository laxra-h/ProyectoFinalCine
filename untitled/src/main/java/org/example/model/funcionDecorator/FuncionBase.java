package org.example.model.funcionDecorator;

import org.example.model.pelicula.Pelicula;
import org.example.model.salaPrototype.Asiento;
import org.example.model.salaPrototype.Sala;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class FuncionBase implements Funcion {

    private String codigo;
    private Pelicula pelicula;
    private Sala sala;
    private LocalDateTime fecha;
    private final int DESCUENTO_SEMANA = 40;
    private final double COSTO = 20000;
    private EstadoFuncion estado;


    /**
     * Metodo constructor de la funcionBase
     * @param codigo
     * @param fecha
     */
    public FuncionBase(String codigo, LocalDateTime fecha) {
        this.codigo = codigo;
        this.fecha = fecha;
    }

    /**
     * Metodo constructor completo de la funcionBase asociada a una pelicula y sala
     * @param codigo
     * @param pelicula
     * @param sala
     * @param fecha
     * @param estado
     */
    public FuncionBase(String codigo, Pelicula pelicula, Sala sala, LocalDateTime fecha, EstadoFuncion estado) {
        this.codigo = codigo;
        this.pelicula = pelicula;
        this.sala = sala;
        this.fecha = fecha;
        this.estado = estado;
    }



    /**
     * Metodo para calcular el costo de la funcion base si es fin de semana o dia entre semana
     * @return
     */
    @Override
    public double calcularCosto() {
        DayOfWeek dia = (this.fecha != null ? this.fecha : LocalDateTime.now()).getDayOfWeek();
        if (dia == DayOfWeek.SATURDAY || dia == DayOfWeek.SUNDAY) {
            return COSTO;
        } else {
            return calcularCostoSemana();
        }
    }

    /**
     * Metodo que calcula el costo con promocion de semana
     * @return
     */
    public double calcularCostoSemana() {
        return (COSTO * DESCUENTO_SEMANA) / 100;
    }

    /**
     * Metodo para resetear los asientos
     */
    public void resetAsientos() {

            for (Asiento asiento : sala.getListAsientos()) {
                asiento.setDisponible(true);
            }
    }










    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Pelicula getPelicula() {
        return pelicula;
    }

    public void setPelicula(Pelicula pelicula) {
        this.pelicula = pelicula;
    }

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }


    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public int getDESCUENTO_SEMANA() {
        return DESCUENTO_SEMANA;
    }

    public double getCOSTO() {
        return COSTO;
    }

    public EstadoFuncion getEstado() {
        return estado;
    }

    public void setEstado(EstadoFuncion estado) {
        this.estado = estado;
        if (estado == EstadoFuncion.FINALIZADA) {
            resetAsientos();
        }
    }
}
