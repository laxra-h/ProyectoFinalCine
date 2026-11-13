package org.example.model.funcionDecorator;

import org.example.model.pelicula.Pelicula;
import org.example.model.salaPrototype.Asiento;
import org.example.model.salaPrototype.Sala;

import java.time.LocalDate;
import java.time.LocalTime;

public interface Funcion {


    /**
     * Metodo para definir como calcula el costo la funcion base y sus deoradores
     * @return
     */
    public double calcularCosto();

    public String getCodigo();

    public Pelicula getPelicula();

    public Sala getSala();

    public Asiento[] getAsientos();

    public LocalDate getFecha();

    public LocalTime getHora();

    public EstadoFuncion getEstado();

    public void setEstado(EstadoFuncion estado);
}
