package org.example.model.usuarios;

import org.example.model.compra.Compra;
import org.example.model.observer.Observador;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class Cliente extends Usuario implements Observador {

    private LocalDate fechaNacimiento;
    private int puntos;
    private TarjetaVirtual theTarjeta;
    private List<String> listNotificaciones;
    private List<Compra> listCompras;


    /**
     * Metodo para construir el Cliente (constructor)
     * @param builder
     */
    public Cliente(Builder builder) {
        super(builder.id, builder.nombre, builder.telefono, builder.email, builder.contrasenia);
        this.fechaNacimiento = builder.fechaNacimiento;
        this.puntos = builder.puntos;
        this.theTarjeta = new TarjetaVirtual(id, 0);
        this.listNotificaciones = new ArrayList<>();
        this.listCompras = new ArrayList<>();
    }

    /**
     * Metodo del patron Observer para recibir notificaciones de funciones o compras
     * @param mensaje
     */
    @Override
    public void actualizar(String mensaje) {
        this.listNotificaciones.add(mensaje);
    }


    /**
     * Clase Builder
     */
    public static class Builder {

        private final String id;
        private final String nombre;
        private final String contrasenia;

        private String telefono;
        private String email;
        private LocalDate fechaNacimiento;
        private int puntos = 0;

        /**
         * Metodo para los atributos obligatorios
         * @param id
         * @param nombre
         * @param contrasenia
         * @param puntos
         */
        public Builder(String id, String nombre, String contrasenia, int puntos) {
            this.id = id;
            this.nombre = nombre;
            this.contrasenia = contrasenia;
            this.puntos = puntos;
        }

        public Builder telefono(String  telefono) {
            this.telefono = telefono;
            return this;
        }

        public Builder email(String  email) {
            this.email = email;
            return this;
        }

        public Builder fechaNacimiento(LocalDate fechaNacimiento) {
            this.fechaNacimiento = fechaNacimiento;
            return this;
        }


        /**
         * Metodo que construye el cliente
         * @return
         */
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

    public TarjetaVirtual getTheTarjeta() {
        return theTarjeta;
    }

    public void setTheTarjeta(TarjetaVirtual theTarjeta) {
        this.theTarjeta = theTarjeta;
    }

    public List<String> getListNotificaciones() {
        return listNotificaciones;
    }

    public void setListNotificaciones(List<String> listNotificaciones) {
        this.listNotificaciones = listNotificaciones;
    }

    public List<Compra> getListCompras() {
        return listCompras;
    }

    public void setListCompras(List<Compra> listCompras) {
        this.listCompras = listCompras;
    }
}
