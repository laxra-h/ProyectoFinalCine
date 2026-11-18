package org.example.controller;

import org.example.model.Cine;
import org.example.model.consumibleComposite.Ticket;
import org.example.model.compra.Compra;
import org.example.model.consumibleComposite.Consumible;
import org.example.model.pelicula.Pelicula;
import org.example.model.reporteAdapter.AdaptadorExportadorPdf;
import org.example.model.reporteAdapter.ExportadorTxt;
import org.example.model.reporteAdapter.TipoReporte;
import org.example.services.CineFacade;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ReportesAdminController {

    private final Cine cine = Cine.getInstance();
    private final CineFacade cineFacade = new CineFacade();
    private static final DateTimeFormatter FORMATO_CORTO = DateTimeFormatter.ofPattern("dd/MM");

    public List<TipoReporte> obtenerTiposReporte() {
        return Arrays.asList(TipoReporte.values());
    }

    public String generarTextoReporte(TipoReporte tipo, LocalDate inicio, LocalDate fin) {
        return cineFacade.generarReporte(tipo, inicio, fin);
    }

    public File exportarTxt(TipoReporte tipo, LocalDate inicio, LocalDate fin, File destino) throws IOException {
        return cineFacade.exportarReporte(tipo, inicio, fin, new ExportadorTxt(), destino);
    }

    public File exportarPdf(TipoReporte tipo, LocalDate inicio, LocalDate fin, File destino) throws IOException {
        return cineFacade.exportarReporte(tipo, inicio, fin, new AdaptadorExportadorPdf(), destino);
    }

    public Map<String, Number> obtenerEntradasPorPelicula(LocalDate inicio, LocalDate fin) {
        Map<String, Number> resultado = new LinkedHashMap<>();
        for (Pelicula p : cine.getListPeliculas()) {
            resultado.put(p.getNombre(), 0);
        }
        for (Compra c : cine.getListCompras()) {
            if (!esPagadaEnRango(c, inicio, fin)) {
                continue;
            }
            for (Ticket t : c.getListaTickets()) {
                if (t.getTheFuncion() != null && t.getTheFuncion().getPelicula() != null) {
                    String pelicula = t.getTheFuncion().getPelicula().getNombre();
                    resultado.put(pelicula, resultado.getOrDefault(pelicula, 0).intValue() + 1);
                }
            }
        }
        return resultado;
    }

    public Map<String, Double> obtenerDistribucionVentas(LocalDate inicio, LocalDate fin) {
        double boleteria = 0;
        double confiteria = 0;
        for (Compra c : cine.getListCompras()) {
            if (!esPagadaEnRango(c, inicio, fin)) {
                continue;
            }
            for (Ticket t : c.getListaTickets()) {
                boleteria += t.getPrecio();
            }
            for (Consumible item : c.getListConsumibles()) {
                confiteria += item.calcularCosto();
            }
        }
        Map<String, Double> distribucion = new LinkedHashMap<>();
        distribucion.put("Boletería", boleteria);
        distribucion.put("Confitería", confiteria);
        return distribucion;
    }

    public Map<String, Number> obtenerVentasPorFecha(LocalDate inicio, LocalDate fin) {
        Map<LocalDate, Double> ordenado = new TreeMap<>();
        for (Compra c : cine.getListCompras()) {
            if (!esPagadaEnRango(c, inicio, fin)) {
                continue;
            }
            ordenado.merge(c.getFechaCompra(), c.getTotal(), Double::sum);
        }
        Map<String, Number> resultado = new LinkedHashMap<>();
        for (Map.Entry<LocalDate, Double> entry : ordenado.entrySet()) {
            resultado.put(entry.getKey().format(FORMATO_CORTO), entry.getValue());
        }
        return resultado;
    }

    private boolean esPagadaEnRango(Compra c, LocalDate inicio, LocalDate fin) {
        if (c == null || c.getFechaCompra() == null || c.getTheEstadoCompra() == null) {
            return false;
        }
        if (!"PAGADA".equals(c.getTheEstadoCompra().getNombre())) {
            return false;
        }
        boolean desde = (inicio == null) || !c.getFechaCompra().isBefore(inicio);
        boolean hasta = (fin == null) || !c.getFechaCompra().isAfter(fin);
        return desde && hasta;
    }
}
