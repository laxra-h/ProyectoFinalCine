package org.example.model.usuarios;

public abstract class Usuario {

    protected String id;
    protected String nombre;
    protected String telefono;
    protected String email;
    protected String contrasenia;


    /**
     * Metodo constructor del usuario
     * @param id
     * @param nombre
     * @param telefono
     * @param email
     * @param contrasenia
     */
    public Usuario(String id, String nombre, String telefono, String email, String contrasenia) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
        this.contrasenia = contrasenia;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }
}
