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
import javafx.scene.control.Button;

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

    private ToggleGroup grupoMenu;

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

        lblBienvenida.setText("BIENVENIDO");

    }

    @FXML
    private void mostrarPeliculas() {
        mostrarSeccion("PELÍCULAS",
                "Administra la información de las películas de CinemaUQ.");
    }

    @FXML
    private void mostrarSalas() {
        mostrarSeccion("SALAS Y ASIENTOS",
                "Gestiona las salas, su distribución y los tipos de asientos.");
    }

    @FXML
    private void mostrarFunciones() {
        mostrarSeccion("FUNCIONES",
                "Programa, consulta y administra las funciones del cine.");
    }

    @FXML
    private void mostrarProductos() {
        mostrarSeccion("PRODUCTOS Y COMBOS",
                "Administra los productos y combos disponibles en confitería.");
    }

    @FXML
    private void mostrarPromociones() {
        mostrarSeccion("PROMOCIONES",
                "Crea y administra las promociones y descuentos de CinemaUQ.");
    }

    @FXML
    private void mostrarClientes() {
        mostrarSeccion("CLIENTES",
                "Consulta la información de los clientes registrados.");
    }

    @FXML
    private void mostrarCompras() {
        mostrarSeccion("COMPRAS",
                "Consulta las compras y gestiona las solicitudes de cancelación.");
    }

    @FXML
    private void mostrarRecargas() {
        mostrarSeccion("RECARGAS",
                "Gestiona las solicitudes de recarga de las tarjetas virtuales.");
    }

    @FXML
    private void mostrarReportes() {
        mostrarSeccion("REPORTES",
                "Consulta y exporta los reportes del funcionamiento del cine.");
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

        contenido.getChildren().addAll(lblTitulo, lblDescripcion);

        contenidoDinamico.getChildren().setAll(contenido);
    }

    @FXML
    private void cerrarSesion() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org.example/bienvenida.fxml"));

            Parent root = loader.load();

            Stage stage = (Stage) btnCerrarSesion.getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}