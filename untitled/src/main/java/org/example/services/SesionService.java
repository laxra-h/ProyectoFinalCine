package org.example.services;

import org.example.model.Cine;
import org.example.model.usuarios.Administrador;
import org.example.model.usuarios.Cliente;

public class SesionService {


    Cine cine = Cine.getInstance();

    /**
     * Metodo  para iniciar sesion coomo cliente
     * @param id
     * @param contrasenia
     * @return
     */
    public Cliente iniciarSesionCliente(String id, String contrasenia) {
        for (Cliente cliente : cine.getListClientes()) {
            if (cliente.getId().equals(id) && cliente.getContrasenia().equals(contrasenia)) {
                return cliente;
            }
        }
        return null;
    }

    /**
     * Metodo para iniciar sesion como administrador
     * @param id
     * @param contrasenia
     * @return
     */
    public Administrador iniciarSesionAdministrador(String id, String contrasenia) {
        for (Administrador a : cine.getListAministradores()) {
            if (a.getId().equals(id) && a.getContrasenia().equals(contrasenia)) {
                return a;
            }
        }
        return null;
    }


}
