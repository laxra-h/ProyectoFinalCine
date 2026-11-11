package org.example.model.usuarios;

import java.time.LocalDate;

public class Cliente extends Usuario {

    private LocalDate fechaNacimiento;
    private int puntos;


    /**
     * Metodo constructor del cliente
     * @param id
     * @param nombre
     * @param telefono
     * @param email
     * @param contrasenia
     * @param fechaNacimiento
     * @param puntos
     */
    public Cliente(String id, String nombre, String telefono, String email, String contrasenia, LocalDate fechaNacimiento, int puntos) {
        super(id, nombre, telefono, email, contrasenia);
        this.fechaNacimiento = fechaNacimiento;
        this.puntos = puntos;
    }



    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }
}
