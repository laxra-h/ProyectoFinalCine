package org.example.viewController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.beans.property.SimpleStringProperty;
import javafx.util.StringConverter;
import org.example.controller.PeliculasController;
import org.example.model.pelicula.ClasificacionPelicula;
import org.example.model.pelicula.GeneroPelicula;
import org.example.model.pelicula.Pelicula;

import java.util.Optional;

public class PeliculasViewController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtDirector;
    @FXML private TextArea txtDescripcion;
    @FXML private ComboBox<GeneroPelicula> cbGenero;
    @FXML private ComboBox<ClasificacionPelicula> cbClasificacion;
    @FXML private Spinner<Integer> spDuracion;

    @FXML private TableView<Pelicula> tablaPeliculas;
    @FXML private TableColumn<Pelicula, String> colNombre;
    @FXML private TableColumn<Pelicula, String> colDirector;
    @FXML private TableColumn<Pelicula, String> colGenero;
    @FXML private TableColumn<Pelicula, String> colClasificacion;
    @FXML private TableColumn<Pelicula, String> colDuracion;

    @FXML private Button btnActualizar;
    @FXML private Button btnEliminar;
    @FXML private Label lblMensaje;

    private final PeliculasController controller = new PeliculasController();
    private final ObservableList<Pelicula> peliculas = FXCollections.observableArrayList();
    private String nombreSeleccionado;

    @FXML
    public void initialize() {
        configurarFormulario();
        configurarTabla();
        tablaPeliculas.setItems(peliculas);
        tablaPeliculas.getSelectionModel().selectedItemProperty().addListener(
                (observable, anterior, seleccionada) -> cargarSeleccion(seleccionada)
        );
        btnActualizar.setDisable(true);
        btnEliminar.setDisable(true);
        refrescarPeliculas();
    }

    private void configurarFormulario() {
        cbGenero.setItems(FXCollections.observableArrayList(controller.obtenerGeneros()));
        cbClasificacion.setItems(FXCollections.observableArrayList(controller.obtenerClasificaciones()));

        cbGenero.setConverter(new StringConverter<>() {
            @Override
            public String toString(GeneroPelicula genero) {
                return genero == null ? "" : textoGenero(genero);
            }

            @Override
            public GeneroPelicula fromString(String texto) {
                return null;
            }
        });

        cbClasificacion.setConverter(new StringConverter<>() {
            @Override
            public String toString(ClasificacionPelicula clasificacion) {
                return clasificacion == null ? "" : textoClasificacion(clasificacion);
            }

            @Override
            public ClasificacionPelicula fromString(String texto) {
                return null;
            }
        });

        spDuracion.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 400, 120)
        );
    }

    private void configurarTabla() {
        colNombre.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNombre())
        );
        colDirector.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDirector())
        );
        colGenero.setCellValueFactory(data ->
                new SimpleStringProperty(textoGenero(data.getValue().getGenero()))
        );
        colClasificacion.setCellValueFactory(data ->
                new SimpleStringProperty(textoClasificacion(data.getValue().getClasificacion()))
        );
        colDuracion.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDuracionMin() + " min")
        );
    }

    @FXML
    private void crearPelicula() {
        PeliculasController.ResultadoOperacion resultado = controller.crearPelicula(
                txtNombre.getText(),
                txtDirector.getText(),
                txtDescripcion.getText(),
                cbClasificacion.getValue(),
                cbGenero.getValue(),
                spDuracion.getValue()
        );
        finalizarOperacion(resultado);
    }

    @FXML
    private void actualizarPelicula() {
        if (nombreSeleccionado == null) {
            mostrarMensaje("Selecciona una película de la tabla para actualizarla.", false);
            return;
        }

        PeliculasController.ResultadoOperacion resultado = controller.actualizarPelicula(
                nombreSeleccionado,
                txtNombre.getText(),
                txtDirector.getText(),
                txtDescripcion.getText(),
                cbClasificacion.getValue(),
                cbGenero.getValue(),
                spDuracion.getValue()
        );
        finalizarOperacion(resultado);
    }

    @FXML
    private void eliminarPelicula() {
        if (nombreSeleccionado == null) {
            mostrarMensaje("Selecciona una película de la tabla para eliminarla.", false);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Eliminar película");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Estás segura de eliminar la película " + nombreSeleccionado + "?");
        Optional<ButtonType> respuesta = confirmacion.showAndWait();

        if (respuesta.isEmpty() || respuesta.get() != ButtonType.OK) {
            return;
        }

        PeliculasController.ResultadoOperacion resultado = controller.eliminarPelicula(nombreSeleccionado);
        finalizarOperacion(resultado);
    }

    @FXML
    private void limpiarFormulario() {
        limpiarCampos();
        mostrarMensaje("", true);
    }

    @FXML
    private void refrescarPeliculas() {
        peliculas.setAll(controller.obtenerPeliculas());
        tablaPeliculas.refresh();
    }

    private void finalizarOperacion(PeliculasController.ResultadoOperacion resultado) {
        mostrarMensaje(resultado.mensaje(), resultado.exitoso());
        if (resultado.exitoso()) {
            refrescarPeliculas();
            limpiarCampos();
        }
    }

    private void cargarSeleccion(Pelicula pelicula) {
        if (pelicula == null) {
            nombreSeleccionado = null;
            btnActualizar.setDisable(true);
            btnEliminar.setDisable(true);
            return;
        }

        nombreSeleccionado = pelicula.getNombre();
        txtNombre.setText(pelicula.getNombre());
        txtDirector.setText(pelicula.getDirector());
        txtDescripcion.setText(pelicula.getDescripcion());
        cbGenero.setValue(pelicula.getGenero());
        cbClasificacion.setValue(pelicula.getClasificacion());
        spDuracion.getValueFactory().setValue(pelicula.getDuracionMin());
        btnActualizar.setDisable(false);
        btnEliminar.setDisable(false);
        mostrarMensaje("Película seleccionada para editar.", true);
    }

    private void limpiarCampos() {
        tablaPeliculas.getSelectionModel().clearSelection();
        nombreSeleccionado = null;
        txtNombre.clear();
        txtDirector.clear();
        txtDescripcion.clear();
        cbGenero.getSelectionModel().clearSelection();
        cbClasificacion.getSelectionModel().clearSelection();
        spDuracion.getValueFactory().setValue(120);
        btnActualizar.setDisable(true);
        btnEliminar.setDisable(true);
    }

    private void mostrarMensaje(String mensaje, boolean exito) {
        lblMensaje.setText(mensaje);
        lblMensaje.getStyleClass().removeAll("mensaje-error", "mensaje-exito");
        if (!mensaje.isBlank()) {
            lblMensaje.getStyleClass().add(exito ? "mensaje-exito" : "mensaje-error");
        }
    }

    private String textoGenero(GeneroPelicula genero) {
        if (genero == null) {
            return "";
        }
        return switch (genero) {
            case COMEDIA -> "Comedia";
            case TERROR -> "Terror";
            case ROMANCE -> "Romance";
            case ACCION -> "Acción";
            case CIENCIA_FICCION -> "Ciencia ficción";
        };
    }

    private String textoClasificacion(ClasificacionPelicula clasificacion) {
        if (clasificacion == null) {
            return "";
        }
        return switch (clasificacion) {
            case TODOS -> "Para todos";
            case MAYORES_7 -> "Mayores de 7 años";
            case MAYORES_12 -> "Mayores de 12 años";
            case MAYORES_15 -> "Mayores de 15 años";
            case MAYORES_18 -> "Mayores de 18 años";
        };
    }
}
