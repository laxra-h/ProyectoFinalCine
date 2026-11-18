package org.example.viewController;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import org.example.controller.SalasController;
import org.example.model.funcionDecorator.Funcion;
import org.example.model.salaPrototype.Asiento;
import org.example.model.salaPrototype.Sala;

import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Set;

public class SalasViewController {

    @FXML private Label lblTotalSalas;
    @FXML private TextField txtNumeroSala;
    @FXML private Label lblCapacidad;
    @FXML private Label lblDistribucion;
    @FXML private Label lblFunciones;
    @FXML private Button btnCrear;
    @FXML private Button btnActualizar;
    @FXML private Button btnEliminar;
    @FXML private Button btnLimpiar;
    @FXML private Label lblMensaje;

    @FXML private Label lblSalaSeleccionada;
    @FXML private GridPane gridAsientos;
    @FXML private Label lblAsientosDisponibles;
    @FXML private Label lblAsientosOcupados;

    @FXML private TableView<Sala> tablaSalas;
    @FXML private TableColumn<Sala, String> colNumeroSala;
    @FXML private TableColumn<Sala, Integer> colCapacidad;
    @FXML private TableColumn<Sala, String> colDistribucion;
    @FXML private TableColumn<Sala, Integer> colFunciones;

    @FXML private TableView<Funcion> tablaFunciones;
    @FXML private TableColumn<Funcion, String> colFuncionCodigo;
    @FXML private TableColumn<Funcion, String> colFuncionPelicula;
    @FXML private TableColumn<Funcion, String> colFuncionFecha;
    @FXML private TableColumn<Funcion, String> colFuncionFormato;
    @FXML private TableColumn<Funcion, String> colFuncionEstado;

    private final SalasController controller = new SalasController();
    private final ObservableList<Sala> salas = FXCollections.observableArrayList();
    private final ObservableList<Funcion> funciones = FXCollections.observableArrayList();
    private Sala salaSeleccionada;

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        configurarTablaSalas();
        configurarTablaFunciones();
        tablaSalas.setItems(salas);
        tablaFunciones.setItems(funciones);

        tablaSalas.getSelectionModel().selectedItemProperty().addListener(
                (observable, anterior, seleccionada) -> cargarSalaSeleccionada(seleccionada)
        );

        tablaFunciones.getSelectionModel().selectedItemProperty().addListener(
                (observable, anterior, seleccionada) -> mostrarAsientos(seleccionada)
        );

        btnActualizar.setDisable(true);
        btnEliminar.setDisable(true);

        limpiarVistaAsientos("Selecciona una sala y después una función para consultar sus asientos.");
        refrescarSalas();
    }

    private void configurarTablaSalas() {
        colNumeroSala.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNumeroSala())
        );
        colCapacidad.setCellValueFactory(data ->
                new SimpleIntegerProperty(controller.obtenerCapacidad(data.getValue())).asObject()
        );
        colDistribucion.setCellValueFactory(data ->
                new SimpleStringProperty(controller.obtenerDistribucion(data.getValue()))
        );
        colFunciones.setCellValueFactory(data ->
                new SimpleIntegerProperty(controller.obtenerCantidadFunciones(data.getValue())).asObject()
        );
    }

    private void configurarTablaFunciones() {
        colFuncionCodigo.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCodigo())
        );
        colFuncionPelicula.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getPelicula() == null
                                ? "Sin película"
                                : data.getValue().getPelicula().getNombre()
                )
        );
        colFuncionFecha.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getFecha() == null
                                ? "Sin fecha"
                                : data.getValue().getFecha().format(FORMATO_FECHA)
                )
        );
        colFuncionFormato.setCellValueFactory(data ->
                new SimpleStringProperty(controller.obtenerFormato(data.getValue()))
        );
        colFuncionEstado.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getEstado() == null
                                ? "SIN ESTADO"
                                : data.getValue().getEstado().name()
                )
        );
    }

    @FXML
    private void crearSala() {
        SalasController.ResultadoOperacion resultado = controller.crearSala(txtNumeroSala.getText());
        finalizarOperacion(resultado);
    }

    @FXML
    private void actualizarSala() {
        if (salaSeleccionada == null) {
            mostrarMensaje("Selecciona una sala de la tabla para actualizarla.", false);
            return;
        }

        SalasController.ResultadoOperacion resultado = controller.actualizarSala(
                salaSeleccionada.getNumeroSala(),
                txtNumeroSala.getText()
        );
        finalizarOperacion(resultado);
    }

    @FXML
    private void eliminarSala() {
        if (salaSeleccionada == null) {
            mostrarMensaje("Selecciona una sala de la tabla para eliminarla.", false);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Eliminar sala");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText(
                "¿Estás segura de eliminar la sala " + salaSeleccionada.getNumeroSala() + "?"
        );
        Optional<ButtonType> respuesta = confirmacion.showAndWait();

        if (respuesta.isEmpty() || respuesta.get() != ButtonType.OK) {
            return;
        }

        SalasController.ResultadoOperacion resultado = controller.eliminarSala(salaSeleccionada);
        finalizarOperacion(resultado);
    }

    @FXML
    private void limpiarFormulario() {
        tablaSalas.getSelectionModel().clearSelection();
        salaSeleccionada = null;
        txtNumeroSala.clear();
        lblCapacidad.setText("-");
        lblDistribucion.setText("-");
        lblFunciones.setText("-");
        funciones.clear();
        btnActualizar.setDisable(true);
        btnEliminar.setDisable(true);
        limpiarVistaAsientos("Selecciona una sala y después una función para consultar sus asientos.");
        mostrarMensaje("", true);
    }

    private void cargarSalaSeleccionada(Sala sala) {
        salaSeleccionada = sala;

        if (sala == null) {
            return;
        }

        txtNumeroSala.setText(sala.getNumeroSala());
        lblCapacidad.setText(String.valueOf(controller.obtenerCapacidad(sala)));
        lblDistribucion.setText(
                sala.getNumeroFilas() + " × " + sala.getNumeroColumnas()
        );
        lblFunciones.setText(String.valueOf(controller.obtenerCantidadFunciones(sala)));

        funciones.setAll(controller.obtenerFunciones(sala));
        tablaFunciones.getSelectionModel().clearSelection();
        limpiarVistaAsientos("Sala " + sala.getNumeroSala() + ": selecciona una función para consultar los asientos.");

        btnActualizar.setDisable(false);
        btnEliminar.setDisable(false);
        mostrarMensaje("Sala seleccionada.", true);
    }

    private void mostrarAsientos(Funcion funcion) {
        if (funcion == null || salaSeleccionada == null) {
            limpiarVistaAsientos("Selecciona una función de la tabla para visualizar sus asientos.");
            return;
        }

        gridAsientos.getChildren().clear();

        Asiento[] asientos = salaSeleccionada.getListAsientos();
        if (asientos == null || asientos.length == 0) {
            limpiarVistaAsientos("La sala no tiene asientos generados.");
            return;
        }

        Set<String> ocupados = controller.obtenerAsientosOcupados(funcion);
        int columnas = salaSeleccionada.getNumeroColumnas();
        int disponibles = 0;
        int cantidadOcupados = 0;

        Label esquina = new Label("");
        gridAsientos.add(esquina, 0, 0);

        for (int columna = 1; columna <= columnas; columna++) {
            Label encabezado = new Label(String.valueOf(columna));
            encabezado.getStyleClass().add("fila-asiento");
            gridAsientos.add(encabezado, columna, 0);
        }

        for (int i = 0; i < asientos.length; i++) {
            Asiento asientoActual = asientos[i];
            if (asientoActual == null) {
                continue;
            }

            int filaGrid = i / columnas + 1;
            int columnaGrid = i % columnas + 1;

            if (columnaGrid == 1) {
                Label encabezadoFila = new Label(String.valueOf(asientoActual.getFila()));
                encabezadoFila.getStyleClass().add("fila-asiento");
                gridAsientos.add(encabezadoFila, 0, filaGrid);
            }

            String codigoAsiento = asientoActual.getCodigo();
            boolean ocupado = ocupados.contains(codigoAsiento);

            Button botonAsiento = new Button(String.valueOf(asientoActual.getNumero()));
            botonAsiento.getStyleClass().add("asiento-boton");
            botonAsiento.getStyleClass().add(ocupado ? "asiento-ocupado" : "asiento-disponible");
            botonAsiento.setStyle(
                    "-fx-pref-width: 25px; -fx-min-width: 25px; -fx-max-width: 25px;" +
                            "-fx-pref-height: 25px; -fx-min-height: 25px; -fx-max-height: 25px;" +
                            "-fx-font-size: 9px; -fx-padding: 0;"
            );
            botonAsiento.setFocusTraversable(false);
            botonAsiento.setMouseTransparent(true);

            gridAsientos.add(botonAsiento, columnaGrid, filaGrid);

            if (ocupado) {
                cantidadOcupados++;
            } else {
                disponibles++;
            }
        }

        lblSalaSeleccionada.setText(
                "Sala: " + salaSeleccionada.getNumeroSala() +
                        " | Función: " + funcion.getCodigo() +
                        " | Estado: " + (funcion.getEstado() == null ? "SIN ESTADO" : funcion.getEstado().name())
        );
        lblAsientosDisponibles.setText("Disponibles: " + disponibles);
        lblAsientosOcupados.setText("Ocupados: " + cantidadOcupados);
    }

    private void limpiarVistaAsientos(String mensaje) {
        gridAsientos.getChildren().clear();
        lblSalaSeleccionada.setText(mensaje);
        lblAsientosDisponibles.setText("Disponibles: -");
        lblAsientosOcupados.setText("Ocupados: -");
    }

    private void refrescarSalas() {
        salas.setAll(controller.obtenerSalas());
        tablaSalas.refresh();
        lblTotalSalas.setText(
                salas.size() + (salas.size() == 1 ? " sala" : " salas")
        );
    }

    private void finalizarOperacion(SalasController.ResultadoOperacion resultado) {
        mostrarMensaje(resultado.mensaje(), resultado.exitoso());
        if (resultado.exitoso()) {
            refrescarSalas();
            limpiarFormulario();
            mostrarMensaje(resultado.mensaje(), true);
        }
    }

    private void mostrarMensaje(String mensaje, boolean exito) {
        lblMensaje.setText(mensaje);
        lblMensaje.getStyleClass().removeAll("mensaje-error", "mensaje-exito");
        if (mensaje != null && !mensaje.isBlank()) {
            lblMensaje.getStyleClass().add(exito ? "mensaje-exito" : "mensaje-error");
        }
    }
}
