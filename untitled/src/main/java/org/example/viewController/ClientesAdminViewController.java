package org.example.viewController;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import org.example.controller.ClientesAdminController;
import org.example.model.usuarios.Cliente;
import org.example.model.tarjeta.TarjetaVirtual;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

public class ClientesAdminViewController {

    @FXML private TextField txtBuscar;
    @FXML private TextField txtId;
    @FXML private TextField txtNombre;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCorreo;
    @FXML private PasswordField txtContrasenia;
    @FXML private DatePicker dpFechaNacimiento;

    @FXML private Button btnCrear;
    @FXML private Button btnActualizar;
    @FXML private Button btnEliminar;

    @FXML private Label lblMensaje;
    @FXML private Label lblPuntos;
    @FXML private Label lblSaldo;
    @FXML private Label lblCompras;
    @FXML private Label lblEstadoTarjeta;

    @FXML private TableView<Cliente> tablaClientes;
    @FXML private TableColumn<Cliente, String> colId;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private TableColumn<Cliente, String> colCorreo;
    @FXML private TableColumn<Cliente, String> colFechaNacimiento;
    @FXML private TableColumn<Cliente, String> colPuntos;
    @FXML private TableColumn<Cliente, String> colSaldo;
    @FXML private TableColumn<Cliente, String> colCompras;
    @FXML private TableColumn<Cliente, String> colEstadoTarjeta;

    private final ClientesAdminController controller = new ClientesAdminController();
    private final ObservableList<Cliente> clientes = FXCollections.observableArrayList();
    private FilteredList<Cliente> clientesFiltrados;
    private Cliente clienteSeleccionado;

    private static final NumberFormat DINERO =
            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO"));
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        DINERO.setMaximumFractionDigits(0);
        DINERO.setMinimumFractionDigits(0);

        configurarFormulario();
        configurarTabla();

        clientesFiltrados = new FilteredList<>(clientes, cliente -> true);
        tablaClientes.setItems(clientesFiltrados);

        tablaClientes.getSelectionModel().selectedItemProperty().addListener(
                (observable, anterior, seleccionado) -> cargarClienteSeleccionado(seleccionado)
        );
        txtBuscar.textProperty().addListener(
                (observable, anterior, texto) -> filtrarClientes(texto)
        );

        btnActualizar.setDisable(true);
        btnEliminar.setDisable(true);
        limpiarFormulario();
    }

    private void configurarFormulario() {
        txtTelefono.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().matches("\\d{0,10}") ? change : null
        ));
        dpFechaNacimiento.setDayCellFactory(picker -> new javafx.scene.control.DateCell() {
            @Override
            public void updateItem(LocalDate fecha, boolean vacio) {
                super.updateItem(fecha, vacio);
                setDisable(vacio || fecha.isAfter(LocalDate.now()));
            }
        });
    }

    private void configurarTabla() {
        colId.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getId()));
        colNombre.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNombre()));
        colTelefono.setCellValueFactory(data ->
                new SimpleStringProperty(valorTexto(data.getValue().getTelefono())));
        colCorreo.setCellValueFactory(data ->
                new SimpleStringProperty(valorTexto(data.getValue().getEmail())));
        colFechaNacimiento.setCellValueFactory(data ->
                new SimpleStringProperty(formatearFecha(data.getValue().getFechaNacimiento())));
        colPuntos.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getPuntos() + " pts"));
        colSaldo.setCellValueFactory(data ->
                new SimpleStringProperty(formatearSaldo(data.getValue())));
        colCompras.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(
                        data.getValue().getListCompras() == null ? 0 : data.getValue().getListCompras().size()
                )));
        colEstadoTarjeta.setCellValueFactory(data ->
                new SimpleStringProperty(estadoTarjeta(data.getValue())));
    }


    private void filtrarClientes(String texto) {
        String busqueda = texto == null ? "" : texto.trim().toLowerCase(Locale.ROOT);
        clientesFiltrados.setPredicate(cliente -> busqueda.isEmpty() ||
                contiene(cliente.getId(), busqueda) ||
                contiene(cliente.getNombre(), busqueda) ||
                contiene(cliente.getEmail(), busqueda) ||
                contiene(cliente.getTelefono(), busqueda));
    }

    private boolean contiene(String valor, String busqueda) {
        return valor != null && valor.toLowerCase(Locale.ROOT).contains(busqueda);
    }

    private void cargarClienteSeleccionado(Cliente cliente) {
        clienteSeleccionado = cliente;
        boolean seleccionado = cliente != null;
        btnActualizar.setDisable(!seleccionado);
        btnEliminar.setDisable(!seleccionado);
        btnCrear.setDisable(seleccionado);
        txtId.setDisable(seleccionado);
        txtContrasenia.setDisable(seleccionado);

        if (!seleccionado) {
            actualizarResumen(null);
            return;
        }

        txtId.setText(cliente.getId());
        txtNombre.setText(cliente.getNombre());
        txtTelefono.setText(cliente.getTelefono());
        txtCorreo.setText(cliente.getEmail());
        dpFechaNacimiento.setValue(cliente.getFechaNacimiento());
        txtContrasenia.clear();
        mostrarMensaje("Cliente seleccionado. Puedes actualizar sus datos de contacto.", true);
        actualizarResumen(cliente);
    }

    @FXML
    private void crearCliente() {
        ClientesAdminController.ResultadoOperacion resultado = controller.crearCliente(
                txtId.getText(),
                txtNombre.getText(),
                txtTelefono.getText(),
                txtCorreo.getText(),
                txtContrasenia.getText(),
                dpFechaNacimiento.getValue()
        );
        finalizarOperacion(resultado);
    }

    @FXML
    private void actualizarCliente() {
        if (clienteSeleccionado == null) {
            mostrarMensaje("Selecciona un cliente de la tabla.", false);
            return;
        }
        ClientesAdminController.ResultadoOperacion resultado = controller.actualizarCliente(
                clienteSeleccionado,
                txtNombre.getText(),
                txtTelefono.getText(),
                txtCorreo.getText(),
                dpFechaNacimiento.getValue()
        );
        finalizarOperacion(resultado);
    }

    @FXML
    private void eliminarCliente() {
        if (clienteSeleccionado == null) {
            mostrarMensaje("Selecciona un cliente de la tabla.", false);
            return;
        }
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Eliminar cliente");
        alerta.setHeaderText(null);
        alerta.setContentText("¿Deseas eliminar la cuenta de " + clienteSeleccionado.getNombre() + "?");
        Optional<ButtonType> respuesta = alerta.showAndWait();
        if (respuesta.isEmpty() || respuesta.get() != ButtonType.OK) {
            return;
        }
        finalizarOperacion(controller.eliminarCliente(clienteSeleccionado));
    }

    @FXML
    private void limpiarFormulario() {
        tablaClientes.getSelectionModel().clearSelection();
        clienteSeleccionado = null;
        txtId.clear();
        txtNombre.clear();
        txtTelefono.clear();
        txtCorreo.clear();
        txtContrasenia.clear();
        dpFechaNacimiento.setValue(null);
        txtId.setDisable(false);
        txtContrasenia.setDisable(false);
        btnCrear.setDisable(false);
        btnActualizar.setDisable(true);
        btnEliminar.setDisable(true);
        actualizarResumen(null);
        mostrarMensaje("Registra un cliente o selecciona uno de la tabla para editarlo.", true);
    }

    private void finalizarOperacion(ClientesAdminController.ResultadoOperacion resultado) {
        mostrarMensaje(resultado.mensaje(), resultado.exitoso());
        if (resultado.exitoso()) {
            limpiarFormulario();
        }
    }

    private void actualizarResumen(Cliente cliente) {
        if (cliente == null) {
            lblPuntos.setText("-");
            lblSaldo.setText("-");
            lblCompras.setText("-");
            lblEstadoTarjeta.setText("-");
            return;
        }
        TarjetaVirtual tarjeta = cliente.getTheTarjeta();
        lblPuntos.setText(cliente.getPuntos() + " pts");
        lblSaldo.setText(tarjeta == null ? DINERO.format(0) : DINERO.format(tarjeta.getSaldo()));
        lblCompras.setText(String.valueOf(
                cliente.getListCompras() == null ? 0 : cliente.getListCompras().size()
        ));
        lblEstadoTarjeta.setText(estadoTarjeta(cliente));
    }

    private String formatearSaldo(Cliente cliente) {
        TarjetaVirtual tarjeta = cliente.getTheTarjeta();
        return tarjeta == null ? DINERO.format(0) : DINERO.format(tarjeta.getSaldo());
    }

    private String estadoTarjeta(Cliente cliente) {
        TarjetaVirtual tarjeta = cliente.getTheTarjeta();
        if (tarjeta == null) {
            return "Sin tarjeta";
        }
        return tarjeta.isActiva() ? "Activa" : "Inactiva";
    }

    private String formatearFecha(LocalDate fecha) {
        return fecha == null ? "-" : fecha.format(FECHA);
    }

    private String valorTexto(String texto) {
        return texto == null || texto.isBlank() ? "-" : texto;
    }

    private void mostrarMensaje(String mensaje, boolean exitoso) {
        lblMensaje.setText(mensaje);
        lblMensaje.getStyleClass().removeAll("mensaje-exito", "mensaje-error");
        lblMensaje.getStyleClass().add(exitoso ? "mensaje-exito" : "mensaje-error");
    }
}
