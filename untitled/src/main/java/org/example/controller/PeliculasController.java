package org.example.controller;

import org.example.model.Cine;
import org.example.model.pelicula.ClasificacionPelicula;
import org.example.model.pelicula.GeneroPelicula;
import org.example.model.pelicula.Pelicula;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PeliculasController {

    private final Cine cine = Cine.getInstance();

    public record ResultadoOperacion(boolean exitoso, String mensaje) {
    }

    public List<Pelicula> obtenerPeliculas() {
        return new ArrayList<>(cine.getListPeliculas());
    }

    public List<GeneroPelicula> obtenerGeneros() {
        return Arrays.asList(GeneroPelicula.values());
    }

    public List<ClasificacionPelicula> obtenerClasificaciones() {
        return Arrays.asList(ClasificacionPelicula.values());
    }

    public ResultadoOperacion crearPelicula(
            String nombre,
            String director,
            String descripcion,
            ClasificacionPelicula clasificacion,
            GeneroPelicula genero,
            int duracionMin
    ) {
        ResultadoOperacion validacion = validarDatos(
                nombre, director, descripcion, clasificacion, genero, duracionMin
        );
        if (!validacion.exitoso()) {
            return validacion;
        }

        if (cine.obtenerPelicula(nombre.trim()) != null) {
            return new ResultadoOperacion(false, "Ya existe una película con ese nombre.");
        }

        Pelicula pelicula = new Pelicula(
                nombre.trim(),
                director.trim(),
                descripcion.trim(),
                clasificacion,
                genero,
                duracionMin
        );

        if (!cine.crearPelicula(pelicula)) {
            return new ResultadoOperacion(false, "No fue posible registrar la película.");
        }

        return new ResultadoOperacion(true, "Película registrada correctamente.");
    }

    public ResultadoOperacion actualizarPelicula(
            String nombreActual,
            String nombreNuevo,
            String director,
            String descripcion,
            ClasificacionPelicula clasificacion,
            GeneroPelicula genero,
            int duracionMin
    ) {
        ResultadoOperacion validacion = validarDatos(
                nombreNuevo, director, descripcion, clasificacion, genero, duracionMin
        );
        if (!validacion.exitoso()) {
            return validacion;
        }

        Pelicula peliculaActual = cine.obtenerPelicula(nombreActual);
        if (peliculaActual == null) {
            return new ResultadoOperacion(false, "La película seleccionada ya no existe.");
        }

        Pelicula peliculaConNombreNuevo = cine.obtenerPelicula(nombreNuevo.trim());
        if (peliculaConNombreNuevo != null && peliculaConNombreNuevo != peliculaActual) {
            return new ResultadoOperacion(false, "Ya existe otra película con ese nombre.");
        }

        boolean actualizada = cine.actualizarPelicula(
                nombreActual, nombreNuevo.trim(), director.trim(), descripcion.trim(),
                clasificacion, genero, duracionMin
        );

        if (!actualizada) {
            return new ResultadoOperacion(false, "No fue posible actualizar la película.");
        }

        return new ResultadoOperacion(true, "Película actualizada correctamente.");
    }

    public ResultadoOperacion eliminarPelicula(String nombre) {
        Pelicula pelicula = cine.obtenerPelicula(nombre);
        if (pelicula == null) {
            return new ResultadoOperacion(false, "La película seleccionada ya no existe.");
        }

        if (!cine.eliminarPelicula(nombre)) {
            return new ResultadoOperacion(
                    false,
                    "No se puede eliminar porque tiene funciones asociadas. Primero elimina o reasigna esas funciones."
            );
        }

        return new ResultadoOperacion(true, "Película eliminada correctamente.");
    }

    private ResultadoOperacion validarDatos(
            String nombre,
            String director,
            String descripcion,
            ClasificacionPelicula clasificacion,
            GeneroPelicula genero,
            int duracionMin
    ) {
        if (nombre == null || nombre.isBlank() || director == null || director.isBlank() ||
                descripcion == null || descripcion.isBlank()) {
            return new ResultadoOperacion(false, "Completa todos los campos de la película.");
        }
        if (clasificacion == null || genero == null) {
            return new ResultadoOperacion(false, "Selecciona el género y la clasificación.");
        }
        if (duracionMin < 1 || duracionMin > 400) {
            return new ResultadoOperacion(false, "La duración debe estar entre 1 y 400 minutos.");
        }
        return new ResultadoOperacion(true, "");
    }
}
