package org.example.viewController;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import org.example.controller.ConfiteriaAdminController;
import org.example.model.consumibleComposite.Combo;
import org.example.model.consumibleComposite.Consumible;
import org.example.model.consumibleComposite.Producto;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class ConfiteriaAdminViewController {

    @FXML private TextField txtProductoNombre;
    @FXML private TextField txtProductoDescripcion;
    @FXML private TextField txtProductoPrecio;
    @FXML private TextField txtProductoPuntos;
    @FXML private Button btnCrearProducto;
    @FXML private Button btnActualizarProducto;
    @FXML private Button btnEliminarProducto;
    @FXML private Button btnLimpiarProducto;
    @FXML private Label lblMensajeProducto;
    @FXML private Label lblTotalProductos;
    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, String> colProductoNombre;
    @FXML private TableColumn<Producto, String> colProductoDescripcion;
    @FXML private TableColumn<Producto, String> colProductoPrecio;
    @FXML private TableColumn<Producto, String> colProductoPuntos;

    @FXML private TextField txtComboNombre;
    @FXML private TextField txtComboDescripcion;
    @FXML private ComboBox<Consumible> cbComponente;
    @FXML private Button btnAgregarComponente;
    @FXML private Button btnQuitarComponente;
    @FXML private Button btnCrearCombo;
    @FXML private Button btnActualizarCombo;
    @FXML private Button btnEliminarCombo;
    @FXML private Button btnLimpiarCombo;
    @FXML private Label lblMensajeCombo;
    @FXML private Label lblTotalCombos;
    @FXML private Label lblPrecioCombo;
    @FXML private Label lblPuntosCombo;
    @FXML private ListView<Consumible> listaComponentes;
    @FXML private TableView<Combo> tablaCombos;
    @FXML private TableColumn<Combo, String> colComboNombre;
    @FXML private TableColumn<Combo, String> colComboDescripcion;
    @FXML private TableColumn<Combo, String> colComboContenido;
    @FXML private TableColumn<Combo, String> colComboPrecio;
    @FXML private TableColumn<Combo, String> colComboPuntos;

    private final ConfiteriaAdminController controller = new ConfiteriaAdminController();    private final ObservableList<Producto> productos = FXCollections.observableArrayList();
    private final ObservableList<Combo> combos = FXCollections.observableArrayList();
    private final ObservableList<Consumible> componentesSeleccionados =
            FXCollections.observableArrayList();

    private Producto productoSeleccionado;
    private Combo comboSeleccionado;
    private static final NumberFormat DINERO =
            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO"));

    @FXML
    public void initialize() {
        DINERO.setMaximumFractionDigits(0);
        DINERO.setMinimumFractionDigits(0);

        configurarTablaProductos();
        configurarTablaCombos();
        configurarSelectorComponentes();

        tablaProductos.setItems(productos);
        tablaCombos.setItems(combos);
        listaComponentes.setItems(componentesSeleccionados);

        tablaProductos.getSelectionModel().selectedItemProperty().addListener(
                (observable, anterior, seleccionado) -> cargarProductoSeleccionado(seleccionado)
        );
        tablaCombos.getSelectionModel().selectedItemProperty().addListener(
                (observable, anterior, seleccionado) -> cargarComboSeleccionado(seleccionado)
        );
        listaComponentes.getSelectionModel().selectedIndexProperty().addListener(
                (observable, anterior, seleccionado) ->
                        btnQuitarComponente.setDisable(seleccionado == null || seleccionado.intValue() < 0)
        );

        btnActualizarProducto.setDisable(true);
        btnEliminarProducto.setDisable(true);
        btnActualizarCombo.setDisable(true);
        btnEliminarCombo.setDisable(true);
        btnQuitarComponente.setDisable(true);

        refrescarProductos();
        refrescarCombos();
        actualizarResumenCombo();
    }

    private void configurarTablaProductos() {
        colProductoNombre.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNombre()));
        colProductoDescripcion.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDescripcion()));
        colProductoPrecio.setCellValueFactory(data ->
                new SimpleStringProperty(DINERO.format(data.getValue().calcularCosto())));
        colProductoPuntos.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().calcularPuntos() + " pts"));
    }

    private void configurarTablaCombos() {
        colComboNombre.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNombre()));
        colComboDescripcion.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDescripcion()));
        colComboContenido.setCellValueFactory(data ->
                new SimpleStringProperty(resumirComponentes(data.getValue())));
        colComboPrecio.setCellValueFactory(data ->
                new SimpleStringProperty(DINERO.format(data.getValue().calcularCosto())));
        colComboPuntos.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().calcularPuntos() + " pts"));
    }

    private void configurarSelectorComponentes() {
        cbComponente.setConverter(new StringConverter<>() {
            @Override
            public String toString(Consumible consumible) {
                return textoComponente(consumible);
            }

            @Override
            public Consumible fromString(String texto) {
                return null;
            }
        });

        listaComponentes.setCellFactory(lista -> new ListCell<>() {
            @Override
            protected void updateItem(Consumible consumible, boolean vacio) {
                super.updateItem(consumible, vacio);
                setText(vacio || consumible == null ? null : textoComponente(consumible));
            }
        });
    }

    private String textoComponente(Consumible consumible) {
        if (consumible == null) {
            return "";
        }
        String tipo = consumible instanceof Producto ? "Producto" : "Combo";
        return tipo + " · " + consumible.getNombre();
    }

    private String resumirComponentes(Combo combo) {
        Map<String, Integer> cantidades = new LinkedHashMap<>();
        for (Consumible componente : combo.getListaConsumibles()) {
            String tipo = componente instanceof Producto ? "Producto" : "Combo";
            cantidades.merge(tipo + ": " + componente.getNombre(), 1, Integer::sum);
        }
        List<String> resumen = new ArrayList<>();
        for (Map.Entry<String, Integer> entrada : cantidades.entrySet()) {
            resumen.add(entrada.getKey() + (entrada.getValue() > 1 ? " x" + entrada.getValue() : ""));
        }
        return String.join(", ", resumen);
    }

    private void refrescarProductos() {
        productos.setAll(controller.obtenerProductos());
        lblTotalProductos.setText(productos.size() + " productos");
    }

    private void refrescarCombos() {
        combos.setAll(controller.obtenerCombos());
        lblTotalCombos.setText(combos.size() + " combos");
    }

    private void cargarProductoSeleccionado(Producto producto) {
        productoSeleccionado = producto;
        boolean seleccionado = producto != null;
        btnActualizarProducto.setDisable(!seleccionado);
        btnEliminarProducto.setDisable(!seleccionado);
        if (!seleccionado) {
            return;
        }

        txtProductoNombre.setText(producto.getNombre());
        txtProductoDescripcion.setText(producto.getDescripcion());
        txtProductoPrecio.setText(String.valueOf(producto.getCosto()));
        txtProductoPuntos.setText(String.valueOf(producto.getPuntosRedimir()));
        mostrarMensajeProducto("Producto seleccionado para editar.", true);
    }

    private void cargarComboSeleccionado(Combo combo) {
        comboSeleccionado = combo;
        boolean seleccionado = combo != null;
        btnActualizarCombo.setDisable(!seleccionado);
        btnEliminarCombo.setDisable(!seleccionado);

        componentesSeleccionados.clear();
        if (!seleccionado) {
            actualizarSelectorComponentes();
            actualizarResumenCombo();
            return;
        }

        txtComboNombre.setText(combo.getNombre());
        txtComboDescripcion.setText(combo.getDescripcion());
        componentesSeleccionados.setAll(combo.getListaConsumibles());
        actualizarSelectorComponentes();
        actualizarResumenCombo();
        mostrarMensajeCombo("Combo seleccionado para editar.", true);
    }

    private void actualizarSelectorComponentes() {
        cbComponente.setItems(FXCollections.observableArrayList(
                controller.obtenerComponentesDisponibles(comboSeleccionado)
        ));
        cbComponente.getSelectionModel().clearSelection();
    }

    @FXML
    private void crearProducto() {
        Double precio = obtenerPrecio(txtProductoPrecio.getText());
        Integer puntos = obtenerPuntos(txtProductoPuntos.getText());
        if (precio == null || puntos == null) {
            mostrarMensajeProducto("Ingresa un precio válido y una cantidad entera de puntos.", false);
            return;
        }

        finalizarProducto(controller.crearProducto(
                txtProductoNombre.getText(),
                txtProductoDescripcion.getText(),
                precio,
                puntos
        ));
    }

    @FXML
    private void actualizarProducto() {
        if (productoSeleccionado == null) {
            mostrarMensajeProducto("Selecciona un producto de la tabla.", false);
            return;
        }
        Double precio = obtenerPrecio(txtProductoPrecio.getText());
        Integer puntos = obtenerPuntos(txtProductoPuntos.getText());
        if (precio == null || puntos == null) {
            mostrarMensajeProducto("Ingresa un precio válido y una cantidad entera de puntos.", false);
            return;
        }

        finalizarProducto(controller.actualizarProducto(
                productoSeleccionado,
                txtProductoNombre.getText(),
                txtProductoDescripcion.getText(),
                precio,
                puntos
        ));
    }

    @FXML
    private void eliminarProducto() {
        if (productoSeleccionado == null) {
            mostrarMensajeProducto("Selecciona un producto de la tabla.", false);
            return;
        }
        if (!confirmar("Eliminar producto", "¿Deseas eliminar el producto \"" +
                productoSeleccionado.getNombre() + "\"?")) {
            return;
        }
        finalizarProducto(controller.eliminarProducto(productoSeleccionado));
    }

    @FXML
    private void limpiarProducto() {
        tablaProductos.getSelectionModel().clearSelection();
        productoSeleccionado = null;
        txtProductoNombre.clear();
        txtProductoDescripcion.clear();
        txtProductoPrecio.clear();
        txtProductoPuntos.clear();
        btnActualizarProducto.setDisable(true);
        btnEliminarProducto.setDisable(true);
        mostrarMensajeProducto("Formulario listo para registrar un producto.", true);
    }

    private void finalizarProducto(ConfiteriaAdminController.ResultadoOperacion resultado) {
        mostrarMensajeProducto(resultado.mensaje(), resultado.exitoso());
        if (resultado.exitoso()) {
            refrescarProductos();
            refrescarCombos();
            limpiarProducto();
        }
    }

    @FXML
    private void agregarComponente() {
        Consumible componente = cbComponente.getValue();
        if (componente == null) {
            mostrarMensajeCombo("Selecciona un producto o combo para agregar.", false);
            return;
        }
        componentesSeleccionados.add(componente);
        cbComponente.getSelectionModel().clearSelection();
        actualizarResumenCombo();
        mostrarMensajeCombo("Componente agregado al combo.", true);
    }

    @FXML
    private void quitarComponente() {
        int indice = listaComponentes.getSelectionModel().getSelectedIndex();
        if (indice < 0 || indice >= componentesSeleccionados.size()) {
            mostrarMensajeCombo("Selecciona un componente de la lista para quitarlo.", false);
            return;
        }
        componentesSeleccionados.remove(indice);
        actualizarResumenCombo();
        mostrarMensajeCombo("Componente retirado del combo.", true);
    }

    @FXML
    private void crearCombo() {
        finalizarCombo(controller.crearCombo(
                txtComboNombre.getText(),
                txtComboDescripcion.getText(),
                new ArrayList<>(componentesSeleccionados)
        ));
    }

    @FXML
    private void actualizarCombo() {
        if (comboSeleccionado == null) {
            mostrarMensajeCombo("Selecciona un combo de la tabla.", false);
            return;
        }
        finalizarCombo(controller.actualizarCombo(
                comboSeleccionado,
                txtComboNombre.getText(),
                txtComboDescripcion.getText(),
                new ArrayList<>(componentesSeleccionados)
        ));
    }

    @FXML
    private void eliminarCombo() {
        if (comboSeleccionado == null) {
            mostrarMensajeCombo("Selecciona un combo de la tabla.", false);
            return;
        }
        if (!confirmar("Eliminar combo", "¿Deseas eliminar el combo \"" +
                comboSeleccionado.getNombre() + "\"?")) {
            return;
        }
        finalizarCombo(controller.eliminarCombo(comboSeleccionado));
    }

    @FXML
    private void limpiarCombo() {
        tablaCombos.getSelectionModel().clearSelection();
        comboSeleccionado = null;
        txtComboNombre.clear();
        txtComboDescripcion.clear();
        componentesSeleccionados.clear();
        actualizarSelectorComponentes();
        actualizarResumenCombo();
        btnActualizarCombo.setDisable(true);
        btnEliminarCombo.setDisable(true);
        btnQuitarComponente.setDisable(true);
        mostrarMensajeCombo("Formulario listo para registrar un combo.", true);
    }

    private void finalizarCombo(ConfiteriaAdminController.ResultadoOperacion resultado) {
        mostrarMensajeCombo(resultado.mensaje(), resultado.exitoso());
        if (resultado.exitoso()) {
            refrescarCombos();
            limpiarCombo();
        }
    }

    private void actualizarResumenCombo() {
        double precio = 0;
        int puntos = 0;
        for (Consumible componente : componentesSeleccionados) {
            precio += componente.calcularCosto();
            puntos += componente.calcularPuntos();
        }
        lblPrecioCombo.setText(DINERO.format(precio));
        lblPuntosCombo.setText(puntos + " pts");
    }

    private Double obtenerPrecio(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(texto.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer obtenerPuntos(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            return null;
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

    private void mostrarMensajeProducto(String mensaje, boolean exitoso) {
        lblMensajeProducto.setText(mensaje);
        lblMensajeProducto.getStyleClass().removeAll("mensaje-exito", "mensaje-error");
        lblMensajeProducto.getStyleClass().add(exitoso ? "mensaje-exito" : "mensaje-error");
    }

    private void mostrarMensajeCombo(String mensaje, boolean exitoso) {
        lblMensajeCombo.setText(mensaje);
        lblMensajeCombo.getStyleClass().removeAll("mensaje-exito", "mensaje-error");
        lblMensajeCombo.getStyleClass().add(exitoso ? "mensaje-exito" : "mensaje-error");
    }
}
