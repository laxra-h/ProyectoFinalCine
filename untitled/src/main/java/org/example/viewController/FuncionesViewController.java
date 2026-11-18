package org.example.viewController;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import org.example.controller.FuncionesController;
import org.example.model.funcionDecorator.EstadoFuncion;
import org.example.model.funcionDecorator.Funcion;
import org.example.model.pelicula.Pelicula;
import org.example.model.salaPrototype.Sala;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;

public class FuncionesViewController {

    @FXML private TextField txtCodigo;
    @FXML private ComboBox<String> cbFormato;
    @FXML private ComboBox<Pelicula> cbPelicula;
    @FXML private ComboBox<Sala> cbSala;
    @FXML private DatePicker dpFecha;
    @FXML private TextField txtHora;

    @FXML private Button btnCrear;
    @FXML private Button btnActualizar;
    @FXML private Button btnEliminar;
    @FXML private Button btnLimpiar;
    @FXML private Button btnCambiarEstado;

    @FXML private ComboBox<EstadoFuncion> cbNuevoEstado;
    @FXML private ComboBox<String> cbFiltroEstado;

    @FXML private Label lblTotalFunciones;
    @FXML private Label lblMensaje;
    @FXML private Label lblPrecio;
    @FXML private Label lblEntradas;
    @FXML private Label lblFin;
    @FXML private Label lblEstado;
    @FXML private Label lblRestriccion;

    @FXML private TableView<Funcion> tablaFunciones;
    @FXML private TableColumn<Funcion, String> colCodigo;
    @FXML private TableColumn<Funcion, String> colPelicula;
    @FXML private TableColumn<Funcion, String> colSala;
    @FXML private TableColumn<Funcion, String> colFecha;
    @FXML private TableColumn<Funcion, String> colFormato;
    @FXML private TableColumn<Funcion, String> colPrecio;
    @FXML private TableColumn<Funcion, String> colEstado;

    private final FuncionesController controller = new FuncionesController();
    private final ObservableList<Funcion> funciones = FXCollections.observableArrayList();
    private Funcion funcionSeleccionada;

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final NumberFormat FORMATO_DINERO =
            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO"));

    @FXML
    public void initialize() {
        FORMATO_DINERO.setMaximumFractionDigits(0);
        FORMATO_DINERO.setMinimumFractionDigits(0);

        configurarCombos();
        configurarTabla();

        tablaFunciones.setItems(funciones);
        tablaFunciones.getSelectionModel().selectedItemProperty().addListener(
                (observable, anterior, seleccionada) -> cargarSeleccion(seleccionada)
        );

        cbFiltroEstado.setOnAction(event -> refrescarFunciones());

        btnActualizar.setDisable(true);
        btnEliminar.setDisable(true);
        btnCambiarEstado.setDisable(true);

        limpiarResumen();
        refrescarFunciones();
    }

    private void configurarCombos() {
        cbPelicula.setItems(FXCollections.observableArrayList(controller.obtenerPeliculas()));
        cbPelicula.setConverter(new StringConverter<>() {
            @Override
            public String toString(Pelicula pelicula) {
                return pelicula == null ? "" : pelicula.getNombre();
            }

            @Override
            public Pelicula fromString(String texto) {
                return null;
            }
        });

        cbSala.setItems(FXCollections.observableArrayList(controller.obtenerSalas()));
        cbSala.setConverter(new StringConverter<>() {
            @Override
            public String toString(Sala sala) {
                return sala == null ? "" : sala.getNumeroSala();
            }

            @Override
            public Sala fromString(String texto) {
                return null;
            }
        });

        cbFormato.setItems(FXCollections.observableArrayList(controller.obtenerFormatos()));
        cbNuevoEstado.setItems(FXCollections.observableArrayList(controller.obtenerEstados()));
        cbNuevoEstado.setConverter(new StringConverter<>() {
            @Override
            public String toString(EstadoFuncion estado) {
                return estado == null ? "" : estado.name().replace('_', ' ');
            }

            @Override
            public EstadoFuncion fromString(String texto) {
                return null;
            }
        });

        ObservableList<String> estadosFiltro = FXCollections.observableArrayList("TODOS");
        for (EstadoFuncion estado : controller.obtenerEstados()) {
            estadosFiltro.add(estado.name());
        }
        cbFiltroEstado.setItems(estadosFiltro);
        cbFiltroEstado.setValue("TODOS");
    }

    private void configurarTabla() {
        colCodigo.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCodigo())
        );
        colPelicula.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getPelicula() == null
                                ? "Sin película" : data.getValue().getPelicula().getNombre()
                )
        );
        colSala.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getSala() == null
                                ? "Sin sala" : data.getValue().getSala().getNumeroSala()
                )
        );
        colFecha.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getFecha() == null
                                ? "Sin fecha" : data.getValue().getFecha().format(FORMATO_FECHA)
                )
        );
        colFormato.setCellValueFactory(data ->
                new SimpleStringProperty(controller.obtenerFormato(data.getValue()))
        );
        colPrecio.setCellValueFactory(data ->
                new SimpleStringProperty(FORMATO_DINERO.format(data.getValue().calcularCosto()))
        );
        colEstado.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getEstado() == null
                                ? "SIN ESTADO" : data.getValue().getEstado().name()
                )
        );
    }

    @FXML
    private void crearFuncion() {
        LocalDateTime fecha = obtenerFechaHora();
        if (fecha == null) {
            return;
        }

        FuncionesController.ResultadoOperacion resultado = controller.crearFuncion(
                cbPelicula.getValue(),
                cbSala.getValue(),
                fecha,
                cbFormato.getValue()
        );
        finalizarOperacion(resultado);
    }

    @FXML
    private void actualizarFuncion() {
        if (funcionSeleccionada == null) {
            mostrarMensaje("Selecciona una función de la tabla para actualizarla.", false);
            return;
        }

        LocalDateTime fecha = obtenerFechaHora();
        if (fecha == null) {
            return;
        }

        FuncionesController.ResultadoOperacion resultado = controller.actualizarFuncion(
                funcionSeleccionada,
                cbPelicula.getValue(),
                cbSala.getValue(),
                fecha,
                cbFormato.getValue()
        );
        finalizarOperacion(resultado);
    }

    @FXML
    private void eliminarFuncion() {
        if (funcionSeleccionada == null) {
            mostrarMensaje("Selecciona una función de la tabla para eliminarla.", false);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Eliminar función");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText(
                "¿Estás segura de eliminar la función " + funcionSeleccionada.getCodigo() + "?"
        );
        Optional<ButtonType> respuesta = confirmacion.showAndWait();
        if (respuesta.isEmpty() || respuesta.get() != ButtonType.OK) {
            return;
        }

        finalizarOperacion(controller.eliminarFuncion(funcionSeleccionada));
    }

    @FXML
    private void cambiarEstado() {
        if (funcionSeleccionada == null) {
            mostrarMensaje("Selecciona una función de la tabla.", false);
            return;
        }

        EstadoFuncion nuevoEstado = cbNuevoEstado.getValue();
        if (nuevoEstado == null) {
            mostrarMensaje("Selecciona el estado que deseas aplicar.", false);
            return;
        }

        if (nuevoEstado == EstadoFuncion.CANCELADA) {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Cancelar función");
            confirmacion.setHeaderText(null);
            confirmacion.setContentText(
                    "¿Deseas cancelar la función " + funcionSeleccionada.getCodigo() +
                            "? Las compras pagadas asociadas se procesarán mediante el patrón State."
            );
            Optional<ButtonType> respuesta = confirmacion.showAndWait();
            if (respuesta.isEmpty() || respuesta.get() != ButtonType.OK) {
                return;
            }
        }

        finalizarOperacion(controller.cambiarEstado(funcionSeleccionada, nuevoEstado));
    }

    @FXML
    private void limpiarFormulario() {
        tablaFunciones.getSelectionModel().clearSelection();
        funcionSeleccionada = null;
        txtCodigo.clear();
        cbFormato.getSelectionModel().clearSelection();
        cbPelicula.getSelectionModel().clearSelection();
        cbSala.getSelectionModel().clearSelection();
        dpFecha.setValue(null);
        txtHora.clear();
        cbNuevoEstado.getSelectionModel().clearSelection();
        btnActualizar.setDisable(true);
        btnEliminar.setDisable(true);
        btnCambiarEstado.setDisable(true);
        limpiarResumen();
        mostrarMensaje("", true);
    }

    private LocalDateTime obtenerFechaHora() {
        if (dpFecha.getValue() == null) {
            mostrarMensaje("Selecciona la fecha de la función.", false);
            return null;
        }
        if (txtHora.getText() == null || txtHora.getText().isBlank()) {
            mostrarMensaje("Escribe la hora de la función en formato HH:mm.", false);
            return null;
        }

        try {
            LocalTime hora = LocalTime.parse(txtHora.getText().trim());
            return LocalDateTime.of(dpFecha.getValue(), hora);
        } catch (DateTimeParseException e) {
            mostrarMensaje("La hora no es válida. Utiliza el formato HH:mm, por ejemplo 14:30.", false);
            return null;
        }
    }

    private void cargarSeleccion(Funcion funcion) {
        funcionSeleccionada = funcion;
        if (funcion == null) {
            btnActualizar.setDisable(true);
            btnEliminar.setDisable(true);
            btnCambiarEstado.setDisable(true);
            limpiarResumen();
            return;
        }

        txtCodigo.setText(funcion.getCodigo());
        cbPelicula.setValue(funcion.getPelicula());
        cbSala.setValue(funcion.getSala());
        cbFormato.setValue(controller.obtenerFormato(funcion));

        if (funcion.getFecha() != null) {
            dpFecha.setValue(funcion.getFecha().toLocalDate());
            txtHora.setText(funcion.getFecha().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        }

        cbNuevoEstado.getSelectionModel().clearSelection();
        btnActualizar.setDisable(false);
        btnEliminar.setDisable(false);
        btnCambiarEstado.setDisable(false);
        mostrarResumen(funcion);

        if (controller.tieneComprasAsociadas(funcion)) {
            mostrarRestriccion(
                    "Esta función tiene compras asociadas: no se puede modificar ni eliminar. Puedes cambiar su estado o cancelarla para procesar los reembolsos."
            );
        } else if (funcion.getEstado() == EstadoFuncion.FINALIZADA ||
                funcion.getEstado() == EstadoFuncion.CANCELADA) {
            mostrarRestriccion(
                    "La función está cerrada y no se puede modificar. Si no tiene compras asociadas, puedes eliminarla."
            );
        } else {
            mostrarRestriccion(
                    "Puedes actualizar o eliminar esta función mientras no tenga compras asociadas."
            );
        }
    }

    private void mostrarResumen(Funcion funcion) {
        lblPrecio.setText(FORMATO_DINERO.format(funcion.calcularCosto()));
        lblEntradas.setText(String.valueOf(controller.obtenerEntradasVendidas(funcion)));

        LocalDateTime fin = controller.obtenerFinalizacion(funcion);
        lblFin.setText(fin == null ? "-" : fin.format(FORMATO_FECHA));
        lblEstado.setText(funcion.getEstado() == null ? "-" : funcion.getEstado().name());
    }

    private void limpiarResumen() {
        lblPrecio.setText("-");
        lblEntradas.setText("-");
        lblFin.setText("-");
        lblEstado.setText("-");
        lblRestriccion.setText("Selecciona una función para consultar su resumen.");
    }

    private void mostrarRestriccion(String mensaje) {
        lblRestriccion.setText(mensaje);
    }

    private void refrescarFunciones() {
        String filtro = cbFiltroEstado.getValue();
        ObservableList<Funcion> resultado = FXCollections.observableArrayList();

        for (Funcion funcion : controller.obtenerFunciones()) {
            if (filtro == null || "TODOS".equals(filtro) ||
                    (funcion.getEstado() != null && funcion.getEstado().name().equals(filtro))) {
                resultado.add(funcion);
            }
        }

        funciones.setAll(resultado);
        tablaFunciones.refresh();
    }

    private void finalizarOperacion(FuncionesController.ResultadoOperacion resultado) {
        mostrarMensaje(resultado.mensaje(), resultado.exitoso());
        if (resultado.exitoso()) {
            refrescarFunciones();
            limpiarFormulario();
            mostrarMensaje(resultado.mensaje(), true);
        } else {
            if (funcionSeleccionada != null) {
                mostrarResumen(funcionSeleccionada);
            }
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
