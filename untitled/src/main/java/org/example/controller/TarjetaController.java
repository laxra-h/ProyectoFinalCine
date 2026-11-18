package org.example.controller;

import org.example.model.tarjeta.TarjetaVirtual;
import org.example.model.tarjeta.EstrategiaMetodoPago;
import org.example.model.tarjeta.MovimientoTarjeta;
import org.example.model.usuarios.Cliente;

import java.util.ArrayList;
import java.util.List;

public class TarjetaController {

    /**
     * Metodo para obtener la tarjeta virtual del cliente
     * @param cliente
     * @return tarjeta virtual del cliente
     */
    public TarjetaVirtual obtenerTarjeta(Cliente cliente) {
        if (cliente == null) {
            return null;
        }
        return cliente.getTheTarjeta();
    }

    /**
     * Metodo para consultar el saldo de la tarjeta
     * @param cliente
     * @return saldo disponible
     */
    public double obtenerSaldo(Cliente cliente) {
        TarjetaVirtual tarjeta = obtenerTarjeta(cliente);

        if (tarjeta == null) {
            return 0;
        }
        return tarjeta.getSaldo();
    }

    /**
     * Metodo para consultar el estado de la tarjeta
     * @param cliente
     * @return estado de la tarjeta
     */
    public String obtenerEstado(Cliente cliente) {
        TarjetaVirtual tarjeta = obtenerTarjeta(cliente);

        if (tarjeta == null) {
            return "SIN TARJETA";
        }
        return tarjeta.isActiva() ? "ACTIVA" : "INACTIVA";
    }

    /**
     * Metodo para obtener los movimientos de la tarjeta
     * @param cliente
     * @return historial de movimientos
     */
    public List<MovimientoTarjeta> obtenerHistorial(Cliente cliente) {
        TarjetaVirtual tarjeta = obtenerTarjeta(cliente);

        if (tarjeta == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(tarjeta.getHistorialMovimientos());
    }

    /**
     * Metodo para solicitar una recarga de la tarjeta
     * @param cliente
     * @param monto
     * @param estrategia
     * @return true si la solicitud fue registrada
     */
    public boolean solicitarRecarga(Cliente cliente, double monto, EstrategiaMetodoPago estrategia) {

        TarjetaVirtual tarjeta = obtenerTarjeta(cliente);

        if (tarjeta == null || !tarjeta.isActiva()) {
            return false;
        }

        if (monto <= 0 || estrategia == null) {
            return false;
        }

        if (tarjeta.isRecargaSolicitada()) {
            return false;
        }

        tarjeta.solicitarRecarga(monto, estrategia);

        return tarjeta.isRecargaSolicitada();
    }
}