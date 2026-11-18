package org.example.services;

import org.example.model.Cine;
import org.example.model.Ticket;
import org.example.model.compra.Compra;
import org.example.model.consumibleComposite.Consumible;
import org.example.model.funcionDecorator.EstadoFuncion;
import org.example.model.funcionDecorator.Funcion;
import org.example.model.pelicula.Pelicula;
import org.example.model.reporteAdapter.ExportadorReporte;
import org.example.model.reporteAdapter.TipoReporte;
import org.example.model.salaPrototype.Asiento;
import org.example.model.salaPrototype.Sala;
import org.example.model.tarjeta.MovimientoTarjeta;
import org.example.model.tarjeta.TipoMovimiento;
import org.example.model.usuarios.Cliente;
import org.example.model.usuarios.RegistroPuntos;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdministradorService {

    Cine cine = Cine.getInstance();
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Metodo para cambiar el estado y notificar a los observadores
     * @param funcion
     * @param estado
     */
    public void cambiarEstadoFuncion(Funcion funcion, EstadoFuncion estado) {
        funcion.setEstado(estado);
        funcion.notificar("El estado de la funcion " + funcion.getCodigo() + ", ha sido cambiada a " + estado);
    }

    /**
     * Metodo para recargar tarjeta
     * @param monto
     * @param id
     */
    public void recargarTarjeta(double monto, String id) {
        Cliente cliente = buscarCLiente(id);

        if (cliente == null || cliente.getTheTarjeta() == null) {
            return;
        }
        if (!cliente.getTheTarjeta().isRecargaSolicitada()) {
            return;
        }
        if (cliente.getTheTarjeta().getMontoSolicitado() != monto) {
            return;
        }

        cliente.getTheTarjeta().recargar(monto);
        cliente.actualizar("Tu recarga por $" + monto + " ha sido aprobada.");
    }

    public Cliente buscarCLiente(String id) {
        for (Cliente c : cine.getListClientes()) {
            if (c.getId().equals(id)) {
                return c;
            }
        }
        return null;
    }

    /**
     * Metodo para consultar compras por una fecha estadistica
     * @param fechaInicio
     * @param fechaFin
     * @return
     */
    public List<Compra> consultarComprasFecha(LocalDate fechaInicio, LocalDate fechaFin) {
        List<Compra> comprasEncontradas = new ArrayList<>();
        for (Compra c : cine.getListCompras()) {
            LocalDate fechaCompra = c.getFechaCompra();
            if (fechaCompra != null && enRango(fechaCompra, fechaInicio, fechaFin)) {
                comprasEncontradas.add(c);
            }
        }
        return comprasEncontradas;
    }

    /**
     * Metodo para generar el reporte de compras por un periodo de tiempo
     * @param fechaInicio
     * @param fechaFin
     * @return
     */
    public String generarReporte(LocalDate fechaInicio, LocalDate fechaFin) {
        return generarReporte(TipoReporte.VENTAS_PERIODO, fechaInicio, fechaFin);
    }

    /**
     * Metodo para generar cualquiera de los 7 reportes operativos del cine
     * @param tipo
     * @param fechaInicio
     * @param fechaFin
     * @return
     */
    public String generarReporte(TipoReporte tipo, LocalDate fechaInicio, LocalDate fechaFin) {
        TipoReporte tipoSeguro = (tipo != null) ? tipo : TipoReporte.VENTAS_PERIODO;
        LocalDate inicio = (fechaInicio != null) ? fechaInicio : LocalDate.now().minusMonths(1);
        LocalDate fin = (fechaFin != null) ? fechaFin : LocalDate.now().plusMonths(1);

        StringBuilder sb = new StringBuilder();
        sb.append("Fecha de generación: ").append(LocalDate.now().format(FORMATO_FECHA)).append("\n");
        sb.append("Periodo consultado: ").append(inicio.format(FORMATO_FECHA))
                .append(" al ").append(fin.format(FORMATO_FECHA)).append("\n\n");

        switch (tipoSeguro) {
            case VENTAS_PERIODO -> construirReporteVentas(sb, inicio, fin);
            case ENTRADAS_PELICULA_FUNCION -> construirReporteEntradasPorPelicula(sb, inicio, fin);
            case OCUPACION_SALAS -> construirReporteOcupacionSalas(sb);
            case CONFITERIA_VENDIDA -> construirReporteConfiteria(sb, inicio, fin);
            case RECARGAS_REALIZADAS -> construirReporteRecargas(sb, inicio, fin);
            case CANCELACIONES_REEMBOLSOS -> construirReporteCancelaciones(sb, inicio, fin);
            case PUNTOS_CLIENTES -> construirReportePuntos(sb);
        }
        return sb.toString();
    }

    /**
     * Metodo para exportar un reporte usando el exportador seleccionado (TXT o Adapter PDF)
     * @param tipo
     * @param fechaInicio
     * @param fechaFin
     * @param exportador
     * @param destino
     * @return
     * @throws IOException
     */
    public File exportarReporte(
            TipoReporte tipo,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            ExportadorReporte exportador,
            File destino
    ) throws IOException {
        String titulo = "Reporte - " + tipo.getTitulo() + " - CinemaUQ";
        String contenido = generarReporte(tipo, fechaInicio, fechaFin);
        return exportador.exportar(titulo, contenido, destino);
    }

    private void construirReporteVentas(StringBuilder sb, LocalDate inicio, LocalDate fin) {
        List<Compra> compras = consultarComprasFecha(inicio, fin);
        sb.append(String.format("%-10s %-20s %-12s %-12s %-14s%n",
                "Código", "Cliente", "Fecha", "Estado", "Total"));
        sb.append("-".repeat(72)).append("\n");

        double totalVentasPagadas = 0;
        int pagadas = 0;
        for (Compra c : compras) {
            String cliente = (c.getTheCliente() != null) ? c.getTheCliente().getNombre() : "N/A";
            String estado = (c.getTheEstadoCompra() != null) ? c.getTheEstadoCompra().getNombre() : "N/A";
            sb.append(String.format("%-10s %-20s %-12s %-12s %-14s%n",
                    c.getCodigo(),
                    recortar(cliente, 19),
                    c.getFechaCompra().format(FORMATO_FECHA),
                    estado,
                    formatearMoneda(c.getTotal())));
            if ("PAGADA".equals(estado)) {
                totalVentasPagadas += c.getTotal();
                pagadas++;
            }
        }
        sb.append("\nRegistros encontrados: ").append(compras.size()).append("\n");
        sb.append("Compras pagadas: ").append(pagadas).append("\n");
        sb.append("Total ventas efectivas: ").append(formatearMoneda(totalVentasPagadas)).append("\n");
    }

    private void construirReporteEntradasPorPelicula(StringBuilder sb, LocalDate inicio, LocalDate fin) {
        Map<String, int[]> conteoPorPelicula = new LinkedHashMap<>();
        for (Pelicula p : cine.getListPeliculas()) {
            conteoPorPelicula.put(p.getNombre(), new int[]{0, 0});
        }

        int totalEntradas = 0;
        double totalRecaudo = 0;

        for (Compra c : consultarComprasFecha(inicio, fin)) {
            if (c.getTheEstadoCompra() == null || !"PAGADA".equals(c.getTheEstadoCompra().getNombre())) {
                continue;
            }
            for (Ticket t : c.getListaTickets()) {
                if (t.getTheFuncion() != null && t.getTheFuncion().getPelicula() != null) {
                    String nombre = t.getTheFuncion().getPelicula().getNombre();
                    int[] datos = conteoPorPelicula.computeIfAbsent(nombre, k -> new int[]{0, 0});
                    datos[0]++;
                    datos[1] += (int) t.getPrecio();
                    totalEntradas++;
                    totalRecaudo += t.getPrecio();
                }
            }
        }

        sb.append(String.format("%-28s %-14s %-16s%n", "Película", "Entradas", "Total"));
        sb.append("-".repeat(60)).append("\n");
        for (Map.Entry<String, int[]> entry : conteoPorPelicula.entrySet()) {
            sb.append(String.format("%-28s %-14d %-16s%n",
                    recortar(entry.getKey(), 27),
                    entry.getValue()[0],
                    formatearMoneda(entry.getValue()[1])));
        }
        sb.append("\nTotal entradas: ").append(totalEntradas).append("\n");
        sb.append("Total ventas boletería: ").append(formatearMoneda(totalRecaudo)).append("\n");
    }

    private void construirReporteOcupacionSalas(StringBuilder sb) {
        sb.append(String.format("%-12s %-12s %-14s %-16s %-14s%n",
                "Función", "Sala", "Capacidad", "Ocupados", "Ocupación"));
        sb.append("-".repeat(70)).append("\n");

        int capacidadTotal = 0;
        int ocupadosTotal = 0;

        for (Funcion f : cine.getListFunciones()) {
            Sala sala = f.getSala();
            Asiento[] asientos = (f.getAsientos() != null) ? f.getAsientos()
                    : (sala != null ? sala.getListAsientos() : null);
            int cap = (asientos != null) ? asientos.length : 0;
            int ocupados = 0;
            if (asientos != null) {
                for (Asiento a : asientos) {
                    if (a != null && !a.isDisponible()) {
                        ocupados++;
                    }
                }
            }
            double porcentaje = (cap > 0) ? (ocupados * 100.0) / cap : 0;
            sb.append(String.format("%-12s %-12s %-14d %-16d %-13.1f%%%n",
                    f.getCodigo(),
                    sala != null ? sala.getNumeroSala() : "N/A",
                    cap,
                    ocupados,
                    porcentaje));
            capacidadTotal += cap;
            ocupadosTotal += ocupados;
        }
        double porcentajeGlobal = (capacidadTotal > 0) ? (ocupadosTotal * 100.0) / capacidadTotal : 0;
        sb.append("\nFunciones registradas: ").append(cine.getListFunciones().size()).append("\n");
        sb.append("Asientos ocupados / capacidad total: ").append(ocupadosTotal)
                .append(" / ").append(capacidadTotal)
                .append(String.format(" (%.1f%%)%n", porcentajeGlobal));
    }

    private void construirReporteConfiteria(StringBuilder sb, LocalDate inicio, LocalDate fin) {
        Map<String, double[]> vendidos = new LinkedHashMap<>();
        int unidadesTotales = 0;
        double recaudoTotal = 0;

        for (Compra c : consultarComprasFecha(inicio, fin)) {
            if (c.getTheEstadoCompra() == null || !"PAGADA".equals(c.getTheEstadoCompra().getNombre())) {
                continue;
            }
            for (Consumible item : c.getListConsumibles()) {
                double[] datos = vendidos.computeIfAbsent(item.getNombre(), k -> new double[]{0, 0});
                datos[0] += 1;
                datos[1] += item.calcularCosto();
                unidadesTotales++;
                recaudoTotal += item.calcularCosto();
            }
        }

        sb.append(String.format("%-24s %-14s %-16s%n", "Producto / Combo", "Cantidad", "Total"));
        sb.append("-".repeat(56)).append("\n");
        for (Map.Entry<String, double[]> entry : vendidos.entrySet()) {
            sb.append(String.format("%-24s %-14d %-16s%n",
                    recortar(entry.getKey(), 23),
                    (int) entry.getValue()[0],
                    formatearMoneda(entry.getValue()[1])));
        }
        sb.append("\nTotal unidades vendidas: ").append(unidadesTotales).append("\n");
        sb.append("Total ventas confitería: ").append(formatearMoneda(recaudoTotal)).append("\n");
    }

    private void construirReporteRecargas(StringBuilder sb, LocalDate inicio, LocalDate fin) {
        sb.append(String.format("%-20s %-12s %-14s %-24s%n", "Cliente", "Fecha", "Monto", "Detalle"));
        sb.append("-".repeat(72)).append("\n");

        int cantidad = 0;
        double montoTotal = 0;

        for (Cliente cliente : cine.getListClientes()) {
            if (cliente.getTheTarjeta() == null) {
                continue;
            }
            for (MovimientoTarjeta mov : cliente.getTheTarjeta().getHistorialMovimientos()) {
                if (mov.getTipo() == TipoMovimiento.RECARGA && enRango(mov.getFecha(), inicio, fin)) {
                    sb.append(String.format("%-20s %-12s %-14s %-24s%n",
                            recortar(cliente.getNombre(), 19),
                            mov.getFecha().format(FORMATO_FECHA),
                            formatearMoneda(mov.getMonto()),
                            recortar(mov.getDescripcion(), 23)));
                    cantidad++;
                    montoTotal += mov.getMonto();
                }
            }
        }
        sb.append("\nTotal recargas realizadas: ").append(cantidad).append("\n");
        sb.append("Monto total recargado: ").append(formatearMoneda(montoTotal)).append("\n");
    }

    private void construirReporteCancelaciones(StringBuilder sb, LocalDate inicio, LocalDate fin) {
        sb.append(String.format("%-10s %-20s %-12s %-14s %-14s%n",
                "Código", "Cliente", "Fecha", "Estado", "Valor"));
        sb.append("-".repeat(72)).append("\n");

        int canceladas = 0;
        int reembolsadas = 0;
        double totalReembolsado = 0;

        for (Compra c : consultarComprasFecha(inicio, fin)) {
            String estado = (c.getTheEstadoCompra() != null) ? c.getTheEstadoCompra().getNombre() : "";
            if ("CANCELADA".equals(estado) || "REEMBOLSADA".equals(estado)) {
                String cliente = (c.getTheCliente() != null) ? c.getTheCliente().getNombre() : "N/A";
                sb.append(String.format("%-10s %-20s %-12s %-14s %-14s%n",
                        c.getCodigo(),
                        recortar(cliente, 19),
                        c.getFechaCompra().format(FORMATO_FECHA),
                        estado,
                        formatearMoneda(c.getMontoPagado())));
                if ("CANCELADA".equals(estado)) {
                    canceladas++;
                } else {
                    reembolsadas++;
                    totalReembolsado += c.getMontoPagado();
                }
            }
        }
        sb.append("\nCompras canceladas: ").append(canceladas).append("\n");
        sb.append("Compras reembolsadas: ").append(reembolsadas).append("\n");
        sb.append("Total dinero reembolsado: ").append(formatearMoneda(totalReembolsado)).append("\n");
    }

    private void construirReportePuntos(StringBuilder sb) {
        sb.append(String.format("%-22s %-16s %-16s %-16s%n",
                "Cliente", "Acumulados", "Redimidos", "Vigentes hoy"));
        sb.append("-".repeat(70)).append("\n");

        int totalAcumulados = 0;
        int totalRedimidos = 0;
        int totalVigentes = 0;

        for (Cliente cliente : cine.getListClientes()) {
            int acumulados = 0;
            int redimidos = 0;
            for (RegistroPuntos reg : cliente.getHistorialPuntos()) {
                acumulados += reg.getPuntosObtenidos();
                redimidos += (reg.getPuntosObtenidos() - reg.getPuntosDisponibles());
            }
            int vigentes = cliente.getPuntos();
            sb.append(String.format("%-22s %-16d %-16d %-16d%n",
                    recortar(cliente.getNombre(), 21),
                    acumulados,
                    redimidos,
                    vigentes));
            totalAcumulados += acumulados;
            totalRedimidos += redimidos;
            totalVigentes += vigentes;
        }
        sb.append("\nClientes registrados: ").append(cine.getListClientes().size()).append("\n");
        sb.append("Total puntos acumulados: ").append(totalAcumulados).append("\n");
        sb.append("Total puntos redimidos: ").append(totalRedimidos).append("\n");
        sb.append("Total puntos vigentes disponibles: ").append(totalVigentes).append("\n");
    }

    private boolean enRango(LocalDate fecha, LocalDate inicio, LocalDate fin) {
        if (fecha == null) {
            return false;
        }
        boolean despuesDeInicio = (inicio == null) || !fecha.isBefore(inicio);
        boolean antesDeFin = (fin == null) || !fecha.isAfter(fin);
        return despuesDeInicio && antesDeFin;
    }

    private String formatearMoneda(double valor) {
        return String.format(Locale.forLanguageTag("es-CO"), "$%,.0f", valor);
    }

    private String recortar(String texto, int max) {
        if (texto == null) {
            return "";
        }
        return texto.length() <= max ? texto : texto.substring(0, max - 1) + "…";
    }
}
