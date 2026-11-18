package org.example.viewController;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.example.controller.ComprasAdminController;
import org.example.controller.ComprasAdminController.ResultadoOperacion;
import org.example.model.compra.Compra;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ComprasAdminViewController {

    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cbEstado;
    @FXML private CheckBox chkSoloSolicitudes;
    @FXML private Label lblTotalCompras;
    @FXML private Label lblSolicitudesPendientes;

    @FXML private TableView<Compra> tablaCompras;
    @FXML private TableColumn<Compra, String> colCodigo;
    @FXML private TableColumn<Compra, String> colCliente;
    @FXML private TableColumn<Compra, String> colFecha;
    @FXML private TableColumn<Compra, String> colEntradas;
    @FXML private TableColumn<Compra, String> colTotal;
    @FXML private TableColumn<Compra, String> colEstado;
    @FXML private TableColumn<Compra, String> colSolicitud;

    @FXML private Label lblTituloDetalle;
    @FXML private Label lblClienteDetalle;
    @FXML private Label lblIdentificacionDetalle;
    @FXML private Label lblCodigoDetalle;
    @FXML private Label lblFechaDetalle;
    @FXML private Label lblEntradasDetalle;
    @FXML private Label lblPuntosDetalle;
    @FXML private Label lblEstadoDetalle;
    @FXML private Label lblSolicitudDetalle;
    @FXML private Label lblDetalleEntradas;
    @FXML private Label lblDetalleConfiteria;
    @FXML private Label lblTotalDetalle;
    @FXML private Label lblReembolsoEstimado;
    @FXML private Button btnAprobarSolicitud;
    @FXML private Button btnRechazarSolicitud;

    private final ComprasAdminController controller = new ComprasAdminController();
    private final ObservableList<Compra> compras = FXCollections.observableArrayList();
    private final ObservableList<Compra> comprasFiltradas = FXCollections.observableArrayList();
    private Compra compraSeleccionada;

    private final NumberFormat formatoMoneda =
            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO"));
    private final DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        formatoMoneda.setMaximumFractionDigits(0);
        formatoMoneda.setMinimumFractionDigits(0);

        configurarFiltros();
        configurarTabla();
        configurarSeleccion();

        tablaCompras.setItems(comprasFiltradas);
        limpiarDetalle();
        refrescarCompras();
    }

    private void configurarFiltros() {
        cbEstado.setItems(FXCollections.observableArrayList(
                "TODAS", "PAGADA", "PENDIENTE", "CANCELADA", "REEMBOLSADA"
        ));
        cbEstado.setValue("TODAS");

        txtBuscar.textProperty().addListener((observable, anterior, nuevo) -> aplicarFiltros());
        cbEstado.valueProperty().addListener((observable, anterior, nuevo) -> aplicarFiltros());
        chkSoloSolicitudes.selectedProperty().addListener(
                (observable, anterior, seleccionado) -> aplicarFiltros()
        );
    }

    private void configurarTabla() {
        colCodigo.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCodigo()));
        colCliente.setCellValueFactory(data ->
                new SimpleStringProperty(controller.obtenerNombreCliente(data.getValue())));
        colFecha.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFechaCompra() == null
                        ? "—" : data.getValue().getFechaCompra().format(formatoFecha)));
        colEntradas.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(
                        controller.obtenerCantidadEntradas(data.getValue()))));
        colTotal.setCellValueFactory(data ->
                new SimpleStringProperty(formatoMoneda.format(data.getValue().getTotal())));
        colEstado.setCellValueFactory(data ->
                new SimpleStringProperty(controller.obtenerEstado(data.getValue())));
        colSolicitud.setCellValueFactory(data ->
                new SimpleStringProperty(controller.tieneSolicitudReembolso(data.getValue())
                        ? "PENDIENTE" : "—"));
    }

    private void configurarSeleccion() {
        tablaCompras.getSelectionModel().selectedItemProperty().addListener(
                (observable, anterior, seleccionada) -> {
                    compraSeleccionada = seleccionada;
                    if (seleccionada == null) {
                        limpiarDetalle();
                    } else {
                        mostrarDetalle(seleccionada);
                    }
                }
        );
    }

    private void refrescarCompras() {
        compras.setAll(controller.obtenerCompras());
        lblTotalCompras.setText(compras.size() + (compras.size() == 1 ? " compra" : " compras"));
        int solicitudes = controller.obtenerSolicitudesReembolso().size();
        lblSolicitudesPendientes.setText(
                solicitudes + (solicitudes == 1 ? " solicitud pendiente" : " solicitudes pendientes")
        );
        aplicarFiltros();
    }

    private void aplicarFiltros() {
        String texto = txtBuscar.getText() == null
                ? "" : txtBuscar.getText().trim().toLowerCase(Locale.ROOT);
        String estado = cbEstado.getValue() == null ? "TODAS" : cbEstado.getValue();
        boolean soloSolicitudes = chkSoloSolicitudes.isSelected();

        List<Compra> resultado = new ArrayList<>();
        for (Compra compra : compras) {
            String nombre = controller.obtenerNombreCliente(compra).toLowerCase(Locale.ROOT);
            String identificacion = controller.obtenerIdentificacionCliente(compra).toLowerCase(Locale.ROOT);
            String codigo = compra.getCodigo() == null ? "" : compra.getCodigo().toLowerCase(Locale.ROOT);
            String estadoCompra = controller.obtenerEstado(compra);

            boolean coincideTexto = texto.isEmpty() || codigo.contains(texto)
                    || nombre.contains(texto) || identificacion.contains(texto);
            boolean coincideEstado = "TODAS".equals(estado)
                    || estado.equalsIgnoreCase(estadoCompra);
            boolean coincideSolicitud = !soloSolicitudes || controller.tieneSolicitudReembolso(compra);

            if (coincideTexto && coincideEstado && coincideSolicitud) {
                resultado.add(compra);
            }
        }

        comprasFiltradas.setAll(resultado);
        if (compraSeleccionada != null && !comprasFiltradas.contains(compraSeleccionada)) {
            tablaCompras.getSelectionModel().clearSelection();
        }
    }

    private void mostrarDetalle(Compra compra) {
        lblTituloDetalle.setText("DETALLE DE LA COMPRA");
        lblClienteDetalle.setText(controller.obtenerNombreCliente(compra));
        lblIdentificacionDetalle.setText(controller.obtenerIdentificacionCliente(compra));
        lblCodigoDetalle.setText(compra.getCodigo());
        lblFechaDetalle.setText(compra.getFechaCompra() == null
                ? "—" : compra.getFechaCompra().format(formatoFecha));
        lblEntradasDetalle.setText(String.valueOf(controller.obtenerCantidadEntradas(compra)));
        lblPuntosDetalle.setText(compra.getPuntosCompra() + " pts");
        lblEstadoDetalle.setText(controller.obtenerEstado(compra));
        lblSolicitudDetalle.setText(controller.tieneSolicitudReembolso(compra)
                ? "SOLICITUD PENDIENTE" : "SIN SOLICITUD PENDIENTE");
        lblDetalleEntradas.setText(controller.obtenerDetalleEntradas(compra));
        lblDetalleConfiteria.setText(controller.obtenerDetalleConfiteria(compra));
        lblTotalDetalle.setText(formatoMoneda.format(compra.getTotal()));

        if (controller.tieneSolicitudReembolso(compra)) {
            lblReembolsoEstimado.setText(formatoMoneda.format(
                    controller.calcularReembolsoEstimado(compra)));
        } else {
            lblReembolsoEstimado.setText("—");
        }

        boolean puedeGestionar = controller.tieneSolicitudReembolso(compra);
        btnAprobarSolicitud.setDisable(!puedeGestionar);
        btnRechazarSolicitud.setDisable(!puedeGestionar);
    }

    @FXML
    private void aprobarSolicitud() {
        if (compraSeleccionada == null) {
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Aprobar cancelación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Deseas aprobar la cancelación y procesar el reembolso de la compra "
                + compraSeleccionada.getCodigo() + "?");
        if (confirmacion.showAndWait().isEmpty()
                || confirmacion.getResult() != javafx.scene.control.ButtonType.OK) {
            return;
        }

        ResultadoOperacion resultado = controller.aprobarSolicitud(compraSeleccionada);
        mostrarMensaje(resultado.exitoso() ? Alert.AlertType.INFORMATION : Alert.AlertType.WARNING,
                resultado.exitoso() ? "Solicitud procesada" : "No fue posible aprobar",
                resultado.mensaje());
        refrescarCompras();
        if (comprasFiltradas.contains(compraSeleccionada)) {
            mostrarDetalle(compraSeleccionada);
        } else {
            tablaCompras.getSelectionModel().clearSelection();
        }
    }

    @FXML
    private void rechazarSolicitud() {
        if (compraSeleccionada == null) {
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Rechazar solicitud");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Deseas rechazar la solicitud de cancelación de la compra "
                + compraSeleccionada.getCodigo() + "?");
        if (confirmacion.showAndWait().isEmpty()
                || confirmacion.getResult() != javafx.scene.control.ButtonType.OK) {
            return;
        }

        ResultadoOperacion resultado = controller.rechazarSolicitud(compraSeleccionada);
        mostrarMensaje(resultado.exitoso() ? Alert.AlertType.INFORMATION : Alert.AlertType.WARNING,
                resultado.exitoso() ? "Solicitud rechazada" : "No fue posible rechazar",
                resultado.mensaje());
        refrescarCompras();
        if (compraSeleccionada != null && comprasFiltradas.contains(compraSeleccionada)) {
            mostrarDetalle(compraSeleccionada);
        } else {
            tablaCompras.getSelectionModel().clearSelection();
        }
    }

    @FXML
    private void limpiarFiltros() {
        txtBuscar.clear();
        cbEstado.setValue("TODAS");
        chkSoloSolicitudes.setSelected(false);
        tablaCompras.getSelectionModel().clearSelection();
    }

    private void limpiarDetalle() {
        lblTituloDetalle.setText("SELECCIONA UNA COMPRA PARA VER SUS DETALLES");
        lblClienteDetalle.setText("—");
        lblIdentificacionDetalle.setText("—");
        lblCodigoDetalle.setText("—");
        lblFechaDetalle.setText("—");
        lblEntradasDetalle.setText("—");
        lblPuntosDetalle.setText("—");
        lblEstadoDetalle.setText("—");
        lblSolicitudDetalle.setText("—");
        lblDetalleEntradas.setText("—");
        lblDetalleConfiteria.setText("—");
        lblTotalDetalle.setText("—");
        lblReembolsoEstimado.setText("—");
        btnAprobarSolicitud.setDisable(true);
        btnRechazarSolicitud.setDisable(true);
    }

    private void mostrarMensaje(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
