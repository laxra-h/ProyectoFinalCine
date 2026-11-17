package org.example.viewController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import org.example.model.usuarios.Cliente;
import javafx.scene.control.Button;
import org.example.services.SesionActual;

import java.io.IOException;

public class PanelClienteViewController {

    @FXML
    private ToggleButton btnCartelera;

    @FXML
    private ToggleButton btnConfiteria;

    @FXML
    private ToggleButton btnComprar;

    @FXML
    private ToggleButton btnMiTarjeta;

    @FXML
    private ToggleButton btnMisPuntos;

    @FXML
    private ToggleButton btnMisCompras;

    @FXML
    private ToggleButton btnMiPerfil;

    @FXML
    private StackPane contenidoDinamico;

    @FXML
    private VBox vistaBienvenida;

    @FXML
    private Label lblBienvenida;

    @FXML
    private Button btnCerrarSesion;

    private static final String RUTA_VISTAS = "/org.example/";

    private ToggleGroup grupoMenu;

    private Cliente clienteActual;

    @FXML
    private void initialize() {

        grupoMenu = new ToggleGroup();

        btnCartelera.setToggleGroup(grupoMenu);
        btnConfiteria.setToggleGroup(grupoMenu);
        btnComprar.setToggleGroup(grupoMenu);
        btnMiTarjeta.setToggleGroup(grupoMenu);
        btnMisPuntos.setToggleGroup(grupoMenu);
        btnMisCompras.setToggleGroup(grupoMenu);
        btnMiPerfil.setToggleGroup(grupoMenu);

        clienteActual = SesionActual.getInstance().getClienteActual();

        if (clienteActual != null) {
            lblBienvenida.setText("BIENVENIDO/A, " + clienteActual.getNombre().toUpperCase());
        } else {
            lblBienvenida.setText("BIENVENIDO/A");
        }
    }

    @FXML
    private void mostrarCartelera() {
        cargarVista("cartelera.fxml");
    }

    @FXML
    private void mostrarConfiteria() {
        cargarVista("confiteria.fxml");
    }

    @FXML
    private void mostrarCompra() {
        cargarVista("compra.fxml");
         }

    @FXML
    private void mostrarTarjeta() {
        cargarVista("tarjeta.fxml");
    }

    @FXML
    private void mostrarPuntos() {
        cargarVista("puntos.fxml");
    }

    @FXML
    private void mostrarCompras() {
        cargarVista("compras.fxml");
    }

    @FXML
    private void mostrarPerfil() {

        FXMLLoader loader = cargarVista("perfil.fxml");

        if (loader != null) {
            PerfilViewController perfilController = loader.getController();
            perfilController.setCliente(clienteActual);
        }
    }

    private FXMLLoader cargarVista(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(RUTA_VISTAS + fxml));
            Parent vista = loader.load();
            contenidoDinamico.getChildren().setAll(vista);
            return loader;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    private void cerrarSesion() {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org.example/bienvenida.fxml")
            );

            Parent root = loader.load();

            SesionActual.getInstance().cerrarSesion();

            Stage stage = (Stage) btnCerrarSesion.getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}