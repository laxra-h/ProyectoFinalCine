package org.example.viewController;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.example.controller.RecargasAdminController;
import org.example.controller.RecargasAdminController.ResultadoOperacion;
import org.example.model.usuarios.Cliente;

import java.text.NumberFormat;
import java.util.Locale;

public class RecargasAdminViewController {

    @FXML private TextField txtBuscar;
    @FXML private Label lblTotalSolicitudes;
    @FXML private Label lblMontoTotal;

    @FXML private TableView<Cliente> tablaSolicitudes;
    @FXML private TableColumn<Cliente, String> colCliente;
    @FXML private TableColumn<Cliente, String> colIdentificacion;
    @FXML private TableColumn<Cliente, String> colTarjeta;
    @FXML private TableColumn<Cliente, String> colMonto;
    @FXML private TableColumn<Cliente, String> colMetodo;

    @FXML private Label lblNombreDetalle;
    @FXML private Label lblIdentificacionDetalle;
    @FXML private Label lblTarjetaDetalle;
    @FXML private Label lblSaldoActual;
    @FXML private Label lblMontoSolicitado;
    @FXML private Label lblMetodoPago;
    @FXML private Label lblEstadoTarjeta;
    @FXML private Button btnAprobar;
    @FXML private Button btnRechazar;

    private final RecargasAdminController controller = new RecargasAdminController();
    private final ObservableList<Cliente> solicitudes = FXCollections.observableArrayList();
    private FilteredList<Cliente> solicitudesFiltradas;
    private Cliente solicitudSeleccionada;

    private final NumberFormat formatoMoneda =
            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO"));

    @FXML
    public void initialize() {
        formatoMoneda.setMaximumFractionDigits(0);
        formatoMoneda.setMinimumFractionDigits(0);

        configurarTabla();
        solicitudesFiltradas = new FilteredList<>(solicitudes, cliente -> true);
        tablaSolicitudes.setItems(solicitudesFiltradas);

        tablaSolicitudes.getSelectionModel().selectedItemProperty().addListener(
                (observable, anterior, seleccionado) -> {
                    solicitudSeleccionada = seleccionado;
                    if (seleccionado == null) {
                        limpiarDetalle();
                    } else {
                        mostrarDetalle(seleccionado);
                    }
                }
        );
        txtBuscar.textProperty().addListener(
                (observable, anterior, texto) -> aplicarFiltro(texto)
        );

        btnAprobar.setDisable(true);
        btnRechazar.setDisable(true);
        limpiarDetalle();
        refrescarSolicitudes();
    }

    private void configurarTabla() {
        colCliente.setCellValueFactory(data ->
                new SimpleStringProperty(controller.obtenerNombreCliente(data.getValue())));
        colIdentificacion.setCellValueFactory(data ->
                new SimpleStringProperty(controller.obtenerIdentificacion(data.getValue())));
        colTarjeta.setCellValueFactory(data ->
                new SimpleStringProperty(controller.obtenerNumeroTarjeta(data.getValue())));
        colMonto.setCellValueFactory(data ->
                new SimpleStringProperty(formatoMoneda.format(
                        controller.obtenerMontoSolicitado(data.getValue()))));
        colMetodo.setCellValueFactory(data ->
                new SimpleStringProperty(controller.obtenerMetodoPago(data.getValue())));
    }

    private void refrescarSolicitudes() {
        solicitudes.setAll(controller.obtenerSolicitudesPendientes());
        int cantidad = solicitudes.size();
        lblTotalSolicitudes.setText(cantidad + (cantidad == 1
                ? " solicitud pendiente" : " solicitudes pendientes"));
        lblMontoTotal.setText(formatoMoneda.format(controller.calcularTotalSolicitado()));
        aplicarFiltro(txtBuscar.getText());

        if (solicitudSeleccionada != null && !solicitudes.contains(solicitudSeleccionada)) {
            tablaSolicitudes.getSelectionModel().clearSelection();
            solicitudSeleccionada = null;
            limpiarDetalle();
        }
    }

    private void aplicarFiltro(String texto) {
        String busqueda = texto == null ? "" : texto.trim().toLowerCase(Locale.ROOT);
        solicitudesFiltradas.setPredicate(cliente -> {
            if (busqueda.isEmpty()) {
                return true;
            }
            return controller.obtenerNombreCliente(cliente).toLowerCase(Locale.ROOT).contains(busqueda)
                    || controller.obtenerIdentificacion(cliente).toLowerCase(Locale.ROOT).contains(busqueda)
                    || controller.obtenerNumeroTarjeta(cliente).toLowerCase(Locale.ROOT).contains(busqueda);
        });

        if (solicitudSeleccionada != null && !solicitudesFiltradas.contains(solicitudSeleccionada)) {
            tablaSolicitudes.getSelectionModel().clearSelection();
        }
    }

    private void mostrarDetalle(Cliente cliente) {
        lblNombreDetalle.setText(controller.obtenerNombreCliente(cliente));
        lblIdentificacionDetalle.setText(controller.obtenerIdentificacion(cliente));
        lblTarjetaDetalle.setText(controller.obtenerNumeroTarjeta(cliente));
        lblSaldoActual.setText(formatoMoneda.format(controller.obtenerSaldoActual(cliente)));
        lblMontoSolicitado.setText(formatoMoneda.format(controller.obtenerMontoSolicitado(cliente)));
        lblMetodoPago.setText(controller.obtenerMetodoPago(cliente));
        lblEstadoTarjeta.setText(controller.tarjetaActiva(cliente) ? "ACTIVA" : "INACTIVA");

        btnAprobar.setDisable(!controller.tarjetaActiva(cliente));
        btnRechazar.setDisable(false);
    }

    @FXML
    private void aprobarSolicitud() {
        if (solicitudSeleccionada == null) {
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Aprobar recarga");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Deseas aprobar la recarga por "
                + formatoMoneda.format(controller.obtenerMontoSolicitado(solicitudSeleccionada))
                + " para " + controller.obtenerNombreCliente(solicitudSeleccionada) + "?");
        if (confirmacion.showAndWait().filter(ButtonType.OK::equals).isEmpty()) {
            return;
        }

        ResultadoOperacion resultado = controller.aprobarSolicitud(solicitudSeleccionada);
        mostrarResultado(resultado);
        refrescarSolicitudes();
    }

    @FXML
    private void rechazarSolicitud() {
        if (solicitudSeleccionada == null) {
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Rechazar recarga");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Deseas rechazar la solicitud de recarga de "
                + controller.obtenerNombreCliente(solicitudSeleccionada) + "?");
        if (confirmacion.showAndWait().filter(ButtonType.OK::equals).isEmpty()) {
            return;
        }

        ResultadoOperacion resultado = controller.rechazarSolicitud(solicitudSeleccionada);
        mostrarResultado(resultado);
        refrescarSolicitudes();
    }

    @FXML
    private void limpiarBusqueda() {
        txtBuscar.clear();
    }

    private void limpiarDetalle() {
        lblNombreDetalle.setText("—");
        lblIdentificacionDetalle.setText("—");
        lblTarjetaDetalle.setText("—");
        lblSaldoActual.setText("—");
        lblMontoSolicitado.setText("—");
        lblMetodoPago.setText("—");
        lblEstadoTarjeta.setText("—");
        btnAprobar.setDisable(true);
        btnRechazar.setDisable(true);
    }

    private void mostrarResultado(ResultadoOperacion resultado) {
        Alert alerta = new Alert(resultado.exitoso()
                ? Alert.AlertType.INFORMATION : Alert.AlertType.WARNING);
        alerta.setTitle(resultado.exitoso() ? "Operación completada" : "No fue posible completar la operación");
        alerta.setHeaderText(null);
        alerta.setContentText(resultado.mensaje());
        alerta.showAndWait();
    }
}
