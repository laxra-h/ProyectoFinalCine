package org.example.viewController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.stage.Stage;

import org.example.model.usuarios.Administrador;
import org.example.services.SesionActual;

import java.io.IOException;

public class PanelAdministradorViewController {

    @FXML
    private ToggleButton btnPeliculas;

    @FXML
    private ToggleButton btnSalas;

    @FXML
    private ToggleButton btnFunciones;

    @FXML
    private ToggleButton btnProductos;

    @FXML
    private ToggleButton btnPromociones;

    @FXML
    private ToggleButton btnClientes;

    @FXML
    private ToggleButton btnCompras;

    @FXML
    private ToggleButton btnRecargas;

    @FXML
    private ToggleButton btnReportes;

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

    private Administrador administradorActual;

    @FXML
    private void initialize() {

        grupoMenu = new ToggleGroup();

        btnPeliculas.setToggleGroup(grupoMenu);
        btnSalas.setToggleGroup(grupoMenu);
        btnFunciones.setToggleGroup(grupoMenu);
        btnProductos.setToggleGroup(grupoMenu);
        btnPromociones.setToggleGroup(grupoMenu);
        btnClientes.setToggleGroup(grupoMenu);
        btnCompras.setToggleGroup(grupoMenu);
        btnRecargas.setToggleGroup(grupoMenu);
        btnReportes.setToggleGroup(grupoMenu);

        administradorActual = SesionActual.getInstance().getAdministradorActual();

        if (administradorActual != null) {
            lblBienvenida.setText(
                    "BIENVENIDO/A, " + administradorActual.getNombre().toUpperCase()
            );
        } else {
            lblBienvenida.setText("BIENVENIDO/A, ADMINISTRADOR");
        }
    }

    @FXML
    private void mostrarPeliculas() {
        cargarVista("adminPeliculas.fxml");
    }

    @FXML
    private void mostrarSalas() {
        cargarVista("salas.fxml");
    }

    @FXML
    private void mostrarFunciones() {
        cargarVista("adminFunciones.fxml");
    }

    @FXML
    private void mostrarProductos() {
        cargarVista("adminConfiteria.fxml");
    }

    @FXML
    private void mostrarPromociones() {
        cargarVista("adminPromociones.fxml");
    }

    @FXML
    private void mostrarClientes() {
        cargarVista("adminCliente.fxml");
    }

    @FXML
    private void mostrarCompras() {
        cargarVista("adminCompras.fxml");
    }

    @FXML
    private void mostrarRecargas() {
        cargarVista("adminRecargas.fxml");
    }

    @FXML
    private void mostrarReportes() {
        cargarVista("adminReportes.fxml");
    }

    private FXMLLoader cargarVista(String fxml) {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(RUTA_VISTAS + fxml)
            );

            Parent vista = loader.load();

            contenidoDinamico.getChildren().setAll(vista);

            return loader;

        } catch (IOException e) {

            e.printStackTrace();

            mostrarSeccion(
                    "ERROR",
                    "No fue posible cargar la sección solicitada."
            );

            return null;
        }
    }

    private void mostrarSeccion(String titulo, String descripcion) {

        VBox contenido = new VBox(20);

        contenido.setAlignment(Pos.CENTER);

        Label lblTitulo = new Label(titulo);
        lblTitulo.getStyleClass().add("titulo-grande");

        Label lblDescripcion = new Label(descripcion);
        lblDescripcion.getStyleClass().add("portada-frase");
        lblDescripcion.setWrapText(true);
        lblDescripcion.setMaxWidth(600);

        contenido.getChildren().addAll(
                lblTitulo,
                lblDescripcion
        );

        contenidoDinamico.getChildren().setAll(contenido);
    }

    @FXML
    private void cerrarSesion() {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(RUTA_VISTAS + "bienvenida.fxml")
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