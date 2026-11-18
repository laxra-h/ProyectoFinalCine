package org.example.controller;

import org.example.model.Cine;
import org.example.model.usuarios.Cliente;

import java.time.LocalDate;

public class PerfilController {

    private final Cine cine = Cine.getInstance();

    public Cliente obtenerCliente(String identificacion) {
        return cine.obtenerCliente(identificacion);
    }

    public boolean actualizarDatos(Cliente cliente, String nombre, String telefono,
                                   String email, LocalDate fechaNacimiento) {

        if (cliente == null) {
            return false;
        }

        return cine.actualizarCliente(
                cliente.getId(),
                nombre,
                telefono,
                email,
                cliente.getContrasenia(),
                fechaNacimiento
        );
    }

    public boolean actualizarContrasena(Cliente cliente, String actual,
                                        String nueva, String confirmacion) {

        if (cliente == null || actual == null || nueva == null || confirmacion == null) {
            return false;
        }

        if (!cliente.getContrasenia().equals(actual)) {
            return false;
        }

        if (nueva.isBlank() || !nueva.equals(confirmacion)) {
            return false;
        }

        return cine.actualizarCliente(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getTelefono(),
                cliente.getEmail(),
                nueva,
                cliente.getFechaNacimiento()
        );
    }
}