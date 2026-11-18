package org.example.viewController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import org.example.controller.ReportesAdminController;
import org.example.model.reporteAdapter.TipoReporte;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

public class ReportesAdminViewController {

    @FXML
    private ComboBox<TipoReporte> cbTipoReporte;

    @FXML
    private DatePicker dpFechaInicio;

    @FXML
    private DatePicker dpFechaFin;

    @FXML
    private TextArea txtVistaPrevia;

    @FXML
    private Label lblEstadoExportacion;

    @FXML
    private BarChart<String, Number> chartBarras;

    @FXML
    private PieChart chartPastel;

    @FXML
    private LineChart<String, Number> chartLineas;

    private final ReportesAdminController controller = new ReportesAdminController();

    @FXML
    public void initialize() {
        cbTipoReporte.setItems(FXCollections.observableArrayList(controller.obtenerTiposReporte()));
        cbTipoReporte.getSelectionModel().selectFirst();
        dpFechaInicio.setValue(LocalDate.now().minusDays(7));
        dpFechaFin.setValue(LocalDate.now().plusDays(7));

        cbTipoReporte.setOnAction(e -> actualizarVista());
        actualizarVista();
    }

    @FXML
    public void actualizarVista() {
        TipoReporte tipo = cbTipoReporte.getValue();
        LocalDate inicio = dpFechaInicio.getValue();
        LocalDate fin = dpFechaFin.getValue();

        txtVistaPrevia.setText(controller.generarTextoReporte(tipo, inicio, fin));
        cargarGraficos(inicio, fin);
    }

    @FXML
    public void exportarTxt() {
        exportarArchivo("txt", "Archivo de texto (*.txt)", "*.txt");
    }

    @FXML
    public void exportarPdf() {
        exportarArchivo("pdf", "Documento PDF (*.pdf)", "*.pdf");
    }

    private void exportarArchivo(String extension, String descripcionFiltro, String patronFiltro) {
        TipoReporte tipo = cbTipoReporte.getValue();
        LocalDate inicio = dpFechaInicio.getValue();
        LocalDate fin = dpFechaFin.getValue();

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Exportar reporte CinemaUQ");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(descripcionFiltro, patronFiltro));
        String nombreSugerido = "reporte_" + tipo.name().toLowerCase() + "." + extension;
        chooser.setInitialFileName(nombreSugerido);

        File destino = chooser.showSaveDialog(txtVistaPrevia.getScene().getWindow());
        if (destino == null) {
            return;
        }

        try {
            File generado = "pdf".equalsIgnoreCase(extension)
                    ? controller.exportarPdf(tipo, inicio, fin, destino)
                    : controller.exportarTxt(tipo, inicio, fin, destino);
            lblEstadoExportacion.setText("Exportado: " + generado.getName());
            mostrarAlerta(Alert.AlertType.INFORMATION, "Reporte exportado",
                    "Archivo generado exitosamente en:\n" + generado.getAbsolutePath());
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al exportar", e.getMessage());
        }
    }

    private void cargarGraficos(LocalDate inicio, LocalDate fin) {
        chartBarras.getData().clear();
        XYChart.Series<String, Number> serieBarras = new XYChart.Series<>();
        serieBarras.setName("Entradas");
        for (Map.Entry<String, Number> entry : controller.obtenerEntradasPorPelicula(inicio, fin).entrySet()) {
            serieBarras.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        chartBarras.getData().add(serieBarras);

        ObservableList<PieChart.Data> datosPastel = FXCollections.observableArrayList();
        for (Map.Entry<String, Double> entry : controller.obtenerDistribucionVentas(inicio, fin).entrySet()) {
            datosPastel.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }
        chartPastel.setData(datosPastel);

        chartLineas.getData().clear();
        XYChart.Series<String, Number> serieLineas = new XYChart.Series<>();
        serieLineas.setName("Ventas ($)");
        for (Map.Entry<String, Number> entry : controller.obtenerVentasPorFecha(inicio, fin).entrySet()) {
            serieLineas.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        chartLineas.getData().add(serieLineas);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
