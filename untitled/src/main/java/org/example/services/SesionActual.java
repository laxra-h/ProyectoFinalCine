package org.example.services;

import org.example.model.usuarios.Usuario;
import org.example.model.usuarios.Cliente;
import org.example.model.usuarios.Administrador;

public class SesionActual {

    private static SesionActual instancia;

    private Usuario usuarioActual;

    private SesionActual() {
    }

    public static SesionActual getInstance() {
        if (instancia == null) {
            instancia = new SesionActual();
        }
        return instancia;
    }

    public void iniciarSesion(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public Cliente getClienteActual() {
        if (usuarioActual instanceof Cliente) {
            return (Cliente) usuarioActual;
        }
        return null;
    }

    public Administrador getAdministradorActual() {
        if (usuarioActual instanceof Administrador) {
            return (Administrador) usuarioActual;
        }
        return null;
    }

    public boolean haySesionActiva() {
        return usuarioActual != null;
    }

    public void cerrarSesion() {
        usuarioActual = null;
    }
}