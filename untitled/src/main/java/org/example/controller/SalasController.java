package org.example.controller;

import org.example.model.Cine;
import org.example.model.consumibleComposite.Ticket;
import org.example.model.compra.Compra;
import org.example.model.funcionDecorator.CuatroD;
import org.example.model.funcionDecorator.DecoratorFuncion;
import org.example.model.funcionDecorator.Funcion;
import org.example.model.funcionDecorator.TresD;
import org.example.model.salaPrototype.Asiento;
import org.example.model.salaPrototype.Sala;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SalasController {

    private final Cine cine = Cine.getInstance();

    public record ResultadoOperacion(boolean exitoso, String mensaje) {
    }

    public List<Sala> obtenerSalas() {
        return new ArrayList<>(cine.getListSalas());
    }

    public List<Funcion> obtenerFunciones(Sala sala) {
        List<Funcion> funciones = new ArrayList<>();

        if (sala == null) {
            return funciones;
        }

        for (Funcion funcion : cine.getListFunciones()) {
            if (funcion.getSala() == sala) {
                funciones.add(funcion);
            }
        }

        funciones.sort(Comparator.comparing(
                Funcion::getFecha,
                Comparator.nullsLast(Comparator.naturalOrder())
        ));

        return funciones;
    }

    public int obtenerCapacidad(Sala sala) {
        if (sala == null || sala.getListAsientos() == null) {
            return 0;
        }

        int capacidad = 0;
        for (Asiento asiento : sala.getListAsientos()) {
            if (asiento != null) {
                capacidad++;
            }
        }
        return capacidad;
    }

    public String obtenerDistribucion(Sala sala) {
        if (sala == null) {
            return "-";
        }
        return sala.getNumeroFilas() + " filas × " + sala.getNumeroColumnas() + " columnas";
    }

    public int obtenerCantidadFunciones(Sala sala) {
        return obtenerFunciones(sala).size();
    }

    public ResultadoOperacion crearSala(String numeroSala) {
        if (numeroSala == null || numeroSala.isBlank()) {
            return new ResultadoOperacion(false, "Escribe el número o nombre de la sala.");
        }

        String numero = numeroSala.trim();
        if (existeNombreSala(numero, null)) {
            return new ResultadoOperacion(false, "Ya existe una sala con ese nombre.");
        }

        if (!cine.crearSala(numero)) {
            return new ResultadoOperacion(false, "No fue posible crear la sala.");
        }

        return new ResultadoOperacion(true, "Sala creada correctamente con 96 asientos.");
    }

    public ResultadoOperacion actualizarSala(String numeroActual, String numeroNuevo) {
        if (numeroActual == null || numeroActual.isBlank()) {
            return new ResultadoOperacion(false, "Selecciona una sala de la tabla.");
        }
        if (numeroNuevo == null || numeroNuevo.isBlank()) {
            return new ResultadoOperacion(false, "Escribe el nuevo nombre o número de la sala.");
        }

        String nuevo = numeroNuevo.trim();
        Sala salaActual = cine.obtenerSala(numeroActual);

        if (salaActual == null) {
            return new ResultadoOperacion(false, "La sala seleccionada ya no existe.");
        }

        if (nuevo.equalsIgnoreCase(numeroActual.trim())) {
            return new ResultadoOperacion(true, "No hay cambios en el nombre de la sala.");
        }

        if (existeNombreSala(nuevo, salaActual)) {
            return new ResultadoOperacion(false, "Ya existe otra sala con ese nombre.");
        }

        if (!cine.actualizarSala(numeroActual, nuevo)) {
            return new ResultadoOperacion(false, "No fue posible actualizar la sala.");
        }

        return new ResultadoOperacion(true, "Sala actualizada correctamente.");
    }

    public ResultadoOperacion eliminarSala(Sala sala) {
        if (sala == null) {
            return new ResultadoOperacion(false, "Selecciona una sala de la tabla.");
        }

        Sala salaRegistrada = cine.obtenerSala(sala.getNumeroSala());
        if (salaRegistrada == null) {
            return new ResultadoOperacion(false, "La sala seleccionada ya no existe.");
        }

        if (!obtenerFunciones(salaRegistrada).isEmpty()) {
            return new ResultadoOperacion(
                    false,
                    "No se puede eliminar esta sala porque tiene funciones asociadas. Elimina o reasigna primero esas funciones."
            );
        }

        if (!cine.eliminarSala(salaRegistrada.getNumeroSala())) {
            return new ResultadoOperacion(false, "No fue posible eliminar la sala.");
        }

        return new ResultadoOperacion(true, "Sala eliminada correctamente.");
    }

    public Set<String> obtenerAsientosOcupados(Funcion funcion) {
        Set<String> ocupados = new HashSet<>();

        if (funcion == null) {
            return ocupados;
        }

        for (Compra compra : cine.getListCompras()) {
            if (compra.getTheEstadoCompra() == null ||
                    !"PAGADA".equals(compra.getTheEstadoCompra().getNombre())) {
                continue;
            }

            for (Ticket ticket : compra.getListaTickets()) {
                if (ticket.getTheFuncion() != null &&
                        ticket.getTheFuncion().getCodigo().equals(funcion.getCodigo()) &&
                        ticket.getCodigoAsiento() != null) {
                    ocupados.add(ticket.getCodigoAsiento());
                }
            }
        }

        return ocupados;
    }

    public String obtenerFormato(Funcion funcion) {
        if (funcion == null) {
            return "-";
        }

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

    private boolean existeNombreSala(String numeroSala, Sala excluir) {
        for (Sala sala : cine.getListSalas()) {
            if (sala != excluir && sala.getNumeroSala() != null &&
                    sala.getNumeroSala().trim().equalsIgnoreCase(numeroSala.trim())) {
                return true;
            }
        }
        return false;
    }
}
