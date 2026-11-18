package org.example.model.funcionDecorator;

import org.example.exceptions.AsientoNoDisponibleException;
import org.example.model.observer.Observador;
import org.example.model.pelicula.Pelicula;
import org.example.model.salaPrototype.Asiento;
import org.example.model.salaPrototype.Sala;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface Funcion {


    /**
     * Metodo para definir como calcula el costo la funcion base y sus deoradores
     * @return
     */
    public double calcularCosto();


    public String getCodigo();

    public Pelicula getPelicula();

    public Sala getSala();

    public LocalDateTime getFecha();

    public EstadoFuncion getEstado();

    public void setEstado(EstadoFuncion estado);

    public void cancelar();

    public void agregarObservador(Observador observador);

    public void eliminarObservador(Observador observador);

    public void notificar();

    public void notificar(String mensaje);

    public int getPuntosRedmidos();

    public Asiento[] getAsientos();

    /**
     * Metodo para verificar si un asiento esta disponible
     * @param codigoAsiento
     * @return
     */
    public boolean asientoDisponible(String codigoAsiento);

    /**
     * Metodo para ocupar un asiento de la funcion
     * @param codigoAsiento
     * @throws AsientoNoDisponibleException
     */
    public void ocuparAsiento(String codigoAsiento) throws AsientoNoDisponibleException;

    /**
     * Metodo para liberar un asiento de la funcion
     * @param codigoAsiento
     */
    public void liberarAsiento(String codigoAsiento);


    public List<Observador> getListObservadores();
}
