package org.example.model.tarjeta;

public interface EstrategiaMetodoPago {

    /**
     * Procesa el pago para la recarga del saldo virtual
     * @param monto
     * @return descripcion del pago procesado
     */
    public String procesarPago(double monto);
}
