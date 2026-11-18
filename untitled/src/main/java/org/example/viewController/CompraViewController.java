package org.example.viewController;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.example.controller.CompraController;
import org.example.model.consumibleComposite.Consumible;
import org.example.model.funcionDecorator.Funcion;
import org.example.model.pelicula.Pelicula;
import org.example.services.SesionActual;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CompraViewController {

    @FXML private ComboBox<Pelicula> cbPeliculas;
    @FXML private ComboBox<Funcion> cbFunciones;
    @FXML private Spinner<Integer> spCantidadEntradas;

    @FXML private Label lblAsientosSeleccionados;

    @FXML private ListView<Consumible> listaConfiteria;

    @FXML private Label lblSubtotalEntradas;
    @FXML private Label lblSubtotalConfiteria;
    @FXML private Label lblSubtotal;
    @FXML private Label lblPuntosDisponibles;
    @FXML private Label lblDescuentoPuntos;
    @FXML private Label lblPuntosGanados;
    @FXML private Label lblTotalPagar;

    @FXML private CheckBox chkUsarPuntos;

    @FXML private Button btnSeleccionarAsientos;
    @FXML private Button btnConfirmarCompra;

    private final CompraController controller = new CompraController();

    private final Map<Consumible, Integer> consumiblesSeleccionados = new HashMap<>();

    private static final Locale LOCALE = Locale.forLanguageTag("es-CO");

    private Funcion funcionSeleccionada;
    private List<String> asientosSeleccionados = new ArrayList<>();

    private double subtotalEntradas;
    private double subtotalConfiteria;
    private double subtotal;
    private double descuentoPuntos;
    private double totalPagar;

    private int puntosUsados;
    private int puntosGanados;

    @FXML
    public void initialize() {

        configurarPeliculas();
        configurarFunciones();
        configurarCantidadEntradas();
        configurarConfiteria();
        configurarEventos();

        lblAsientosSeleccionados.setText("No has seleccionado asientos");

        btnSeleccionarAsientos.setDisable(true);
        btnConfirmarCompra.setDisable(true);

        lblPuntosDisponibles.setText(
                "Puntos disponibles: " + controller.obtenerPuntosCliente()
        );

        actualizarTotales();
    }

    private void configurarPeliculas() {

        cbPeliculas.getItems().setAll(controller.obtenerPeliculas());

        cbPeliculas.setConverter(new StringConverter<>() {

            @Override
            public String toString(Pelicula pelicula) {
                return pelicula == null ? "" : pelicula.getNombre();
            }

            @Override
            public Pelicula fromString(String texto) {
                return null;
            }
        });
    }

    private void configurarFunciones() {

        cbFunciones.setConverter(new StringConverter<>() {

            @Override
            public String toString(Funcion funcion) {

                if (funcion == null) {
                    return "";
                }

                return funcion.getFecha().format(
                        java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", LOCALE)
                ) + " | " + funcion.getSala().getNumeroSala()
                        + " | " + controller.obtenerFormato(funcion)
                        + " | COP " + formatearDinero(funcion.calcularCosto());
            }

            @Override
            public Funcion fromString(String texto) {
                return null;
            }
        });
    }

    private void configurarCantidadEntradas() {

        spCantidadEntradas.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1)
        );
    }

    private void configurarConfiteria() {

        listaConfiteria.setItems(
                FXCollections.observableArrayList(controller.obtenerConfiteria())
        );

        listaConfiteria.setCellFactory(list -> new ConsumibleCell());
    }

    private void configurarEventos() {

        cbPeliculas.setOnAction(event -> {

            Pelicula pelicula = cbPeliculas.getValue();

            cbFunciones.getItems().clear();
            funcionSeleccionada = null;

            if (pelicula != null) {

                cbFunciones.getItems().setAll(
                        controller.obtenerFuncionesDisponibles(pelicula)
                );
            }

            invalidarAsientos();
            actualizarTotales();
        });

        cbFunciones.setOnAction(event -> {

            funcionSeleccionada = cbFunciones.getValue();

            invalidarAsientos();
            actualizarTotales();
        });

        spCantidadEntradas.valueProperty().addListener(
                (observable, anterior, nuevo) -> {

                    invalidarAsientos();
                    actualizarTotales();
                }
        );

        chkUsarPuntos.setOnAction(event -> actualizarTotales());
        btnSeleccionarAsientos.setOnAction(event -> abrirSeleccionAsientos());
        btnConfirmarCompra.setOnAction(event -> confirmarCompra());
    }
    private void confirmarCompra() {

        String resultado = controller.confirmarCompra(
                funcionSeleccionada,
                asientosSeleccionados,
                consumiblesSeleccionados,
                chkUsarPuntos.isSelected()
        );

        javafx.scene.control.Alert alerta = new javafx.scene.control.Alert(
                resultado.equals("!Compra exitosa!")
                        ? javafx.scene.control.Alert.AlertType.INFORMATION
                        : javafx.scene.control.Alert.AlertType.ERROR
        );

        alerta.setTitle("CinemaUQ");
        alerta.setHeaderText(null);
        alerta.setContentText(resultado);
        alerta.showAndWait();

        if (resultado.equals("!Compra exitosa!")) {
            btnConfirmarCompra.setDisable(true);
            lblPuntosDisponibles.setText(
                    "Puntos disponibles: " + controller.obtenerPuntosCliente()
            );
        }
    }

    private void abrirSeleccionAsientos() {

        if (funcionSeleccionada == null) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org.example/seleccionarAsientos.fxml")
            );

            Scene scene = new Scene(loader.load());

            SeleccionarAsientoViewController vista = loader.getController();

            Stage ventana = new Stage();
            ventana.setTitle("Seleccionar asientos");
            ventana.setScene(scene);
            ventana.setResizable(false);
            ventana.initOwner(btnSeleccionarAsientos.getScene().getWindow());

            int cantidad = spCantidadEntradas.getValue();

            vista.configurar(funcionSeleccionada, cantidad, ventana);

            ventana.showAndWait();

            List<String> seleccion = vista.getAsientosSeleccionados();

            if (seleccion.size() == cantidad) {

                asientosSeleccionados = seleccion;

                lblAsientosSeleccionados.setText(
                        "Asientos seleccionados: " +
                                String.join(", ", asientosSeleccionados)
                );

                btnConfirmarCompra.setDisable(false);

            } else {

                asientosSeleccionados.clear();

                lblAsientosSeleccionados.setText(
                        "No has seleccionado asientos"
                );

                btnConfirmarCompra.setDisable(true);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void actualizarTotales() {

        int cantidad = spCantidadEntradas.getValue() == null
                ? 1 : spCantidadEntradas.getValue();

        subtotalEntradas = controller.calcularSubtotalEntradas(
                funcionSeleccionada,
                cantidad
        );

        subtotalConfiteria = controller.calcularSubtotalConfiteria(
                consumiblesSeleccionados
        );

        subtotal = subtotalEntradas + subtotalConfiteria;

        puntosUsados = controller.calcularPuntosUsados(
                chkUsarPuntos.isSelected(),
                subtotal
        );

        descuentoPuntos = controller.calcularDescuentoPuntos(puntosUsados);

        totalPagar = subtotal - descuentoPuntos;

        puntosGanados = controller.calcularPuntosGanados(
                funcionSeleccionada,
                cantidad,
                consumiblesSeleccionados
        );

        lblSubtotalEntradas.setText("COP " + formatearDinero(subtotalEntradas));

        lblSubtotalConfiteria.setText("COP " + formatearDinero(subtotalConfiteria));

        lblSubtotal.setText("COP " + formatearDinero(subtotal));

        lblDescuentoPuntos.setText("COP " + formatearDinero(descuentoPuntos));

        lblPuntosGanados.setText(String.valueOf(puntosGanados));

        lblTotalPagar.setText("COP " + formatearDinero(totalPagar));

        chkUsarPuntos.setDisable(controller.obtenerPuntosCliente() <= 0);
    }

    private void invalidarAsientos() {

        asientosSeleccionados.clear();

        lblAsientosSeleccionados.setText("No has seleccionado asientos");

        btnSeleccionarAsientos.setDisable(
                cbPeliculas.getValue() == null ||
                        cbFunciones.getValue() == null
        );

        btnConfirmarCompra.setDisable(true);
    }

    private String formatearDinero(double valor) {
        return String.format(LOCALE, "%,.0f", valor);
    }

    private class ConsumibleCell extends ListCell<Consumible> {

        private final Label lblNombre = new Label();
        private final Label lblDescripcion = new Label();
        private final Label lblPrecio = new Label();
        private final Spinner<Integer> spinner = new Spinner<>();

        private final VBox informacion = new VBox(4);
        private final HBox contenido = new HBox(12);

        private Consumible consumibleActual;

        public ConsumibleCell() {

            spinner.setValueFactory(
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 0)
            );

            spinner.setPrefWidth(85);

            informacion.getChildren().addAll(
                    lblNombre,
                    lblDescripcion,
                    lblPrecio
            );

            HBox.setHgrow(informacion, Priority.ALWAYS);

            contenido.setAlignment(Pos.CENTER_LEFT);
            contenido.getChildren().addAll(informacion, spinner);

            spinner.valueProperty().addListener(
                    (observable, anterior, nuevo) -> {

                        if (consumibleActual == null) {
                            return;
                        }

                        if (nuevo == 0) {
                            consumiblesSeleccionados.remove(consumibleActual);
                        } else {
                            consumiblesSeleccionados.put(consumibleActual, nuevo);
                        }

                        actualizarTotales();
                    }
            );
        }

        @Override
        protected void updateItem(Consumible consumible, boolean empty) {

            super.updateItem(consumible, empty);

            if (empty || consumible == null) {

                consumibleActual = null;
                setGraphic(null);
                setText(null);

            } else {

                consumibleActual = consumible;

                lblNombre.setText(consumible.getNombre());
                lblDescripcion.setText(consumible.getDescripcion());

                lblPrecio.setText(
                        "COP " + formatearDinero(consumible.calcularCosto())
                                + " | " + consumible.calcularPuntos() + " puntos"
                );

                lblNombre.setStyle("-fx-text-fill: #D9B26F; -fx-font-weight: bold;");
                lblDescripcion.setStyle("-fx-text-fill: #A89A94;");
                lblPrecio.setStyle("-fx-text-fill: #F3E9DC;");

                spinner.getValueFactory().setValue(
                        consumiblesSeleccionados.getOrDefault(consumible, 0)
                );

                setGraphic(contenido);
                setText(null);
            }
        }
    }
}