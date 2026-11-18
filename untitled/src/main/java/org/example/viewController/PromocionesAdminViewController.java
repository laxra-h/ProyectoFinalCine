package org.example.viewController;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import org.example.controller.PromocionesAdminController;
import org.example.model.promocion.Promocion;
import org.example.model.promocion.TipoPromocion;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class PromocionesAdminViewController {

    @FXML private TextField txtBuscar;
    @FXML private TextField txtNombre;
    @FXML private TextField txtDescripcion;
    @FXML private TextField txtPorcentaje;
    @FXML private ComboBox<TipoPromocion> cbTipo;
    @FXML private DatePicker dpFechaInicio;
    @FXML private DatePicker dpFechaFin;
    @FXML private CheckBox chkActiva;

    @FXML private Button btnCrear;
    @FXML private Button btnActualizar;
    @FXML private Button btnEliminar;
    @FXML private Button btnLimpiar;

    @FXML private Label lblMensaje;

    @FXML private TableView<Promocion> tablaPromociones;
    @FXML private TableColumn<Promocion, String> colNombre;
    @FXML private TableColumn<Promocion, String> colTipo;
    @FXML private TableColumn<Promocion, String> colDescuento;
    @FXML private TableColumn<Promocion, String> colVigencia;
    @FXML private TableColumn<Promocion, String> colEstado;

    private final PromocionesAdminController controller = new PromocionesAdminController();
    private final ObservableList<Promocion> promociones = FXCollections.observableArrayList();
    private FilteredList<Promocion> promocionesFiltradas;
    private Promocion promocionSeleccionada;

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        configurarSelectorTipo();
        configurarTabla();

        promocionesFiltradas = new FilteredList<>(promociones, promocion -> true);
        tablaPromociones.setItems(promocionesFiltradas);

        tablaPromociones.getSelectionModel().selectedItemProperty().addListener(
                (observable, anterior, seleccionado) -> cargarPromocionSeleccionada(seleccionado)
        );
        txtBuscar.textProperty().addListener(
                (observable, anterior, texto) -> filtrarPromociones(texto)
        );

        btnActualizar.setDisable(true);
        btnEliminar.setDisable(true);
        refrescarPromociones();
        limpiarFormulario();
    }

    private void configurarSelectorTipo() {
        cbTipo.setItems(FXCollections.observableArrayList(controller.obtenerTiposPromocion()));
        cbTipo.setConverter(new StringConverter<>() {
            @Override
            public String toString(TipoPromocion tipo) {
                return tipo == null ? "" : tipo.getEtiqueta();
            }

            @Override
            public TipoPromocion fromString(String texto) {
                if (texto == null) return null;
                for (TipoPromocion tipo : TipoPromocion.values()) {
                    if (tipo.getEtiqueta().equalsIgnoreCase(texto)) return tipo;
                }
                return null;
            }
        });
    }

    private void configurarTabla() {
        colNombre.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNombre()));
        colTipo.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTipo().getEtiqueta()));
        colDescuento.setCellValueFactory(data ->
                new SimpleStringProperty(formatearPorcentaje(data.getValue().getPorcentajeDescuento())));
        colVigencia.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFechaInicio().format(FECHA)
                        + " - " + data.getValue().getFechaFin().format(FECHA)));
        colEstado.setCellValueFactory(data ->
                new SimpleStringProperty(obtenerEstado(data.getValue())));
    }

    private String formatearPorcentaje(double porcentaje) {
        return String.format(Locale.forLanguageTag("es-CO"), "%s %%",
                java.math.BigDecimal.valueOf(porcentaje).stripTrailingZeros().toPlainString());
    }

    private String obtenerEstado(Promocion promocion) {
        LocalDate hoy = LocalDate.now();
        if (!promocion.isActiva()) return "INACTIVA";
        if (hoy.isBefore(promocion.getFechaInicio())) return "PROGRAMADA";
        if (hoy.isAfter(promocion.getFechaFin())) return "VENCIDA";
        return "ACTIVA";
    }

    private void refrescarPromociones() {
        promociones.setAll(controller.obtenerPromociones());
    }

    private void filtrarPromociones(String texto) {
        String filtro = texto == null ? "" : texto.trim().toLowerCase(Locale.ROOT);
        promocionesFiltradas.setPredicate(promocion -> filtro.isEmpty()
                || promocion.getNombre().toLowerCase(Locale.ROOT).contains(filtro)
                || promocion.getDescripcion().toLowerCase(Locale.ROOT).contains(filtro)
                || promocion.getTipo().getEtiqueta().toLowerCase(Locale.ROOT).contains(filtro)
                || obtenerEstado(promocion).toLowerCase(Locale.ROOT).contains(filtro));
    }

    private void cargarPromocionSeleccionada(Promocion promocion) {
        promocionSeleccionada = promocion;
        boolean seleccionada = promocion != null;
        btnActualizar.setDisable(!seleccionada);
        btnEliminar.setDisable(!seleccionada);
        if (!seleccionada) return;

        txtNombre.setText(promocion.getNombre());
        txtDescripcion.setText(promocion.getDescripcion());
        cbTipo.setValue(promocion.getTipo());
        txtPorcentaje.setText(String.valueOf(promocion.getPorcentajeDescuento()));
        dpFechaInicio.setValue(promocion.getFechaInicio());
        dpFechaFin.setValue(promocion.getFechaFin());
        chkActiva.setSelected(promocion.isActiva());
        mostrarMensaje("Promoción seleccionada para editar.", true);
    }

    @FXML
    private void crearPromocion() {
        Double porcentaje = obtenerPorcentaje();
        if (porcentaje == null) {
            mostrarMensaje("Ingresa un porcentaje válido. Puedes usar decimales.", false);
            return;
        }
        finalizarOperacion(controller.crearPromocion(
                txtNombre.getText(), txtDescripcion.getText(), cbTipo.getValue(),
                porcentaje, dpFechaInicio.getValue(), dpFechaFin.getValue(), chkActiva.isSelected()
        ));
    }

    @FXML
    private void actualizarPromocion() {
        if (promocionSeleccionada == null) {
            mostrarMensaje("Selecciona una promoción de la tabla.", false);
            return;
        }
        Double porcentaje = obtenerPorcentaje();
        if (porcentaje == null) {
            mostrarMensaje("Ingresa un porcentaje válido. Puedes usar decimales.", false);
            return;
        }
        finalizarOperacion(controller.actualizarPromocion(
                promocionSeleccionada, txtNombre.getText(), txtDescripcion.getText(), cbTipo.getValue(),
                porcentaje, dpFechaInicio.getValue(), dpFechaFin.getValue(), chkActiva.isSelected()
        ));
    }

    @FXML
    private void eliminarPromocion() {
        if (promocionSeleccionada == null) {
            mostrarMensaje("Selecciona una promoción de la tabla.", false);
            return;
        }
        if (!confirmar("Eliminar promoción", "¿Deseas eliminar la promoción \""
                + promocionSeleccionada.getNombre() + "\"?")) return;
        finalizarOperacion(controller.eliminarPromocion(promocionSeleccionada));
    }

    @FXML
    private void limpiarFormulario() {
        tablaPromociones.getSelectionModel().clearSelection();
        promocionSeleccionada = null;
        txtNombre.clear();
        txtDescripcion.clear();
        txtPorcentaje.clear();
        cbTipo.getSelectionModel().clearSelection();
        dpFechaInicio.setValue(null);
        dpFechaFin.setValue(null);
        chkActiva.setSelected(true);
        btnActualizar.setDisable(true);
        btnEliminar.setDisable(true);
        mostrarMensaje("Formulario listo para registrar una promoción.", true);
    }

    private Double obtenerPorcentaje() {
        String texto = txtPorcentaje.getText();
        if (texto == null || texto.trim().isEmpty()) return null;
        try {
            return Double.parseDouble(texto.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void finalizarOperacion(PromocionesAdminController.ResultadoOperacion resultado) {
        mostrarMensaje(resultado.mensaje(), resultado.exitoso());
        if (resultado.exitoso()) {
            refrescarPromociones();
            limpiarFormulario();
        }
    }

    private boolean confirmar(String titulo, String contenido) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(contenido);
        Optional<ButtonType> respuesta = alerta.showAndWait();
        return respuesta.isPresent() && respuesta.get() == ButtonType.OK;
    }

    private void mostrarMensaje(String mensaje, boolean exitoso) {
        lblMensaje.setText(mensaje);
        lblMensaje.getStyleClass().removeAll("mensaje-exito", "mensaje-error");
        lblMensaje.getStyleClass().add(exitoso ? "mensaje-exito" : "mensaje-error");
    }
}
