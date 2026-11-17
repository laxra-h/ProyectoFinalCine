package org.example.model.tarjeta;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TarjetaVirtual {

    private String numeroTarjeta;
    private double saldo;
    private boolean activa;
    private boolean recargaSolicitada;
    private double montoSolicitado;
    private EstrategiaMetodoPago estrategiaPago;
    private List<MovimientoTarjeta> historialMovimientos;

    /**
     * Metodo constructor de la tarjeta virtual
     * @param numeroTarjeta
     * @param saldoInicial
     */
    public TarjetaVirtual(String numeroTarjeta, double saldoInicial) {
        this.numeroTarjeta = numeroTarjeta;
        this.saldo = saldoInicial;
        this.activa = true;
        this.recargaSolicitada = false;
        this.montoSolicitado = 0;
        this.estrategiaPago = new PagoPSE();
        this.historialMovimientos = new ArrayList<>();
    }

    /**
     * Permite al cliente dejar una solicitud de recarga pendiente para el administrador
     * @param monto
     * @param estrategia
     */
    public void solicitarRecarga(double monto, EstrategiaMetodoPago estrategia) {
        if (monto > 0) {
            this.montoSolicitado = monto;
            this.estrategiaPago = estrategia;
            this.recargaSolicitada = true;
        }
    }

    /**
     * Recarga el saldo de la tarjeta usando la estrategia de pago seleccionada
     * @param monto
     */
    public void recargar(double monto) {
        if (!activa || monto <= 0) {
            return;
        }
        String detalle = (estrategiaPago != null)
                ? estrategiaPago.procesarPago(monto)
                : "Recarga de saldo por $" + monto;

        this.saldo += monto;
        this.recargaSolicitada = false;
        this.montoSolicitado = 0;
        this.historialMovimientos.add(new MovimientoTarjeta(LocalDate.now(), TipoMovimiento.RECARGA, monto, detalle));
    }

    /**
     * Verifica si la tarjeta esta activa y tiene saldo suficiente para descontar una compra
     * @param monto
     * @param descripcion
     * @return true si el pago fue aprobado
     */
    public boolean pagar(double monto, String descripcion) {
        if (!activa || monto <= 0 || saldo < monto) {
            return false;}
        this.saldo -= monto;
        this.historialMovimientos.add(new MovimientoTarjeta(LocalDate.now(), TipoMovimiento.COMPRA, monto, descripcion));
        return true;
    }

    /**
     * Regresa el dinero de un reembolso a la tarjeta virtual
     * @param monto
     * @param descripcion
     */
    public void reembolsar(double monto, String descripcion) {
        if (monto <= 0) {
            return;
        }
        this.saldo += monto;
        this.historialMovimientos.add(new MovimientoTarjeta(LocalDate.now(), TipoMovimiento.REEMBOLSO, monto, descripcion));
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public boolean isRecargaSolicitada() {
        return recargaSolicitada;
    }

    public void setRecargaSolicitada(boolean recargaSolicitada) {
        this.recargaSolicitada = recargaSolicitada;
    }

    public double getMontoSolicitado() {
        return montoSolicitado;
    }

    public void setMontoSolicitado(double montoSolicitado) {
        this.montoSolicitado = montoSolicitado;
    }

    public EstrategiaMetodoPago getEstrategiaPago() {
        return estrategiaPago;
    }

    public void setEstrategiaPago(EstrategiaMetodoPago estrategiaPago) {
        this.estrategiaPago = estrategiaPago;
    }

    public List<MovimientoTarjeta> getHistorialMovimientos() {
        return historialMovimientos;
    }
}
