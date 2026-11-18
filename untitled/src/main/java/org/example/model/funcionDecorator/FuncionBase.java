package org.example.model.funcionDecorator;

import org.example.exceptions.AsientoNoDisponibleException;
import org.example.model.consumibleComposite.Ticket;
import org.example.model.observer.Observador;
import org.example.model.pelicula.Pelicula;
import org.example.model.salaPrototype.Asiento;
import org.example.model.salaPrototype.Sala;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FuncionBase implements Funcion {

    private String codigo;
    private Pelicula pelicula;
    private Sala sala;
    private LocalDateTime fecha;
    private final int DESCUENTO_SEMANA = 40;
    private final double COSTO = 20000;
    public static final int VALOR_PUNTO = 200;
    private int puntosRedimidos;
    private EstadoFuncion estado;
    private Asiento[] asientos;
    private List<Ticket> listaTickets;
    private List<Observador> listObservadores;


    /**
     * Metodo constructor de la funcionBase
     * @param codigo
     * @param fecha
     */
    public FuncionBase(String codigo, LocalDateTime fecha, int puntosRedimidos) {
        this.codigo = codigo;
        this.fecha = fecha;
        this.estado = EstadoFuncion.PROGRAMADA;
        this.puntosRedimidos = puntosRedimidos;
        this.listaTickets = new ArrayList<>();
        this.listObservadores = new ArrayList<>();
    }

    /**
     * Metodo constructor completo de la funcionBase asociada a una pelicula y sala
     * @param codigo
     * @param pelicula
     * @param sala
     * @param fecha
     * @param estado
     */
    public FuncionBase(String codigo, Pelicula pelicula, Sala sala, LocalDateTime fecha, EstadoFuncion estado) {
        this.codigo = codigo;
        this.pelicula = pelicula;
        this.fecha = fecha;
        this.estado = estado;
        this.listaTickets = new ArrayList<>();
        this.listObservadores = new ArrayList<>();
        setSala(sala);
        this.puntosRedimidos = (int) (calcularCosto() / VALOR_PUNTO);
    }



    /**
     * Metodo para calcular el costo de la funcion base si es fin de semana o dia entre semana
     * @return
     */
    @Override
    public double calcularCosto() {
        DayOfWeek dia = (this.fecha != null ? this.fecha : LocalDateTime.now()).getDayOfWeek();
        if (dia == DayOfWeek.SATURDAY || dia == DayOfWeek.SUNDAY) {
            return COSTO;
        } else {
            return calcularCostoSemana();
        }
    }

    /**
     * Metodo que calcula el costo con promocion de semana
     * @return
     */
    public double calcularCostoSemana() {
        return (COSTO * DESCUENTO_SEMANA) / 100;
    }

    /**
     * Metodo para resetear los asientos de la funcion
     */
    public void resetAsientos() {
        if (asientos == null) {
            return;
        }
        for (Asiento asiento : asientos) {
            if (asiento != null) {
                asiento.setDisponible(true);
            }
        }
    }

    /**
     * Metodo para buscar un asiento de la funcion por su codigo
     * @param codigoAsiento
     * @return
     */
    private Asiento buscarAsiento(String codigoAsiento) {
        if (asientos == null || codigoAsiento == null) {
            return null;
        }
        for (Asiento asiento : asientos) {
            if (asiento != null && asiento.getCodigo().equals(codigoAsiento)) {
                return asiento;
            }
        }
        return null;
    }

    @Override
    public Asiento[] getAsientos() {
        return asientos;
    }

    @Override
    public boolean asientoDisponible(String codigoAsiento) {
        Asiento asiento = buscarAsiento(codigoAsiento);
        return asiento != null && asiento.isDisponible();
    }

    @Override
    public void ocuparAsiento(String codigoAsiento) throws AsientoNoDisponibleException {
        Asiento asiento = buscarAsiento(codigoAsiento);
        if (asiento == null) {
            throw new AsientoNoDisponibleException("El asiento " + codigoAsiento + " no existe en la función " + codigo);
        }
        if (!asiento.isDisponible()) {
            throw new AsientoNoDisponibleException("El asiento " + codigoAsiento + " ya fue vendido para la función " + codigo);
        }
        asiento.setDisponible(false);
    }

    @Override
    public void liberarAsiento(String codigoAsiento) {
        Asiento asiento = buscarAsiento(codigoAsiento);
        if (asiento != null) {
            asiento.setDisponible(true);
        }
    }

    /**
     * Metodo para cancelar la funcion y notificar a los observadores
     */
    @Override
    public void cancelar() {
        setEstado(EstadoFuncion.CANCELADA);
    }

    /**
     * Metodo para suscribir un observador a la funcion
     * @param observador
     */
    @Override
    public void agregarObservador(Observador observador) {
        if (observador != null && !listObservadores.contains(observador)) {
            listObservadores.add(observador);
        }
    }

    /**
     * Metodo para eliminar un observador de la funcion
     * @param observador
     */
    @Override
    public void eliminarObservador(Observador observador) {
        listObservadores.remove(observador);
    }

    /**
     * Metodo para notificar a todos los observadores suscritos sobre el cambio de estado
     */
    @Override
    public void notificar() {
        String nombrePelicula = (pelicula != null) ? pelicula.getNombre() : codigo;
        String mensaje = "La función " + codigo + " (" + nombrePelicula + ") cambió su estado a: " + estado;
        notificar(mensaje);
    }

    /**
     * Metodo para enviar un mensaje especifico a todos los observadores suscritos
     * @param mensaje
     */
    @Override
    public void notificar(String mensaje) {
        for (Observador observador : listObservadores) {
            observador.actualizar(mensaje);
        }
    }

    @Override
    public int getPuntosRedmidos() {
        return puntosRedimidos;
    }


    public List<Ticket> getListaTickets() {
        return listaTickets;
    }

    public void setListaTickets(List<Ticket> listaTickets) {
        this.listaTickets = listaTickets;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Pelicula getPelicula() {
        return pelicula;
    }

    public void setPelicula(Pelicula pelicula) {
        this.pelicula = pelicula;
    }

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
        this.asientos = (sala != null && sala.getListAsientos() != null) ? sala.clonarAsientos() : null;
    }


    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public int getDESCUENTO_SEMANA() {
        return DESCUENTO_SEMANA;
    }

    public double getCOSTO() {
        return COSTO;
    }

    public EstadoFuncion getEstado() {
        return estado;
    }

    public void setEstado(EstadoFuncion estado) {
        this.estado = estado;
        if (estado == EstadoFuncion.FINALIZADA) {
            resetAsientos();
        }
        notificar();
    }


    public void setPuntosRedimidos(int puntosRedimidos) {
        this.puntosRedimidos = puntosRedimidos;
    }

    public List<Observador> getListObservadores() {
        return listObservadores;
    }

    public void setListObservadores(List<Observador> listObservadores) {
        this.listObservadores = listObservadores;
    }
}
