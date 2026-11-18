package org.example.model.reporteAdapter;

public enum TipoReporte {

    VENTAS_PERIODO("Ventas realizadas por periodo"),
    ENTRADAS_PELICULA_FUNCION("Entradas vendidas por película o función"),
    OCUPACION_SALAS("Ocupación de las salas"),
    CONFITERIA_VENDIDA("Productos y combos vendidos"),
    RECARGAS_REALIZADAS("Recargas realizadas"),
    CANCELACIONES_REEMBOLSOS("Cancelaciones y reembolsos"),
    PUNTOS_CLIENTES("Puntos acumulados y redimidos por los clientes");

    private final String titulo;

    TipoReporte(String titulo) {
        this.titulo = titulo;
    }

    public String getTitulo() {
        return titulo;
    }

    @Override
    public String toString() {
        return titulo;
    }
}
