package org.example.model.usuarios;

import org.example.model.compra.Compra;
import org.example.model.observer.Observador;
import org.example.model.tarjeta.TarjetaVirtual;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class Cliente extends Usuario implements Observador {

    private LocalDate fechaNacimiento;
    private int puntos;
    private List<RegistroPuntos> historialPuntos;
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
        this.historialPuntos = new ArrayList<>();
        if (builder.puntos > 0) {
            this.historialPuntos.add(new RegistroPuntos(builder.puntos, LocalDate.now(), "INICIAL"));
        }
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

    /**
     * Metodo para obtener los puntos vigentes disponibles del cliente
     * @return
     */
    public int getPuntos() {
        return getPuntos(LocalDate.now());
    }

    /**
     * Metodo para obtener los puntos vigentes disponibles en una fecha dada
     * @param fecha
     * @return
     */
    public int getPuntos(LocalDate fecha) {
        int total = 0;
        for (RegistroPuntos registro : historialPuntos) {
            if (registro.estaVigente(fecha)) {
                total += registro.getPuntosDisponibles();
            }
        }
        this.puntos = total;
        return total;
    }

    /**
     * Metodo para registrar puntos acumulados con su fecha de obtencion y vigencia de un ano
     * @param cantidad
     * @param fechaObtencion
     * @param codigoCompra
     */
    public void agregarPuntos(int cantidad, LocalDate fechaObtencion, String codigoCompra) {
        if (cantidad <= 0) {
            return;
        }
        historialPuntos.add(new RegistroPuntos(cantidad, fechaObtencion, codigoCompra));
        this.puntos = getPuntos();
    }

    /**
     * Metodo para redimir puntos usando primero los mas proximos a vencer e impidiendo usar vencidos
     * @param cantidad
     * @return
     */
    public boolean redimirPuntos(int cantidad) {
        return redimirPuntos(cantidad, LocalDate.now());
    }

    /**
     * Metodo para redimir puntos en una fecha dada usando primero los mas proximos a vencer
     * @param cantidad
     * @param fecha
     * @return
     */
    public boolean redimirPuntos(int cantidad, LocalDate fecha) {
        if (cantidad <= 0) {
            return true;
        }
        if (getPuntos(fecha) < cantidad) {
            return false;
        }

        List<RegistroPuntos> vigentes = new ArrayList<>();
        for (RegistroPuntos registro : historialPuntos) {
            if (registro.estaVigente(fecha) && registro.getPuntosDisponibles() > 0) {
                vigentes.add(registro);
            }
        }
        vigentes.sort(Comparator.comparing(RegistroPuntos::getFechaVencimiento));

        int restante = cantidad;
        for (RegistroPuntos registro : vigentes) {
            if (restante <= 0) {
                break;
            }
            restante -= registro.consumir(restante);
        }
        this.puntos = getPuntos(fecha);
        return true;
    }

    /**
     * Metodo para revertir los puntos de una compra cancelada
     * @param puntosUsados
     * @param puntosGanados
     * @param codigoCompra
     */
    public void reembolsarPuntosCompra(int puntosUsados, int puntosGanados, String codigoCompra) {
        if (codigoCompra != null) {
            historialPuntos.removeIf(r -> codigoCompra.equals(r.getCodigoCompra()));
        } else if (puntosGanados > 0) {
            redimirPuntos(Math.min(puntosGanados, getPuntos()));
        }
        if (puntosUsados > 0) {
            historialPuntos.add(new RegistroPuntos(puntosUsados, LocalDate.now(), "REEMBOLSO-" + codigoCompra));
        }
        this.puntos = getPuntos();
    }

    public void setPuntos(int puntos) {
        this.historialPuntos.clear();
        if (puntos > 0) {
            this.historialPuntos.add(new RegistroPuntos(puntos, LocalDate.now(), "AJUSTE"));
        }
        this.puntos = Math.max(0, puntos);
    }

    public List<RegistroPuntos> getHistorialPuntos() {
        return historialPuntos;
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
