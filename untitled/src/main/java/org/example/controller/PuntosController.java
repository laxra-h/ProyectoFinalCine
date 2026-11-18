package org.example.controller;

import org.example.model.compra.Compra;
import org.example.model.usuarios.Cliente;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PuntosController {

    public List<Compra> obtenerComprasConPuntos(Cliente cliente) {

        List<Compra> comprasConPuntos = new ArrayList<>();

        if (cliente == null || cliente.getListCompras() == null) {
            return comprasConPuntos;
        }

        for (Compra compra : cliente.getListCompras()) {

            if (compra.getPuntosCompra() > 0 && compra.getTheEstadoCompra() != null && compra.getTheEstadoCompra().getNombre().equals("PAGADA")) {
                comprasConPuntos.add(compra);
            }
        }
        return comprasConPuntos;
    }

    public LocalDate obtenerFechaVencimiento(Compra compra) {
        return compra.getFechaCompra().plusYears(1);
    }

    public boolean puntosVigentes(Compra compra) {
        return !LocalDate.now().isAfter(obtenerFechaVencimiento(compra));
    }

    public int obtenerPuntosDisponibles(Cliente cliente) {

        if (cliente == null) {
            return 0;
        }

        int puntosDisponibles = 0;

        for (Compra compra : obtenerComprasConPuntos(cliente)) {

            if (puntosVigentes(compra)) {
                puntosDisponibles += compra.getPuntosCompra();
            }
        }

        return puntosDisponibles;
    }
}