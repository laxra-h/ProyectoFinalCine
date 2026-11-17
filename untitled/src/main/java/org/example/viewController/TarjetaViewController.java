package org.example.viewController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import org.example.controller.TarjetaController;
import org.example.model.tarjeta.EstrategiaMetodoPago;
import org.example.model.tarjeta.MovimientoTarjeta;
import org.example.model.tarjeta.PagoPSE;
import org.example.model.tarjeta.PagoTarjetaCredito;
import org.example.model.tarjeta.PagoEfectivo;
import org.example.model.usuarios.Cliente;
import org.example.services.SesionActual;

import java.text.NumberFormat;
import java.util.Locale;

public class TarjetaViewController {

    @FXML private Label lblNumeroTarjeta;
    @FXML private Label lblSaldo;
    @FXML private Label lblEstadoTarjeta;
    @FXML private Label lblEstadoSolicitud;
    @FXML private Label lblMensajeRecarga;

    @FXML private TextField txtMontoRecarga;

    @FXML private ComboBox<String> cbMetodoPago;

    @FXML private TableView<MovimientoTarjeta> tablaMovimientos;
    @FXML private TableColumn<MovimientoTarjeta, String> colFecha;
    @FXML private TableColumn<MovimientoTarjeta, String> colTipo;
    @FXML private TableColumn<MovimientoTarjeta, String> colDescripcion;
    @FXML private TableColumn<MovimientoTarjeta, Double> colMonto;

    private final TarjetaController tarjetaController = new TarjetaController();

    private Cliente clienteActual;

    @FXML
    public void initialize() {
        clienteActual = SesionActual.getInstance().getClienteActual();

        configurarTabla();
        configurarMetodosPago();
        cargarInformacionTarjeta();
        cargarHistorial();
    }

    private void configurarTabla() {
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colMonto.setCellValueFactory(new PropertyValueFactory<>("monto"));
    }

    private void configurarMetodosPago() {
        cbMetodoPago.setItems(FXCollections.observableArrayList("PSE", "Tarjeta de crédito", "Efectivo"));
    }

    private void cargarInformacionTarjeta() {
        if (clienteActual == null) {
            return;
        }

        var tarjeta = tarjetaController.obtenerTarjeta(clienteActual);

        if (tarjeta == null) {
            return;
        }

        lblNumeroTarjeta.setText(tarjeta.getNumeroTarjeta());
        lblSaldo.setText(formatearDinero(tarjeta.getSaldo()));
        lblEstadoTarjeta.setText(tarjetaController.obtenerEstado(clienteActual));

        if (tarjeta.isRecargaSolicitada()) {
            lblEstadoSolicitud.setText("Solicitud pendiente por " + formatearDinero(tarjeta.getMontoSolicitado()));
        } else {
            lblEstadoSolicitud.setText("No tienes solicitudes pendientes.");
        }
    }

    private void cargarHistorial() {
        if (clienteActual == null) {
            return;
        }
        ObservableList<MovimientoTarjeta> movimientos = FXCollections.observableArrayList(tarjetaController.obtenerHistorial(clienteActual));
        tablaMovimientos.setItems(movimientos);
    }

    @FXML
    private void solicitarRecarga() {
        lblMensajeRecarga.getStyleClass().removeAll(
                "mensaje-error",
                "mensaje-exito"
        );

        if (clienteActual == null) {
            mostrarError("No se encontró una sesión activa.");
            return;
        }

        if (txtMontoRecarga.getText().isBlank()) {
            mostrarError("Ingresa el monto de la recarga.");
            return;
        }

        double monto;

        try {
            monto = Double.parseDouble(txtMontoRecarga.getText());
        } catch (NumberFormatException e) {
            mostrarError("Ingresa un monto válido.");
            return;
        }

        if (monto <= 0) {
            mostrarError("El monto debe ser mayor que cero.");
            return;
        }

        if (cbMetodoPago.getValue() == null) {
            mostrarError("Selecciona un método de pago.");
            return;
        }

        EstrategiaMetodoPago estrategia;

        switch (cbMetodoPago.getValue()) {
            case "PSE":
                estrategia = new PagoPSE();
                break;

            case "Tarjeta de crédito":
                estrategia = new PagoTarjetaCredito();
                break;

            case "Efectivo":
                estrategia = new PagoEfectivo();
                break;

            default:
                mostrarError("Método de pago no válido.");
                return;
        }

        boolean solicitud = tarjetaController.solicitarRecarga(
                clienteActual,
                monto,
                estrategia
        );

        if (solicitud) {
            lblMensajeRecarga.setText("Solicitud de recarga registrada correctamente.");
            lblMensajeRecarga.getStyleClass().add("mensaje-exito");

            txtMontoRecarga.clear();
            cbMetodoPago.getSelectionModel().clearSelection();

            cargarInformacionTarjeta();
        } else {
            mostrarError("No fue posible registrar la solicitud. Verifica si ya tienes una solicitud pendiente.");
        }
    }

    private void mostrarError(String mensaje) {
        lblMensajeRecarga.setText(mensaje);
        lblMensajeRecarga.getStyleClass().add("mensaje-error");
    }

    private String formatearDinero(double monto) {
        NumberFormat formato = NumberFormat.getCurrencyInstance(
                Locale.forLanguageTag("es-CO")
        );

        return formato.format(monto);
    }
}