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

    private ToggleGroup grupoMenu;

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

        lblBienvenida.setText("BIENVENIDO/A");

    }

    @FXML
    private void mostrarCartelera() {
        mostrarSeccion("CARTELERA",
                "Consulta nuestras películas, sinopsis, clasificaciones y funciones disponibles.");
    }

    @FXML
    private void mostrarConfiteria() {
        mostrarSeccion("CONFITERÍA",
                "Consulta nuestros productos, precios y combos disponibles.");
    }

    @FXML
    private void mostrarCompra() {
        mostrarSeccion("COMPRAR",
                "Aquí podrás seleccionar películas, funciones, asientos y productos para realizar tu compra.");
    }

    @FXML
    private void mostrarTarjeta() {
        mostrarSeccion("MI TARJETA",
                "Consulta el saldo, estado y movimientos de tu tarjeta virtual.");
    }

    @FXML
    private void mostrarPuntos() {
        mostrarSeccion("MIS PUNTOS",
                "Consulta tus puntos acumulados, vencimientos y opciones de redención.");
    }

    @FXML
    private void mostrarCompras() {
        mostrarSeccion("MIS COMPRAS",
                "Consulta tu historial de compras y los detalles de tus transacciones.");
    }

    @FXML
    private void mostrarPerfil() {
        mostrarSeccion("MI PERFIL",
                "Consulta y actualiza tu información personal.");
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
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org.example/bienvenida.fxml")
            );

            Parent root = loader.load();

            Stage stage = (Stage) btnCerrarSesion.getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}