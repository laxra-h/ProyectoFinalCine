package org.example.model.tarjeta;

public class PagoPSE implements EstrategiaMetodoPago {

    @Override
    public String procesarPago(double monto) {
        return "Recarga por PSE aprobada por $" + monto;
    }
}
