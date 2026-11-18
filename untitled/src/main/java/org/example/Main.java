package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        DatosIniciales.inicializar();
        DatosIniciales.inicializarConfiteria();
        DatosIniciales.inicializarClientes();
        DatosIniciales.inicializarCompras();
        DatosIniciales.inicializarMovimientosTarjetas();
        DatosIniciales.inicializarPuntosClientes();

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/org.example/bienvenida.fxml")
        );

        Scene scene = new Scene(loader.load());

        stage.setTitle("CinemaUQ");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}