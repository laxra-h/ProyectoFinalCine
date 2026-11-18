package org.example.controller;

import org.example.model.consumibleComposite.Ticket;
import org.example.model.compra.Compra;
import org.example.model.consumibleComposite.Consumible;
import org.example.model.funcionDecorator.Funcion;
import org.example.model.pelicula.Pelicula;
import org.example.model.salaPrototype.Sala;
import org.example.model.usuarios.Cliente;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ComprasController {

    public List<Compra> obtenerCompras(Cliente cliente) {
        if (cliente == null || cliente.getListCompras() == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(cliente.getListCompras());
    }

    public int obtenerCantidadEntradas(Compra compra) {
        if (compra == null || compra.getListaTickets() == null) {
            return 0;
        }

        return compra.getListaTickets().size();
    }

    public String obtenerConsumibles(Compra compra) {
        if (compra == null || compra.getListConsumibles() == null
                || compra.getListConsumibles().isEmpty()) {
            return "Sin consumibles";
        }

        return compra.getListConsumibles().stream()
                .map(Consumible::getNombre)
                .collect(Collectors.joining(", "));
    }

    public String obtenerEstado(Compra compra) {
        if (compra == null || compra.getTheEstadoCompra() == null) {
            return "Sin estado";
        }

        return compra.getTheEstadoCompra().getNombre();
    }

    public String obtenerDetalleEntradas(Compra compra) {
        if (compra == null || compra.getListaTickets() == null
                || compra.getListaTickets().isEmpty()) {
            return "No hay entradas registradas.";
        }

        StringBuilder detalles = new StringBuilder();

        for (Ticket ticket : compra.getListaTickets()) {
            Funcion funcion = ticket.getTheFuncion();

            if (funcion == null) {
                continue;
            }

            Pelicula pelicula = funcion.getPelicula();
            Sala sala = funcion.getSala();

            if (detalles.length() > 0) {
                detalles.append("\n");
            }

            detalles.append("Película: ")
                    .append(pelicula != null ? pelicula.getNombre() : "No disponible")
                    .append(" | Sala: ")
                    .append(sala != null ? sala.getNumeroSala() : "No disponible")
                    .append(" | Asiento: ")
                    .append(ticket.getCodigoAsiento())
                    .append(" | Función: ")
                    .append(funcion.getFecha().toLocalDate())
                    .append(" ")
                    .append(funcion.getFecha().toLocalTime());
        }

        return detalles.length() > 0 ? detalles.toString() : "No hay detalles disponibles.";
    }

    public String obtenerDetalleConsumibles(Compra compra) {
        if (compra == null || compra.getListConsumibles() == null
                || compra.getListConsumibles().isEmpty()) {
            return "No se adquirieron productos de confitería.";
        }

        StringBuilder detalles = new StringBuilder();

        for (Consumible consumible : compra.getListConsumibles()) {
            if (detalles.length() > 0) {
                detalles.append("\n");
            }

            detalles.append(consumible.getNombre())
                    .append(" - $")
                    .append(String.format("%,.0f", consumible.calcularCosto()));

            if (consumible.getDescripcion() != null
                    && !consumible.getDescripcion().isBlank()) {
                detalles.append(" (")
                        .append(consumible.getDescripcion())
                        .append(")");
            }
        }

        return detalles.toString();
    }
}