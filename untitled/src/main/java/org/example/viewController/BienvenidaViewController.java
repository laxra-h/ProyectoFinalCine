package org.example.viewController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class BienvenidaViewController {

    @FXML
    private Button btnCliente;

    @FXML
    private Button btnAdministrador;

    @FXML
    private void abrirOpcionesCliente() {
        cambiarVista("/org.example/opcionesCliente.fxml");
    }

    @FXML
    private void abrirLoginAdministrador() {
        cambiarVista("/org.example/loginAdministrador.fxml");
    }

    private void cambiarVista(String ruta) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta));

            Parent root = loader.load();

            Stage stage = (Stage) btnCliente.getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}