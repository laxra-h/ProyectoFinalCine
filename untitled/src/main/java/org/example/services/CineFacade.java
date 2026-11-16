package org.example.services;

import org.example.model.Cine;
import org.example.model.compra.Compra;
import org.example.model.compra.estadoCompra.EstadoCancelada;
import org.example.model.consumibleComposite.Consumible;
import org.example.model.funcionDecorator.EstadoFuncion;
import org.example.model.funcionDecorator.Funcion;
import org.example.model.salaPrototype.Asiento;

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
     * Metodo que permite al aministrador cancelar funcion y hacer los reembolsos de manera automatica
     * @param funcion
     * @param estadoFuncion
     */
    public void cancelarFuncion(Funcion funcion, EstadoFuncion estadoFuncion) {
        administradorService.cambiarEstadoFuncion(funcion,estadoFuncion);
        for (Compra c : cine.getListCompras()){
            if (c.getListaTickets().get(0).getTheFuncion() == funcion){
                compraService.realizarReembolso(c.getTheCliente(), c.getCodigo());
                c.setTheEstadoCompra(new EstadoCancelada());
            }
        }
    }

    /**
     * Metodo que realiza la compra
     * @param compra
     * @param consumible
     */
    public void realizarCompra(Compra compra, List<Consumible> consumible) {
        for (Consumible c : consumible){
            compra.addConsumible(c);
        }
        compraService.efectuarCompra(compra);
    }


}
