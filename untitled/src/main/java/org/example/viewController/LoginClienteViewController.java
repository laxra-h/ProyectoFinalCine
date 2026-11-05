package org.example.viewController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginClienteViewController {

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtContrasena;

    @FXML
    private Label lblMensaje;

    @FXML
    private void initialize() {

        txtUsuario.setOnAction(event -> txtContrasena.requestFocus());

        txtContrasena.setOnAction(event -> ingresar());

    }

    @FXML
    private void ingresar() {

        String usuario = txtUsuario.getText().trim();
        String contrasena = txtContrasena.getText();

        lblMensaje.setText("");

        if (usuario.isEmpty() && contrasena.isEmpty()) {
            lblMensaje.setText("Ingresa tu usuario y contraseña.");
            return;
        }

        if (usuario.isEmpty()) {
            lblMensaje.setText("Ingresa tu usuario.");
            txtUsuario.requestFocus();
            return;
        }

        if (contrasena.isEmpty()) {
            lblMensaje.setText("Ingresa tu contraseña.");
            txtContrasena.requestFocus();
            return;
        }

    }

    @FXML
    private void volver() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org.example/opcionesCliente.fxml"));

            Parent root = loader.load();

            Stage stage = (Stage) txtUsuario.getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}