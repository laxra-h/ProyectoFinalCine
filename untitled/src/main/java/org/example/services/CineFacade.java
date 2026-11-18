package org.example.services;

import org.example.exceptions.CancelacionNoPermitidaException;
import org.example.model.Cine;
import org.example.model.compra.Compra;
import org.example.model.compra.estrategiaReembolso.ReembolsoCompleto;
import org.example.model.consumibleComposite.Consumible;
import org.example.model.funcionDecorator.EstadoFuncion;
import org.example.model.funcionDecorator.Funcion;
import org.example.model.reporteAdapter.ExportadorReporte;
import org.example.model.reporteAdapter.TipoReporte;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class CineFacade {

    Cine cine = Cine.getInstance();

    private final ClienteService clienteService;
    private final CompraServices compraService;
    private final SesionService sesionService;
    private final AdministradorService administradorService;

    /**
     * Metodo constructor de la fachada del cine
     */
    public CineFacade() {
        this.clienteService = new ClienteService();
        this.compraService = new CompraServices();
        this.sesionService = new SesionService();
        this.administradorService = new AdministradorService();
    }

    /**
     * Metodo para generar un reporte
     * @param fechaInicio
     * @param fechaFin
     * @return
     */
    public String generarReporte(LocalDate fechaInicio, LocalDate fechaFin) {
        return administradorService.generarReporte(fechaInicio, fechaFin);
    }

    /**
     * Metodo para generar un reporte operativo por tipo y rango de fechas
     * @param tipo
     * @param fechaInicio
     * @param fechaFin
     * @return
     */
    public String generarReporte(TipoReporte tipo, LocalDate fechaInicio, LocalDate fechaFin) {
        return administradorService.generarReporte(tipo, fechaInicio, fechaFin);
    }

    /**
     * Metodo para exportar un reporte en TXT o PDF a traves de la fachada
     * @param tipo
     * @param fechaInicio
     * @param fechaFin
     * @param exportador
     * @param destino
     * @return
     * @throws IOException
     */
    public File exportarReporte(
            TipoReporte tipo,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            ExportadorReporte exportador,
            File destino
    ) throws IOException {
        return administradorService.exportarReporte(tipo, fechaInicio, fechaFin, exportador, destino);
    }

    /**
     * Metodo que permite al administrador cancelar funcion y hacer los reembolsos del 100% de manera automatica
     * @param funcion
     * @param estadoFuncion
     */
    public void cancelarFuncion(Funcion funcion, EstadoFuncion estadoFuncion) {
        administradorService.cambiarEstadoFuncion(funcion, estadoFuncion);
        for (Compra c : cine.getListCompras()) {
            if (c.getListaTickets() != null && !c.getListaTickets().isEmpty()
                    && c.getListaTickets().get(0).getTheFuncion() == funcion) {
                c.setTheEstrategiaReembolso(new ReembolsoCompleto());
                try {
                    c.cancelar();
                } catch (CancelacionNoPermitidaException ignored) {
                }
            }
        }
    }

    /**
     * Metodo que realiza la compra
     * @param compra
     * @param consumible
     */
    public void realizarCompra(Compra compra, List<Consumible> consumible) {
        for (Consumible c : consumible) {
            compra.addConsumible(c);
        }
        compraService.efectuarCompra(compra);
    }
}
