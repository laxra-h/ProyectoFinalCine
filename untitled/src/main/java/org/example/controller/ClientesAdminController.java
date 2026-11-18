package org.example.controller;

import org.example.model.Cine;
import org.example.model.compra.Compra;
import org.example.model.usuarios.Cliente;
import org.example.model.tarjeta.TarjetaVirtual;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ClientesAdminController {

    private final Cine cine = Cine.getInstance();

    public record ResultadoOperacion(boolean exitoso, String mensaje) {
    }

    private static final Pattern CORREO_VALIDO =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public List<Cliente> obtenerClientes() {
        return new ArrayList<>(cine.getListClientes());
    }

    public ResultadoOperacion crearCliente(
            String id,
            String nombre,
            String telefono,
            String email,
            String contrasenia,
            LocalDate fechaNacimiento
    ) {
        ResultadoOperacion validacion = validarDatos(
                id, nombre, telefono, email, fechaNacimiento
        );
        if (!validacion.exitoso()) {
            return validacion;
        }
        if (contrasenia == null || contrasenia.isBlank()) {
            return new ResultadoOperacion(false, "La contraseña es obligatoria para crear la cuenta.");
        }
        if (cine.obtenerCliente(id.trim()) != null) {
            return new ResultadoOperacion(false, "Ya existe un cliente con esa identificación.");
        }
        if (correoRegistrado(email.trim(), null)) {
            return new ResultadoOperacion(false, "Ya existe un cliente registrado con ese correo.");
        }

        Cliente cliente = new Cliente.Builder(
                id.trim(), nombre.trim(), contrasenia, 0
        )
                .telefono(telefono.trim())
                .email(email.trim())
                .fechaNacimiento(fechaNacimiento)
                .build();

        if (!cine.crearCliente(cliente)) {
            return new ResultadoOperacion(false, "No fue posible registrar el cliente.");
        }
        return new ResultadoOperacion(true, "Cliente registrado correctamente.");
    }

    public ResultadoOperacion actualizarCliente(
            Cliente cliente,
            String nombre,
            String telefono,
            String email,
            LocalDate fechaNacimiento
    ) {
        if (cliente == null || cine.obtenerCliente(cliente.getId()) != cliente) {
            return new ResultadoOperacion(false, "Selecciona un cliente registrado.");
        }

        ResultadoOperacion validacion = validarDatos(
                cliente.getId(), nombre, telefono, email, fechaNacimiento
        );
        if (!validacion.exitoso()) {
            return validacion;
        }
        if (correoRegistrado(email.trim(), cliente)) {
            return new ResultadoOperacion(false, "Otro cliente ya utiliza ese correo electrónico.");
        }

        boolean actualizado = cine.actualizarCliente(
                cliente.getId(),
                nombre.trim(),
                telefono.trim(),
                email.trim(),
                cliente.getContrasenia(),
                fechaNacimiento
        );
        if (!actualizado) {
            return new ResultadoOperacion(false, "No fue posible actualizar los datos del cliente.");
        }
        return new ResultadoOperacion(true, "Datos del cliente actualizados correctamente.");
    }

    public ResultadoOperacion eliminarCliente(Cliente cliente) {
        if (cliente == null || cine.obtenerCliente(cliente.getId()) != cliente) {
            return new ResultadoOperacion(false, "Selecciona un cliente registrado.");
        }

        if (tieneCompras(cliente)) {
            return new ResultadoOperacion(
                    false,
                    "No se puede eliminar este cliente porque tiene compras asociadas. " +
                            "Conserva su registro para mantener el historial del cine."
            );
        }

        TarjetaVirtual tarjeta = cliente.getTheTarjeta();
        if (tarjeta != null && (tarjeta.getSaldo() > 0 || tarjeta.isRecargaSolicitada() ||
                !tarjeta.getHistorialMovimientos().isEmpty())) {
            return new ResultadoOperacion(
                    false,
                    "No se puede eliminar este cliente porque su tarjeta tiene saldo, " +
                            "movimientos o una recarga pendiente."
            );
        }

        if (!cine.eliminarCliente(cliente.getId())) {
            return new ResultadoOperacion(false, "No fue posible eliminar el cliente.");
        }
        return new ResultadoOperacion(true, "Cliente eliminado correctamente.");
    }

    private ResultadoOperacion validarDatos(
            String id,
            String nombre,
            String telefono,
            String email,
            LocalDate fechaNacimiento
    ) {
        if (id == null || id.isBlank() || nombre == null || nombre.isBlank() ||
                telefono == null || telefono.isBlank() || email == null || email.isBlank() ||
                fechaNacimiento == null) {
            return new ResultadoOperacion(false, "Completa todos los campos del cliente.");
        }
        if (!telefono.trim().matches("\\d{10}")) {
            return new ResultadoOperacion(false, "El teléfono debe contener exactamente 10 dígitos.");
        }
        if (!CORREO_VALIDO.matcher(email.trim()).matches()) {
            return new ResultadoOperacion(false, "Ingresa un correo electrónico válido.");
        }
        if (fechaNacimiento.isAfter(LocalDate.now())) {
            return new ResultadoOperacion(false, "La fecha de nacimiento no puede ser futura.");
        }
        return new ResultadoOperacion(true, "Datos válidos.");
    }

    private boolean correoRegistrado(String email, Cliente excluido) {
        for (Cliente existente : cine.getListClientes()) {
            if (existente != excluido && existente.getEmail() != null &&
                    existente.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }

    private boolean tieneCompras(Cliente cliente) {
        if (cliente.getListCompras() != null && !cliente.getListCompras().isEmpty()) {
            return true;
        }
        for (Compra compra : cine.getListCompras()) {
            if (compra.getTheCliente() == cliente) {
                return true;
            }
        }
        return false;
    }
}
