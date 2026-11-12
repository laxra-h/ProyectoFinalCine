package org.example.model.funcionDecorator;

import org.example.model.pelicula.Pelicula;
import org.example.model.salaPrototype.Asiento;
import org.example.model.salaPrototype.Sala;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

public class FuncionBase implements Funcion {

    private String codigo;
    private Pelicula pelicula;
    private Sala sala;
    private Asiento[] asientos;
    private LocalDate fecha;
    private LocalTime hora;
    private final int DESCUENTO_SEMANA = 40;
    private final double COSTO = 20000;
    private EstadoFuncion estado;


    /**
     * Metodo constructor de la funcionBase
     * @param codigo
     * @param fecha
     * @param hora
     * @param estado
     */
    public FuncionBase(String codigo, LocalDate fecha, LocalTime hora, EstadoFuncion estado) {
        this.codigo = codigo;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = estado;
    }

    /**
     * Metodo constructor completo de la funcionBase asociada a una pelicula y sala
     * @param codigo
     * @param pelicula
     * @param sala
     * @param fecha
     * @param hora
     * @param estado
     */
    public FuncionBase(String codigo, Pelicula pelicula, Sala sala, LocalDate fecha, LocalTime hora, EstadoFuncion estado) {
        this.codigo = codigo;
        this.pelicula = pelicula;
        this.sala = sala;
        this.asientos = sala != null ? sala.clonarAsientosParaFuncion() : null;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = estado;
    }



    /**
     * Metodo para calcular el costo de la funcion base si es fin de semana o dia entre semana
     * @return
     */
    @Override
    public double calcularCosto() {
        DayOfWeek dia = (this.fecha != null ? this.fecha : LocalDate.now()).getDayOfWeek();
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

    @Override
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    @Override
    public Pelicula getPelicula() {
        return pelicula;
    }

    public void setPelicula(Pelicula pelicula) {
        this.pelicula = pelicula;
    }

    @Override
    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
        if (sala != null && this.asientos == null) {
            this.asientos = sala.clonarAsientosParaFuncion();
        }
    }

    @Override
    public Asiento[] getAsientos() {
        return asientos;
    }

    @Override
    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    @Override
    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    @Override
    public EstadoFuncion getEstado() {
        return estado;
    }

    @Override
    public void setEstado(EstadoFuncion estado) {
        this.estado = estado;
    }
}
