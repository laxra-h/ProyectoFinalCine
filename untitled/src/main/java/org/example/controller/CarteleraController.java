package org.example.controller;

import org.example.model.Cine;
import org.example.model.funcionDecorator.CuatroD;
import org.example.model.funcionDecorator.DecoratorFuncion;
import org.example.model.funcionDecorator.EstadoFuncion;
import org.example.model.funcionDecorator.Funcion;
import org.example.model.funcionDecorator.TresD;
import org.example.model.pelicula.Pelicula;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CarteleraController {

    private final Cine cine = Cine.getInstance();

    /**
     * Metodo para obtener las peliculas registradas en el cine
     * @return copia de la lista de peliculas
     */
    public List<Pelicula> obtenerPeliculas() {
        return new ArrayList<>(cine.getListPeliculas());
    }

    /**
     * Metodo para obtener las funciones programada de una pelicula
     * @param pelicula
     * @return lista de funciones programadas de la pelicula
     */
    public List<Funcion> obtenerFuncionesDisponibles(Pelicula pelicula) {
        List<Funcion> disponibles = new ArrayList<>();
        for (Funcion funcion : cine.getListFunciones()) {
            if (funcion.getPelicula() == pelicula && funcion.getEstado() == EstadoFuncion.PROGRAMADA) {
                disponibles.add(funcion);
            }
        }
        disponibles.sort(Comparator.comparing(Funcion::getFecha));
        return disponibles;
    }

    /**
     * Metodo para saber el formato de una funcion recorriendo sus decoradores
     * @param funcion
     * @return "4D", "3D" o "2D"
     */
    public String obtenerFormato(Funcion funcion) {
        String formato = "2D";
        Funcion actual = funcion;
        while (actual instanceof DecoratorFuncion decorador) {
            if (actual instanceof CuatroD) {
                return "4D";
            }
            if (actual instanceof TresD) {
                formato = "3D";
            }
            actual = decorador.getFuncion();
        }
        return formato;
    }
}