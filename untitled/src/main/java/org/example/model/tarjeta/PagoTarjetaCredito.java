package org.example.model.tarjeta;

public class PagoTarjetaCredito implements EstrategiaMetodoPago {

    @Override
    public String procesarPago(double monto) {
        return "Recarga con Tarjeta de Credito aprobada por $" + monto;
    }
}
