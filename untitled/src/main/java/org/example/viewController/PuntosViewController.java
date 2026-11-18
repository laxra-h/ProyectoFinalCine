package org.example.viewController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import org.example.controller.PuntosController;
import org.example.model.compra.Compra;
import org.example.model.usuarios.Cliente;
import org.example.services.SesionActual;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class PuntosViewController {

    @FXML private Label lblPuntosDisponibles;

    @FXML private TableView<Compra> tablaPuntos;
    @FXML private TableColumn<Compra, String> colCompra;
    @FXML private TableColumn<Compra, String> colFecha;
    @FXML private TableColumn<Compra, Integer> colPuntos;
    @FXML private TableColumn<Compra, String> colVencimiento;
    @FXML private TableColumn<Compra, String> colEstado;

    private final PuntosController puntosController = new PuntosController();

    private Cliente clienteActual;

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.forLanguageTag("es-CO"));

    @FXML
    public void initialize() {

        clienteActual = SesionActual.getInstance().getClienteActual();

        configurarTabla();
        cargarPuntos();
    }

    private void configurarTabla() {

        colCompra.setCellValueFactory(new PropertyValueFactory<>("codigo"));

        colFecha.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getFechaCompra().format(FORMATO_FECHA)
                )
        );

        colPuntos.setCellValueFactory(new PropertyValueFactory<>("puntosCompra"));

        colVencimiento.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        puntosController.obtenerFechaVencimiento(data.getValue())
                                .format(FORMATO_FECHA)
                )
        );

        colEstado.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        puntosController.puntosVigentes(data.getValue())
                                ? "VIGENTES" : "VENCIDOS"
                )
        );
    }

    private void cargarPuntos() {

        if (clienteActual == null) {
            lblPuntosDisponibles.setText("0 puntos");
            return;
        }

        lblPuntosDisponibles.setText(
                puntosController.obtenerPuntosDisponibles(clienteActual) + " puntos"
        );

        ObservableList<Compra> compras = FXCollections.observableArrayList(
                puntosController.obtenerComprasConPuntos(clienteActual)
        );

        tablaPuntos.setItems(compras);
    }
}