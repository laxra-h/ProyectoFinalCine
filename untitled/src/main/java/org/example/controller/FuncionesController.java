package org.example.controller;

import org.example.exceptions.CancelacionNoPermitidaException;
import org.example.model.Cine;
import org.example.model.consumibleComposite.Ticket;
import org.example.model.compra.Compra;
import org.example.model.compra.estrategiaReembolso.ReembolsoCompleto;
import org.example.model.funcionDecorator.CuatroD;
import org.example.model.funcionDecorator.DecoratorFuncion;
import org.example.model.funcionDecorator.EstadoFuncion;
import org.example.model.funcionDecorator.Funcion;
import org.example.model.funcionDecorator.FuncionBase;
import org.example.model.funcionDecorator.TresD;
import org.example.model.observer.Observador;
import org.example.model.pelicula.Pelicula;
import org.example.model.salaPrototype.Sala;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class FuncionesController {

    private final Cine cine = Cine.getInstance();

    public record ResultadoOperacion(boolean exitoso, String mensaje) {
    }

    public List<Funcion> obtenerFunciones() {
        List<Funcion> funciones = new ArrayList<>(cine.getListFunciones());
        funciones.sort(Comparator.comparing(
                Funcion::getFecha,
                Comparator.nullsLast(Comparator.naturalOrder())
        ));
        return funciones;
    }

    public List<Pelicula> obtenerPeliculas() {
        return new ArrayList<>(cine.getListPeliculas());
    }

    public List<Sala> obtenerSalas() {
        return new ArrayList<>(cine.getListSalas());
    }

    public List<String> obtenerFormatos() {
        return Arrays.asList("2D", "3D", "4D");
    }

    public List<EstadoFuncion> obtenerEstados() {
        return Arrays.asList(EstadoFuncion.values());
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

    public int obtenerEntradasVendidas(Funcion funcion) {
        if (funcion == null) {
            return 0;
        }

        int cantidad = 0;
        for (Compra compra : cine.getListCompras()) {
            if (compra.getTheEstadoCompra() == null ||
                    !"PAGADA".equals(compra.getTheEstadoCompra().getNombre())) {
                continue;
            }

            for (Ticket ticket : compra.getListaTickets()) {
                if (ticket.getTheFuncion() != null &&
                        ticket.getTheFuncion().getCodigo().equals(funcion.getCodigo())) {
                    cantidad++;
                }
            }
        }
        return cantidad;
    }

    public LocalDateTime obtenerFinalizacion(Funcion funcion) {
        if (funcion == null || funcion.getFecha() == null || funcion.getPelicula() == null) {
            return null;
        }
        return funcion.getFecha().plusMinutes(funcion.getPelicula().getDuracionMin());
    }

    public boolean tieneComprasAsociadas(Funcion funcion) {
        if (funcion == null) {
            return false;
        }

        for (Compra compra : cine.getListCompras()) {
            for (Ticket ticket : compra.getListaTickets()) {
                if (ticket.getTheFuncion() != null &&
                        ticket.getTheFuncion().getCodigo().equals(funcion.getCodigo())) {
                    return true;
                }
            }
        }
        return false;
    }

    public ResultadoOperacion crearFuncion(
            Pelicula pelicula,
            Sala sala,
            LocalDateTime fecha,
            String formato
    ) {
        ResultadoOperacion validacion = validarDatos(pelicula, sala, fecha, formato, null);
        if (!validacion.exitoso()) {
            return validacion;
        }

        if (!horarioDisponible(pelicula, sala, fecha, null)) {
            return new ResultadoOperacion(
                    false,
                    "La sala ya tiene otra función programada en ese horario."
            );
        }

        String codigo = generarCodigo();
        Funcion nuevaFuncion = construirFuncion(codigo, pelicula, sala, fecha, formato);
        cine.getListFunciones().add(nuevaFuncion);

        return new ResultadoOperacion(true, "Función " + codigo + " creada correctamente.");
    }

    public ResultadoOperacion actualizarFuncion(
            Funcion funcionActual,
            Pelicula pelicula,
            Sala sala,
            LocalDateTime fecha,
            String formato
    ) {
        if (funcionActual == null || !cine.getListFunciones().contains(funcionActual)) {
            return new ResultadoOperacion(false, "Selecciona una función registrada para actualizar.");
        }

        if (tieneComprasAsociadas(funcionActual)) {
            return new ResultadoOperacion(
                    false,
                    "No se puede modificar esta función porque tiene compras asociadas."
            );
        }

        if (funcionActual.getEstado() == EstadoFuncion.CANCELADA ||
                funcionActual.getEstado() == EstadoFuncion.FINALIZADA) {
            return new ResultadoOperacion(
                    false,
                    "No se pueden modificar funciones canceladas o finalizadas."
            );
        }

        ResultadoOperacion validacion = validarDatos(pelicula, sala, fecha, formato, funcionActual);
        if (!validacion.exitoso()) {
            return validacion;
        }

        if (!horarioDisponible(pelicula, sala, fecha, funcionActual)) {
            return new ResultadoOperacion(
                    false,
                    "La sala ya tiene otra función programada en ese horario."
            );
        }

        Funcion actualizada = construirFuncion(
                funcionActual.getCodigo(), pelicula, sala, fecha, formato
        );

        for (Observador observador : funcionActual.getListObservadores()) {
            actualizada.agregarObservador(observador);
        }

        int indice = cine.getListFunciones().indexOf(funcionActual);
        cine.getListFunciones().set(indice, actualizada);

        return new ResultadoOperacion(true, "Función actualizada correctamente.");
    }

    public ResultadoOperacion eliminarFuncion(Funcion funcion) {
        if (funcion == null || !cine.getListFunciones().contains(funcion)) {
            return new ResultadoOperacion(false, "Selecciona una función de la tabla.");
        }

        if (tieneComprasAsociadas(funcion)) {
            return new ResultadoOperacion(
                    false,
                    "No se puede eliminar esta función porque tiene compras asociadas. Puedes cancelarla para procesar los reembolsos."
            );
        }

        cine.getListFunciones().remove(funcion);
        return new ResultadoOperacion(true, "Función eliminada correctamente.");
    }

    public ResultadoOperacion cambiarEstado(Funcion funcion, EstadoFuncion nuevoEstado) {
        if (funcion == null || !cine.getListFunciones().contains(funcion)) {
            return new ResultadoOperacion(false, "Selecciona una función registrada.");
        }
        if (nuevoEstado == null) {
            return new ResultadoOperacion(false, "Selecciona el nuevo estado de la función.");
        }
        if (funcion.getEstado() == nuevoEstado) {
            return new ResultadoOperacion(false, "La función ya tiene ese estado.");
        }
        if (!transicionPermitida(funcion.getEstado(), nuevoEstado)) {
            return new ResultadoOperacion(
                    false,
                    "Transición no permitida. Una función PROGRAMADA puede iniciar o cancelarse; una EN_CURSO puede finalizarse o cancelarse."
            );
        }

        funcion.setEstado(nuevoEstado);

        if (nuevoEstado == EstadoFuncion.CANCELADA) {
            int comprasProcesadas = 0;
            for (Compra compra : cine.getListCompras()) {
                if (compraAsociadaAFuncion(compra, funcion) &&
                        compra.getTheEstadoCompra() != null &&
                        ("PAGADA".equals(compra.getTheEstadoCompra().getNombre()) ||
                                "PENDIENTE".equals(compra.getTheEstadoCompra().getNombre()))) {
                    if ("PAGADA".equals(compra.getTheEstadoCompra().getNombre())) {
                        compra.setTheEstrategiaReembolso(new ReembolsoCompleto());
                    }
                    try {
                        compra.cancelar();
                        comprasProcesadas++;
                    } catch (CancelacionNoPermitidaException ignored) {
                    }
                }
            }

            return new ResultadoOperacion(
                    true,
                    "Función cancelada. Se procesaron " + comprasProcesadas +
                            (comprasProcesadas == 1 ? " compra asociada." : " compras asociadas.")
            );
        }

        return new ResultadoOperacion(true, "Estado de la función actualizado a " + nuevoEstado + ".");
    }

    private Funcion construirFuncion(
            String codigo,
            Pelicula pelicula,
            Sala sala,
            LocalDateTime fecha,
            String formato
    ) {
        Funcion funcion = new FuncionBase(
                codigo,
                pelicula,
                sala,
                fecha,
                EstadoFuncion.PROGRAMADA
        );

        if ("3D".equals(formato)) {
            funcion = new TresD(funcion);
        } else if ("4D".equals(formato)) {
            funcion = new CuatroD(funcion);
        }

        return funcion;
    }

    private ResultadoOperacion validarDatos(
            Pelicula pelicula,
            Sala sala,
            LocalDateTime fecha,
            String formato,
            Funcion funcionActual
    ) {
        if (pelicula == null || sala == null || fecha == null || formato == null || formato.isBlank()) {
            return new ResultadoOperacion(
                    false,
                    "Selecciona una película, una sala, un formato y una fecha y hora."
            );
        }

        if (!cine.getListPeliculas().contains(pelicula)) {
            return new ResultadoOperacion(false, "La película seleccionada ya no está registrada.");
        }

        if (!cine.getListSalas().contains(sala)) {
            return new ResultadoOperacion(false, "La sala seleccionada ya no está registrada.");
        }

        if (!obtenerFormatos().contains(formato)) {
            return new ResultadoOperacion(false, "El formato seleccionado no es válido.");
        }

        if (!fecha.isAfter(LocalDateTime.now())) {
            return new ResultadoOperacion(false, "La fecha y hora de la función deben ser futuras.");
        }

        if (pelicula.getDuracionMin() <= 0) {
            return new ResultadoOperacion(false, "La película debe tener una duración válida.");
        }

        return new ResultadoOperacion(true, "");
    }

    private boolean horarioDisponible(
            Pelicula pelicula,
            Sala sala,
            LocalDateTime fecha,
            Funcion excluir
    ) {
        LocalDateTime finNuevo = fecha.plusMinutes(pelicula.getDuracionMin());

        for (Funcion existente : cine.getListFunciones()) {
            if (existente == excluir || existente.getSala() != sala ||
                    existente.getEstado() == EstadoFuncion.CANCELADA ||
                    existente.getFecha() == null || existente.getPelicula() == null) {
                continue;
            }

            LocalDateTime inicioExistente = existente.getFecha();
            LocalDateTime finExistente = inicioExistente.plusMinutes(
                    existente.getPelicula().getDuracionMin()
            );

            boolean seCruzan = fecha.isBefore(finExistente) && inicioExistente.isBefore(finNuevo);
            if (seCruzan) {
                return false;
            }
        }

        return true;
    }

    private boolean transicionPermitida(EstadoFuncion actual, EstadoFuncion nuevo) {
        if (actual == null) {
            return false;
        }

        return switch (actual) {
            case PROGRAMADA -> nuevo == EstadoFuncion.EN_CURSO || nuevo == EstadoFuncion.CANCELADA;
            case EN_CURSO -> nuevo == EstadoFuncion.FINALIZADA || nuevo == EstadoFuncion.CANCELADA;
            case FINALIZADA, CANCELADA -> false;
        };
    }

    private boolean compraAsociadaAFuncion(Compra compra, Funcion funcion) {
        if (compra == null || compra.getListaTickets() == null) {
            return false;
        }

        for (Ticket ticket : compra.getListaTickets()) {
            if (ticket.getTheFuncion() != null &&
                    ticket.getTheFuncion().getCodigo().equals(funcion.getCodigo())) {
                return true;
            }
        }
        return false;
    }

    private String generarCodigo() {
        int consecutivo = 1;
        String codigo;

        do {
            codigo = "F" + String.format("%03d", consecutivo++);
        } while (existeCodigo(codigo));

        return codigo;
    }

    private boolean existeCodigo(String codigo) {
        for (Funcion funcion : cine.getListFunciones()) {
            if (funcion.getCodigo().equalsIgnoreCase(codigo)) {
                return true;
            }
        }
        return false;
    }
}
