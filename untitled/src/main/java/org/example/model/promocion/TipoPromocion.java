package org.example.model.promocion;

public enum TipoPromocion {
    ENTRADAS("Entradas"),
    CONFITERIA("Confitería"),
    COMPRA_TOTAL("Compra completa");

    private final String etiqueta;

    TipoPromocion(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
