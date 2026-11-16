package org.example.services;

import org.example.model.Cine;
import org.example.model.compra.Compra;
import org.example.model.funcionDecorator.EstadoFuncion;
import org.example.model.funcionDecorator.Funcion;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AdministradorService {

    Cine cine = Cine.getInstance();

    /**
     * Metodo para cambiar el estado y notificar a los observadores
     * @param funcion
     * @param estado
     */
    public void cambiarEstadoFuncion(Funcion funcion, EstadoFuncion estado){
    funcion.setEstado(estado);
    funcion.notificar("El estado de la funcion " + funcion.getCodigo() + ", ha sido cambiada a " + estado);
}


    /**
     * Metodo para consultar compras por una fecha estadistica
     * @param fechaInicio
     * @param fechaFin
     * @return
     */
    public List<Compra> consultarComprasFecha(LocalDate fechaInicio, LocalDate fechaFin) {
        List<Compra> comprasEncontradas = new ArrayList<>();

        for (Compra c : cine.getListCompras()) {
            LocalDate fechaCompra = c.getFechaCompra();
            if (!fechaCompra.isBefore(fechaInicio) &&
                    !fechaCompra.isAfter(fechaFin)) {
                comprasEncontradas.add(c);
            }
        }

        return comprasEncontradas;
    }

    /**
     * Metodo para generar el reporte de compras por un periodo de tiempo
     * @param fechaInicio
     * @param fechaFin
     * @return
     */
    public String generarReporteCompras(LocalDate fechaInicio, LocalDate fechaFin) {

        List<Compra> compras = consultarComprasFecha(fechaInicio, fechaFin);

        if (compras.isEmpty()) {
            return "REPORTE DE COMPRAS\n" +
                    "No se encontraron compras en el periodo seleccionado.";
        }

        double ingresosTotales = 0;
        Compra compraMayor = compras.get(0);
        Compra compraMenor = compras.get(0);

        for (Compra c : compras) {

            ingresosTotales += c.getTotal();

            if (c.getTotal() > compraMayor.getTotal()) {
                compraMayor = c;
            }

            if (c.getTotal() < compraMenor.getTotal()) {
                compraMenor = c;
            }
        }

        double promedioCompra = ingresosTotales / compras.size();

        return "Cantidad de compras: " + compras.size() + "\nTotal de compras: " + ingresosTotales +
                "\nPromedio de compras: " + promedioCompra + "\nCompra mayor: " + compraMayor.getTotal() + "\nCompra menor: " + compraMenor.getTotal();


    }



    public String generarReporte(LocalDate fechaInicio, LocalDate fechaFin) {
        List<Compra> listCompras = consultarComprasFecha(fechaInicio, fechaFin);

    }


}
