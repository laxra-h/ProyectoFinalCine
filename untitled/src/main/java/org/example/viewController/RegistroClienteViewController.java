package org.example.viewController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class RegistroClienteViewController {

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtTelefono;

    @FXML
    private TextField txtCorreo;

    @FXML
    private PasswordField txtContrasena;

    @FXML
    private DatePicker dpFechaNacimiento;

    @FXML
    private Label lblMensaje;

    @FXML
    private Button btnVolver;

    @FXML
    private Button btnCrear;

    @FXML
    private void initialize() {

        txtId.setTextFormatter(new TextFormatter<>(change -> change.getControlNewText().matches("\\d*") ? change : null));

        txtTelefono.setTextFormatter(new TextFormatter<>(change -> change.getControlNewText().matches("\\d{0,10}") ? change : null));
    }

    @FXML
    private void crearCuenta() {

        lblMensaje.setText("");

        if (txtId.getText().trim().isEmpty()
                || txtNombre.getText().trim().isEmpty()
                || txtTelefono.getText().trim().isEmpty()
                || txtCorreo.getText().trim().isEmpty()
                || txtContrasena.getText().isEmpty()
                || dpFechaNacimiento.getValue() == null) {

            mostrarError("Debes completar todos los campos.");
            return;
        }

        if (txtTelefono.getText().length() != 10) {
            mostrarError("El teléfono debe tener exactamente 10 dígitos.");
            txtTelefono.requestFocus();
            return;
        }

        if (!txtCorreo.getText().contains("@") || !txtCorreo.getText().contains(".")) {
            mostrarError("Ingresa un correo electrónico válido.");
            txtCorreo.requestFocus();
            return;
        }

        mostrarExito("✓ Datos validados correctamente.");
    }

    @FXML
    private void volver() {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org.example/opcionesCliente.fxml")
            );

            Parent root = loader.load();

            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void mostrarError(String mensaje) {
        lblMensaje.setStyle("-fx-text-fill: #C0392B;");
        lblMensaje.setText(mensaje);
    }

    private void mostrarExito(String mensaje) {
        lblMensaje.setStyle("-fx-text-fill: #1E8449;");
        lblMensaje.setText(mensaje);
    }
}