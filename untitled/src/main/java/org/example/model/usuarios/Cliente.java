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


    private Cliente(Builder builder) {
        super(builder.id, builder.nombre, builder.telefono, builder.email, builder.contrasenia);
        this.fechaNacimiento = builder.fechaNacimiento;
        this.puntos = builder.puntos;
    }

    public static class Builder {
        private String id;
        private String nombre;
        private String telefono = "";
        private String email = "";
        private String contrasenia;
        private LocalDate fechaNacimiento;
        private int puntos = 0;

        public Builder(String id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }

        public Builder telefono(String telefono) {
            this.telefono = telefono;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder contrasenia(String contrasenia) {
            this.contrasenia = contrasenia;
            return this;
        }

        public Builder fechaNacimiento(LocalDate fechaNacimiento) {
            this.fechaNacimiento = fechaNacimiento;
            return this;
        }

        public Builder puntos(int puntos) {
            this.puntos = puntos;
            return this;
        }

        public Cliente build() {
            return new Cliente(this);
        }
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
