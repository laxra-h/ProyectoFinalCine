package org.example.viewController;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.example.controller.ComprasController;
import org.example.model.compra.Compra;
import org.example.model.usuarios.Cliente;
import org.example.services.ClienteService;
import org.example.services.SesionActual;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class ComprasViewController {

    @FXML
    private TableView<Compra> tablaCompras;

    @FXML
    private TableColumn<Compra, String> colCodigo;

    @FXML
    private TableColumn<Compra, String> colFecha;

    @FXML
    private TableColumn<Compra, String> colEntradas;

    @FXML
    private TableColumn<Compra, String> colConsumibles;

    @FXML
    private TableColumn<Compra, String> colTotal;

    @FXML
    private TableColumn<Compra, String> colEstado;

    @FXML
    private Label lblCantidadCompras;

    @FXML
    private Label lblSeleccion;

    @FXML
    private Label lblCodigoDetalle;

    @FXML
    private Label lblFechaDetalle;

    @FXML
    private Label lblEntradasDetalle;

    @FXML
    private Label lblPuntosDetalle;

    @FXML
    private Label lblEstadoDetalle;

    @FXML
    private Label lblDetalleEntradas;

    @FXML
    private Label lblDetalleConsumibles;

    @FXML
    private Label lblTotalDetalle;

    @FXML
    private Button btnSolicitarReembolso;

    private final ComprasController controller = new ComprasController();
    private final ClienteService clienteService = new ClienteService();

    private Cliente clienteActual;
    private Compra compraSeleccionada;

    private final NumberFormat formatoMoneda =
            NumberFormat.getCurrencyInstance(new Locale("es", "CO"));

    private final DateTimeFormatter formatoFecha =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        clienteActual = SesionActual.getInstance().getClienteActual();

        configurarColumnas();
        configurarSeleccion();
        limpiarDetalles();
        cargarCompras();
    }

    private void configurarColumnas() {

        colCodigo.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCodigo()));

        colFecha.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getFechaCompra().format(formatoFecha)));

        colEntradas.setCellValueFactory(data ->
                new SimpleStringProperty(
                        String.valueOf(controller.obtenerCantidadEntradas(data.getValue()))));

        colConsumibles.setCellValueFactory(data ->
                new SimpleStringProperty(controller.obtenerConsumibles(data.getValue())));

        colTotal.setCellValueFactory(data ->
                new SimpleStringProperty(
                        formatoMoneda.format(data.getValue().getTotal())));

        colEstado.setCellValueFactory(data ->
                new SimpleStringProperty(controller.obtenerEstado(data.getValue())));
    }

    private void configurarSeleccion() {

        tablaCompras.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, anterior, seleccionada) -> {

                    compraSeleccionada = seleccionada;

                    if (seleccionada != null) {
                        mostrarDetalles(seleccionada);
                    } else {
                        limpiarDetalles();
                    }
                });
    }

    private void cargarCompras() {

        if (clienteActual == null) {
            lblCantidadCompras.setText("0 compras");
            return;
        }

        List<Compra> compras = controller.obtenerCompras(clienteActual);

        tablaCompras.getItems().setAll(compras);

        lblCantidadCompras.setText(
                compras.size() + (compras.size() == 1 ? " compra" : " compras"));
    }

    private void mostrarDetalles(Compra compra) {

        lblSeleccion.setText("DETALLES DE LA COMPRA SELECCIONADA");

        lblCodigoDetalle.setText(compra.getCodigo());

        lblFechaDetalle.setText(
                compra.getFechaCompra().format(formatoFecha));

        lblEntradasDetalle.setText(
                String.valueOf(controller.obtenerCantidadEntradas(compra)));

        lblPuntosDetalle.setText(
                String.valueOf(compra.getPuntosCompra()));

        lblEstadoDetalle.setText(
                controller.obtenerEstado(compra));

        lblDetalleEntradas.setText(
                controller.obtenerDetalleEntradas(compra));

        lblDetalleConsumibles.setText(
                controller.obtenerDetalleConsumibles(compra));

        lblTotalDetalle.setText(
                formatoMoneda.format(compra.getTotal()));

        actualizarBotonReembolso(compra);
    }

    private void actualizarBotonReembolso(Compra compra) {

        boolean solicitudExistente =
                org.example.model.Cine.getInstance()
                        .getListSolicitudesReembolso()
                        .contains(compra);

        btnSolicitarReembolso.setDisable(
                !controller.obtenerEstado(compra).equalsIgnoreCase("PAGADA")
                        || solicitudExistente);
    }

    @FXML
    private void solicitarReembolso() {

        if (clienteActual == null || compraSeleccionada == null) {
            return;
        }

        boolean registrado = clienteService.solicitarReembolso(
                clienteActual, compraSeleccionada);

        if (registrado) {

            mostrarMensaje(
                    Alert.AlertType.INFORMATION,
                    "Solicitud registrada",
                    "Tu solicitud de reembolso ha sido enviada al administrador.");

            actualizarBotonReembolso(compraSeleccionada);

        } else {

            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Solicitud no registrada",
                    "No fue posible registrar la solicitud. Verifica el estado de la compra.");
        }
    }

    private void limpiarDetalles() {

        lblSeleccion.setText("SELECCIONA UNA COMPRA PARA VER SUS DETALLES");

        lblCodigoDetalle.setText("");
        lblFechaDetalle.setText("");
        lblEntradasDetalle.setText("");
        lblPuntosDetalle.setText("");
        lblEstadoDetalle.setText("");
        lblDetalleEntradas.setText("");
        lblDetalleConsumibles.setText("");
        lblTotalDetalle.setText("");

        btnSolicitarReembolso.setDisable(true);
    }

    private void mostrarMensaje(Alert.AlertType tipo, String titulo, String mensaje) {

        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}