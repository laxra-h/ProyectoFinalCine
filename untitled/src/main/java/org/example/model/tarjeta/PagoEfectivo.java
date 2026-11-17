package org.example.model.tarjeta;

public class PagoEfectivo implements EstrategiaMetodoPago {

    @Override
    public String procesarPago(double monto) {
        return "Recarga en Efectivo aprobada por $" + monto;
    }
}
