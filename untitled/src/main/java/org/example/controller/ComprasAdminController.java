package org.example.controller;

import org.example.model.Cine;
import org.example.model.Ticket;
import org.example.model.compra.Compra;
import org.example.model.compra.estadoCompra.EstadoPagada;
import org.example.model.compra.estadoCompra.EstadoReembolsada;
import org.example.model.compra.estrategiaReembolso.ReembolsoCompleto;
import org.example.model.compra.estrategiaReembolso.SinReembolso;
import org.example.model.funcionDecorator.EstadoFuncion;
import org.example.model.funcionDecorator.Funcion;
import org.example.model.usuarios.Cliente;
import org.example.model.tarjeta.MovimientoTarjeta;
import org.example.model.tarjeta.TipoMovimiento;

import java.util.ArrayList;
import java.util.List;

public class ComprasAdminController {

    private final Cine cine = Cine.getInstance();
    private final ComprasController consultas = new ComprasController();

    public record ResultadoOperacion(boolean exitoso, String mensaje) {
    }

    public List<Compra> obtenerCompras() {
        return new ArrayList<>(cine.getListCompras());
    }

    public List<Compra> obtenerSolicitudesReembolso() {
        return new ArrayList<>(cine.getListSolicitudesReembolso());
    }

    public boolean tieneSolicitudReembolso(Compra compra) {
        return compra != null && cine.getListSolicitudesReembolso().contains(compra);
    }

    public String obtenerNombreCliente(Compra compra) {
        Cliente cliente = compra != null ? compra.getTheCliente() : null;
        return cliente != null ? cliente.getNombre() : "Cliente no disponible";
    }

    public String obtenerIdentificacionCliente(Compra compra) {
        Cliente cliente = compra != null ? compra.getTheCliente() : null;
        return cliente != null ? cliente.getId() : "—";
    }

    public int obtenerCantidadEntradas(Compra compra) {
        return consultas.obtenerCantidadEntradas(compra);
    }

    public String obtenerEstado(Compra compra) {
        return consultas.obtenerEstado(compra);
    }

    public String obtenerDetalleEntradas(Compra compra) {
        return consultas.obtenerDetalleEntradas(compra);
    }

    public String obtenerDetalleConfiteria(Compra compra) {
        return consultas.obtenerDetalleConsumibles(compra);
    }

    public double obtenerMontoRealmentePagado(Compra compra) {
        if (compra == null) {
            return 0;
        }

        Cliente cliente = compra.getTheCliente();
        if (cliente == null || cliente.getTheTarjeta() == null
                || cliente.getTheTarjeta().getHistorialMovimientos() == null) {
            return compra.getTotal();
        }

        String descripcion = "Compra " + compra.getCodigo();
        for (MovimientoTarjeta movimiento : cliente.getTheTarjeta().getHistorialMovimientos()) {
            if (movimiento.getTipo() == TipoMovimiento.COMPRA
                    && descripcion.equals(movimiento.getDescripcion())) {
                return movimiento.getMonto();
            }
        }

        return compra.getTotal();
    }

    public double calcularReembolsoEstimado(Compra compra) {
        if (compra == null || !(compra.getTheEstadoCompra() instanceof EstadoPagada)
                || compra.getListaTickets() == null || compra.getListaTickets().isEmpty()) {
            return 0;
        }

        Funcion funcion = compra.getListaTickets().get(0).getTheFuncion();
        if (funcion == null) {
            return 0;
        }

        if (funcion.getEstado() == EstadoFuncion.CANCELADA) {
            return new ReembolsoCompleto().calcularReembolso(obtenerMontoRealmentePagado(compra));
        }

        compra.definirEstrategiaReembolso();
        if (compra.getTheEstrategiaReembolso() == null) {
            return 0;
        }

        return compra.getTheEstrategiaReembolso()
                .calcularReembolso(obtenerMontoRealmentePagado(compra));
    }

    public ResultadoOperacion aprobarSolicitud(Compra compra) {
        if (!tieneSolicitudReembolso(compra)) {
            return new ResultadoOperacion(false, "La compra no tiene una solicitud pendiente.");
        }

        if (!(compra.getTheEstadoCompra() instanceof EstadoPagada)) {
            cine.getListSolicitudesReembolso().remove(compra);
            return new ResultadoOperacion(false, "La compra ya no se encuentra en estado PAGADA.");
        }

        if (compra.getListaTickets() == null || compra.getListaTickets().isEmpty()
                || compra.getListaTickets().get(0).getTheFuncion() == null) {
            return new ResultadoOperacion(false, "No se puede procesar la solicitud porque la compra no tiene una función asociada.");
        }

        if (compra.getTheCliente() == null || compra.getTheCliente().getTheTarjeta() == null) {
            return new ResultadoOperacion(false, "No se encontró la tarjeta virtual del cliente para realizar el reembolso.");
        }

        Funcion funcion = compra.getListaTickets().get(0).getTheFuncion();
        if (funcion.getEstado() == EstadoFuncion.CANCELADA) {
            compra.setTheEstrategiaReembolso(new ReembolsoCompleto());
        } else {
            compra.definirEstrategiaReembolso();
        }

        if (compra.getTheEstrategiaReembolso() instanceof SinReembolso) {
            cine.getListSolicitudesReembolso().remove(compra);
            if (compra.getTheCliente() != null) {
                compra.getTheCliente().actualizar(
                        "La solicitud de cancelación de la compra " + compra.getCodigo()
                                + " no fue aprobada porque faltan menos de 2 horas para la función."
                );
            }
            return new ResultadoOperacion(false,
                    "La solicitud no procede: faltan menos de 2 horas para la función. La compra continúa PAGADA.");
        }

        double montoReembolso = calcularMontoConEstrategia(compra);
        if (montoReembolso <= 0) {
            return new ResultadoOperacion(false, "No fue posible calcular un valor de reembolso válido.");
        }

        compra.cancelar();

        if (compra.getTheEstadoCompra() instanceof EstadoReembolsada) {
            cine.getListSolicitudesReembolso().remove(compra);
            return new ResultadoOperacion(true,
                    "Solicitud aprobada. Se reembolsaron " + String.format("$%,.0f", montoReembolso)
                            + " a la tarjeta virtual del cliente.");
        }

        return new ResultadoOperacion(false,
                "No se pudo completar el reembolso. La solicitud permanece pendiente.");
    }

    public ResultadoOperacion rechazarSolicitud(Compra compra) {
        if (!tieneSolicitudReembolso(compra)) {
            return new ResultadoOperacion(false, "La compra no tiene una solicitud pendiente.");
        }

        cine.getListSolicitudesReembolso().remove(compra);
        if (compra.getTheCliente() != null) {
            compra.getTheCliente().actualizar(
                    "La solicitud de cancelación de la compra " + compra.getCodigo()
                            + " fue rechazada por el administrador."
            );
        }

        return new ResultadoOperacion(true,
                "Solicitud rechazada. La compra conserva su estado actual y no se realizó ningún reembolso.");
    }

    private double calcularMontoConEstrategia(Compra compra) {
        double montoPagado = obtenerMontoRealmentePagado(compra);
        if (compra.getTheEstrategiaReembolso() == null) {
            return montoPagado;
        }
        return compra.getTheEstrategiaReembolso().calcularReembolso(montoPagado);
    }
}
