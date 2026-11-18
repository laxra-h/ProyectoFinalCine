package org.example.viewController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.model.Cine;
import org.example.model.usuarios.Administrador;
import org.example.services.SesionActual;

import java.io.IOException;

public class LoginAdministradorViewController {

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

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            lblMensaje.setText("Ingresa tu usuario y contraseña.");
            return;
        }

        Cine cine = Cine.getInstance();
        Administrador administrador = cine.obtenerAdministrador(usuario);

        if (administrador == null ||
                !administrador.getContrasenia().equals(contrasena)) {
            lblMensaje.setText("Usuario o contraseña incorrectos.");
            return;
        }

        SesionActual.getInstance().iniciarSesion(administrador);

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org.example/panelAdministrador.fxml")
            );

            Parent root = loader.load();
            Stage stage = (Stage) txtUsuario.getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            lblMensaje.setText("No fue posible abrir el panel administrativo.");
        }
    }

    @FXML
    private void volver() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org.example/bienvenida.fxml"));

            Parent root = loader.load();

            Stage stage = (Stage) txtUsuario.getScene().getWindow();

            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}