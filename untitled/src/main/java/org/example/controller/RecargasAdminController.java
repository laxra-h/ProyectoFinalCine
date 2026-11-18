package org.example.controller;

import org.example.model.Cine;
import org.example.model.tarjeta.EstrategiaMetodoPago;
import org.example.model.tarjeta.MovimientoTarjeta;
import org.example.model.tarjeta.PagoEfectivo;
import org.example.model.tarjeta.PagoPSE;
import org.example.model.tarjeta.PagoTarjetaCredito;
import org.example.model.tarjeta.TipoMovimiento;
import org.example.model.tarjeta.TarjetaVirtual;
import org.example.model.usuarios.Cliente;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecargasAdminController {

    private final Cine cine = Cine.getInstance();

    public record ResultadoOperacion(boolean exitoso, String mensaje) {
    }

    public List<Cliente> obtenerSolicitudesPendientes() {
        List<Cliente> solicitudes = new ArrayList<>();
        for (Cliente cliente : cine.getListClientes()) {
            if (cliente != null && cliente.getTheTarjeta() != null
                    && cliente.getTheTarjeta().isRecargaSolicitada()) {
                solicitudes.add(cliente);
            }
        }
        return solicitudes;
    }

    public String obtenerNombreCliente(Cliente cliente) {
        return cliente == null || cliente.getNombre() == null
                ? "Cliente no disponible" : cliente.getNombre();
    }

    public String obtenerIdentificacion(Cliente cliente) {
        return cliente == null || cliente.getId() == null ? "—" : cliente.getId();
    }

    public String obtenerNumeroTarjeta(Cliente cliente) {
        TarjetaVirtual tarjeta = obtenerTarjeta(cliente);
        return tarjeta == null || tarjeta.getNumeroTarjeta() == null
                ? "—" : tarjeta.getNumeroTarjeta();
    }

    public double obtenerSaldoActual(Cliente cliente) {
        TarjetaVirtual tarjeta = obtenerTarjeta(cliente);
        return tarjeta == null ? 0 : tarjeta.getSaldo();
    }

    public double obtenerMontoSolicitado(Cliente cliente) {
        TarjetaVirtual tarjeta = obtenerTarjeta(cliente);
        return tarjeta == null || !tarjeta.isRecargaSolicitada()
                ? 0 : tarjeta.getMontoSolicitado();
    }

    public String obtenerMetodoPago(Cliente cliente) {
        TarjetaVirtual tarjeta = obtenerTarjeta(cliente);
        EstrategiaMetodoPago estrategia = tarjeta == null ? null : tarjeta.getEstrategiaPago();
        if (estrategia instanceof PagoPSE) {
            return "PSE";
        }
        if (estrategia instanceof PagoTarjetaCredito) {
            return "Tarjeta de crédito";
        }
        if (estrategia instanceof PagoEfectivo) {
            return "Efectivo";
        }
        return estrategia == null ? "No especificado" : estrategia.getClass().getSimpleName();
    }

    public boolean tarjetaActiva(Cliente cliente) {
        TarjetaVirtual tarjeta = obtenerTarjeta(cliente);
        return tarjeta != null && tarjeta.isActiva();
    }

    public ResultadoOperacion aprobarSolicitud(Cliente cliente) {
        ResultadoOperacion validacion = validarSolicitud(cliente);
        if (!validacion.exitoso()) {
            return validacion;
        }

        TarjetaVirtual tarjeta = cliente.getTheTarjeta();
        if (!tarjeta.isActiva()) {
            return new ResultadoOperacion(false,
                    "No se puede aprobar la recarga porque la tarjeta del cliente está inactiva.");
        }

        double monto = tarjeta.getMontoSolicitado();
        double saldoAnterior = tarjeta.getSaldo();
        int movimientosAnteriores = tarjeta.getHistorialMovimientos().size();

        tarjeta.recargar(monto);

        boolean movimientoRegistrado = tarjeta.getHistorialMovimientos().size() == movimientosAnteriores + 1
                && tarjeta.getHistorialMovimientos().get(tarjeta.getHistorialMovimientos().size() - 1)
                .getTipo() == TipoMovimiento.RECARGA;
        boolean saldoActualizado = Math.abs(tarjeta.getSaldo() - (saldoAnterior + monto)) < 0.000001;
        boolean solicitudCerrada = !tarjeta.isRecargaSolicitada() && tarjeta.getMontoSolicitado() == 0;

        if (!movimientoRegistrado || !saldoActualizado || !solicitudCerrada) {
            return new ResultadoOperacion(false,
                    "No fue posible confirmar la recarga. Verifica el estado de la tarjeta y su historial.");
        }

        cliente.actualizar("Tu solicitud de recarga por " + formatearDinero(monto)
                + " fue aprobada. Tu nuevo saldo es " + formatearDinero(tarjeta.getSaldo()) + ".");

        return new ResultadoOperacion(true,
                "Recarga aprobada correctamente. Se agregaron " + formatearDinero(monto)
                        + " a la tarjeta virtual de " + obtenerNombreCliente(cliente) + ".");
    }

    public ResultadoOperacion rechazarSolicitud(Cliente cliente) {
        ResultadoOperacion validacion = validarSolicitud(cliente);
        if (!validacion.exitoso()) {
            return validacion;
        }

        TarjetaVirtual tarjeta = cliente.getTheTarjeta();
        double monto = tarjeta.getMontoSolicitado();

        tarjeta.setRecargaSolicitada(false);
        tarjeta.setMontoSolicitado(0);
        tarjeta.setEstrategiaPago(null);

        cliente.actualizar("Tu solicitud de recarga por " + formatearDinero(monto)
                + " fue rechazada por el administrador.");

        return new ResultadoOperacion(true,
                "Solicitud de recarga rechazada. No se modificó el saldo de la tarjeta.");
    }

    public double calcularTotalSolicitado() {
        double total = 0;
        for (Cliente cliente : obtenerSolicitudesPendientes()) {
            total += obtenerMontoSolicitado(cliente);
        }
        return total;
    }

    private ResultadoOperacion validarSolicitud(Cliente cliente) {
        if (cliente == null || !cine.getListClientes().contains(cliente)) {
            return new ResultadoOperacion(false, "Selecciona una solicitud de un cliente registrado.");
        }

        TarjetaVirtual tarjeta = cliente.getTheTarjeta();
        if (tarjeta == null || !tarjeta.isRecargaSolicitada()) {
            return new ResultadoOperacion(false, "Este cliente no tiene una solicitud de recarga pendiente.");
        }

        if (!Double.isFinite(tarjeta.getMontoSolicitado()) || tarjeta.getMontoSolicitado() <= 0) {
            return new ResultadoOperacion(false, "La solicitud contiene un monto no válido.");
        }

        if (tarjeta.getEstrategiaPago() == null) {
            return new ResultadoOperacion(false, "La solicitud no tiene un método de pago registrado.");
        }

        if (tarjeta.getHistorialMovimientos() == null) {
            return new ResultadoOperacion(false, "No se encontró el historial de movimientos de la tarjeta.");
        }

        return new ResultadoOperacion(true, "Solicitud válida.");
    }

    private TarjetaVirtual obtenerTarjeta(Cliente cliente) {
        return cliente == null ? null : cliente.getTheTarjeta();
    }

    private String formatearDinero(double monto) {
        return String.format(Locale.forLanguageTag("es-CO"), "$%,.0f", monto);
    }
}
