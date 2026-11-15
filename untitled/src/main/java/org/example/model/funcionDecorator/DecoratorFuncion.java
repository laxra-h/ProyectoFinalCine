package org.example.model.funcionDecorator;

import org.example.model.observer.Observador;
import org.example.model.pelicula.Pelicula;
import org.example.model.salaPrototype.Asiento;
import org.example.model.salaPrototype.Sala;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public abstract class DecoratorFuncion implements Funcion {

    protected final Funcion funcion;

    /**
     * Metodo constructor del Decorator
     * @param funcion
     */
    public DecoratorFuncion(Funcion funcion) {
        this.funcion = funcion;
    }


    public Funcion getFuncion() {
        return funcion;
    }


    @Override
    public String getCodigo() {
        return funcion.getCodigo();
    }

    @Override
    public Pelicula getPelicula() {
        return funcion.getPelicula();
    }

    @Override
    public Sala getSala() {
        return funcion.getSala();
    }

    @Override
    public LocalDateTime getFecha() {
        return funcion.getFecha();
    }

    @Override
    public EstadoFuncion getEstado() {
        return funcion.getEstado();
    }

    @Override
    public void setEstado(EstadoFuncion estado) {
        funcion.setEstado(estado);
    }

    @Override
    public void cancelar() {
        funcion.cancelar();
    }

    @Override
    public void agregarObservador(Observador observador) {
        funcion.agregarObservador(observador);
    }

    @Override
    public void eliminarObservador(Observador observador) {
        funcion.eliminarObservador(observador);
    }

    @Override
    public void notificar() {
        funcion.notificar();
    }

    @Override
    public void notificar(String mensaje) {
        funcion.notificar(mensaje);
    }
}
