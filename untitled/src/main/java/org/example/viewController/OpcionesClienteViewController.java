package org.example.viewController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class OpcionesClienteViewController {

    @FXML
    private Button btnIniciarSesion;

    @FXML
    private Button btnRegistrarse;

    @FXML
    private Button btnVolver;

    @FXML
    private void abrirLogin() {

        cambiarVista("/org.example/loginCliente.fxml");

    }

    @FXML
    private void abrirRegistro() {

        cambiarVista("/org.example/registroCliente.fxml");

    }

    @FXML
    private void volver() {

        cambiarVista("/org.example/bienvenida.fxml");

    }

    private void cambiarVista(String ruta) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta));

            Parent root = loader.load();

            Stage stage = (Stage) btnVolver.getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}