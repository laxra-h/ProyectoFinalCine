package org.example.viewController;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.controller.PerfilController;
import org.example.model.usuarios.Cliente;

public class PerfilViewController {

    @FXML
    private TextField txtIdentificacion;

    @FXML
    private TextField txtNombreCompleto;

    @FXML
    private TextField txtTelefono;

    @FXML
    private TextField txtCorreo;

    @FXML
    private DatePicker dpFechaNacimiento;

    @FXML
    private PasswordField txtContrasenaActual;

    @FXML
    private PasswordField txtNuevaContrasena;

    @FXML
    private PasswordField txtConfirmarContrasena;

    private final PerfilController controller = new PerfilController();

    private Cliente cliente;

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
        cargarDatos();
    }

    private void cargarDatos() {

        if (cliente == null) {
            return;
        }

        txtIdentificacion.setText(cliente.getId());
        txtNombreCompleto.setText(cliente.getNombre());
        txtTelefono.setText(cliente.getTelefono());
        txtCorreo.setText(cliente.getEmail());
        dpFechaNacimiento.setValue(cliente.getFechaNacimiento());
    }

    @FXML
    private void guardarDatos() {

        if (cliente == null) {
            mostrarMensaje(Alert.AlertType.ERROR, "Error", "No se encontró el cliente.");
            return;
        }

        String nombre = txtNombreCompleto.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String correo = txtCorreo.getText().trim();

        if (nombre.isEmpty() || telefono.isEmpty() || correo.isEmpty()
                || dpFechaNacimiento.getValue() == null) {
            mostrarMensaje(Alert.AlertType.WARNING, "Campos incompletos",
                    "Debes completar todos los campos.");
            return;
        }

        if (!correo.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            mostrarMensaje(Alert.AlertType.WARNING, "Correo inválido",
                    "Ingresa una dirección de correo válida.");
            return;
        }

        boolean actualizado = controller.actualizarDatos(
                cliente,
                nombre,
                telefono,
                correo,
                dpFechaNacimiento.getValue()
        );

        if (actualizado) {
            cargarDatos();
            mostrarMensaje(Alert.AlertType.INFORMATION, "Perfil actualizado",
                    "Tus datos personales se actualizaron correctamente.");
        } else {
            mostrarMensaje(Alert.AlertType.ERROR, "Error",
                    "No fue posible actualizar tus datos.");
        }
    }

    @FXML
    private void actualizarContrasena() {

        if (cliente == null) {
            mostrarMensaje(Alert.AlertType.ERROR, "Error", "No se encontró el cliente.");
            return;
        }

        String actual = txtContrasenaActual.getText();
        String nueva = txtNuevaContrasena.getText();
        String confirmacion = txtConfirmarContrasena.getText();

        if (actual.isBlank() || nueva.isBlank() || confirmacion.isBlank()) {
            mostrarMensaje(Alert.AlertType.WARNING, "Campos incompletos",
                    "Debes completar todos los campos de contraseña.");
            return;
        }

        if (!nueva.equals(confirmacion)) {
            mostrarMensaje(Alert.AlertType.WARNING, "Contraseñas diferentes",
                    "La nueva contraseña y su confirmación no coinciden.");
            return;
        }

        if (actual.equals(nueva)) {
            mostrarMensaje(Alert.AlertType.WARNING, "Contraseña sin cambios",
                    "La nueva contraseña debe ser diferente a la actual.");
            return;
        }

        boolean actualizado = controller.actualizarContrasena(
                cliente,
                actual,
                nueva,
                confirmacion
        );

        if (actualizado) {
            txtContrasenaActual.clear();
            txtNuevaContrasena.clear();
            txtConfirmarContrasena.clear();

            mostrarMensaje(Alert.AlertType.INFORMATION, "Contraseña actualizada",
                    "Tu contraseña se actualizó correctamente.");
        } else {
            mostrarMensaje(Alert.AlertType.ERROR, "Error",
                    "No se pudo actualizar la contraseña. Verifica tu contraseña actual.");
        }
    }

    private void mostrarMensaje(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}