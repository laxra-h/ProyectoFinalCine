package org.example.controller;

import org.example.model.Cine;
import org.example.model.promocion.Promocion;
import org.example.model.promocion.TipoPromocion;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PromocionesAdminController {

    private final Cine cine = Cine.getInstance();

    public record ResultadoOperacion(boolean exitoso, String mensaje) {
    }

    public List<Promocion> obtenerPromociones() {
        return new ArrayList<>(cine.getListPromociones());
    }

    public List<TipoPromocion> obtenerTiposPromocion() {
        return Arrays.asList(TipoPromocion.values());
    }

    public List<Promocion> obtenerPromocionesVigentes(TipoPromocion tipo, LocalDate fecha) {
        List<Promocion> vigentes = new ArrayList<>();
        for (Promocion promocion : cine.getListPromociones()) {
            if ((tipo == null || promocion.getTipo() == tipo) && promocion.estaVigente(fecha)) {
                vigentes.add(promocion);
            }
        }
        return vigentes;
    }

    public ResultadoOperacion crearPromocion(
            String nombre,
            String descripcion,
            TipoPromocion tipo,
            double porcentaje,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            boolean activa
    ) {
        ResultadoOperacion validacion = validarDatos(
                nombre, descripcion, tipo, porcentaje, fechaInicio, fechaFin
        );
        if (!validacion.exitoso()) {
            return validacion;
        }
        if (cine.obtenerPromocion(nombre.trim()) != null) {
            return new ResultadoOperacion(false, "Ya existe una promoción con ese nombre.");
        }

        Promocion promocion = new Promocion(
                nombre.trim(), descripcion.trim(), tipo, porcentaje,
                fechaInicio, fechaFin, activa
        );
        if (!cine.crearPromocion(promocion)) {
            return new ResultadoOperacion(false, "No fue posible registrar la promoción.");
        }
        return new ResultadoOperacion(true, "Promoción creada correctamente.");
    }

    public ResultadoOperacion actualizarPromocion(
            Promocion promocion,
            String nombre,
            String descripcion,
            TipoPromocion tipo,
            double porcentaje,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            boolean activa
    ) {
        if (promocion == null || !cine.getListPromociones().contains(promocion)) {
            return new ResultadoOperacion(false, "Selecciona una promoción registrada.");
        }
        ResultadoOperacion validacion = validarDatos(
                nombre, descripcion, tipo, porcentaje, fechaInicio, fechaFin
        );
        if (!validacion.exitoso()) {
            return validacion;
        }
        Promocion existente = cine.obtenerPromocion(nombre.trim());
        if (existente != null && existente != promocion) {
            return new ResultadoOperacion(false, "Ya existe otra promoción con ese nombre.");
        }

        promocion.setNombre(nombre.trim());
        promocion.setDescripcion(descripcion.trim());
        promocion.setTipo(tipo);
        promocion.setPorcentajeDescuento(porcentaje);
        promocion.setFechaInicio(fechaInicio);
        promocion.setFechaFin(fechaFin);
        promocion.setActiva(activa);
        return new ResultadoOperacion(true, "Promoción actualizada correctamente.");
    }

    public ResultadoOperacion eliminarPromocion(Promocion promocion) {
        if (promocion == null || !cine.getListPromociones().contains(promocion)) {
            return new ResultadoOperacion(false, "Selecciona una promoción registrada.");
        }
        if (!cine.eliminarPromocion(promocion.getNombre())) {
            return new ResultadoOperacion(false, "No fue posible eliminar la promoción.");
        }
        return new ResultadoOperacion(true, "Promoción eliminada correctamente.");
    }

    private ResultadoOperacion validarDatos(
            String nombre,
            String descripcion,
            TipoPromocion tipo,
            double porcentaje,
            LocalDate fechaInicio,
            LocalDate fechaFin
    ) {
        if (nombre == null || nombre.isBlank() || descripcion == null || descripcion.isBlank()) {
            return new ResultadoOperacion(false, "Completa el nombre y la descripción de la promoción.");
        }
        if (tipo == null) {
            return new ResultadoOperacion(false, "Selecciona a qué aplica la promoción.");
        }
        if (!Double.isFinite(porcentaje) || porcentaje <= 0 || porcentaje > 100) {
            return new ResultadoOperacion(false, "El descuento debe estar entre 0 y 100 %.");
        }
        if (fechaInicio == null || fechaFin == null) {
            return new ResultadoOperacion(false, "Selecciona las fechas de inicio y finalización.");
        }
        if (fechaFin.isBefore(fechaInicio)) {
            return new ResultadoOperacion(false, "La fecha final no puede ser anterior a la fecha inicial.");
        }
        return new ResultadoOperacion(true, "Datos válidos.");
    }
}
