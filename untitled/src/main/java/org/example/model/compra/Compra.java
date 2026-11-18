package org.example.model.compra;

import org.example.exceptions.CancelacionNoPermitidaException;
import org.example.model.Ticket;
import org.example.model.compra.estadoCompra.EstadoCompra;
import org.example.model.compra.estadoCompra.EstadoPendiente;
import org.example.model.compra.estrategiaReembolso.EstrategiaReembolso;
import org.example.model.compra.estrategiaReembolso.ReembolsoCompleto;
import org.example.model.compra.estrategiaReembolso.ReembolsoParcial;
import org.example.model.compra.estrategiaReembolso.SinReembolso;
import org.example.model.consumibleComposite.Consumible;
import org.example.model.salaPrototype.Asiento;
import org.example.model.usuarios.Cliente;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Compra {

    private String codigo;
    private LocalDate fechaCompra;
    private double total;
    private int puntosCompra;
    private boolean usaPuntos;
    private int puntosUsados;
    private double montoPagado;
    private boolean pagoRegistrado;
    private Cliente theCliente;
    private EstadoCompra theEstadoCompra;
    private EstrategiaReembolso theEstrategiaReembolso;
    private List<Ticket> listaTickets;
    private List<Consumible> listConsumibles;


    /**
     * Metodo cosntructor de la cosa Compra
     * @param codigo
     * @param fechaCompra
     * @param total
     * @param puntosCompra
     */
    public Compra(String codigo, LocalDate fechaCompra, double total, int puntosCompra) {
        this.codigo = codigo;
        this.fechaCompra = fechaCompra;
        this.total = total;
        this.puntosCompra = puntosCompra;
        this.usaPuntos = false;
        this.theEstadoCompra = new EstadoPendiente();
        this.listaTickets = new ArrayList<>();
        this.listConsumibles = new ArrayList<>();
    }

    /**
     * Metodo para cambiar el estado de la compra (Patron State)
     * @param estadoCompra
     */
    public void cambiarEstado(EstadoCompra estadoCompra) {
        this.theEstadoCompra = estadoCompra;
    }

    /**
     * Metodo para pagar la compra delegando al estado actual (Patron State)
     */
    public void pagar() {
        this.theEstadoCompra.pagar(this);
    }

    /**
     * Metodo para cancelar la compra delegando al estado actual (Patron State)
     * @throws CancelacionNoPermitidaException
     */
    public void cancelar() throws CancelacionNoPermitidaException {
        this.theEstadoCompra.cancelar(this);
    }

    /**
     * Metodo para registrar el monto pagado con tarjeta y los puntos usados
     * @param montoPagado
     * @param puntosUsados
     */
    public void registrarPago(double montoPagado, int puntosUsados) {
        this.montoPagado = montoPagado;
        this.puntosUsados = puntosUsados;
        this.pagoRegistrado = true;
    }

    /**
     * Metodo para obtener el monto pagado con tarjeta
     * @return
     */
    public double getMontoPagado() {
        return pagoRegistrado ? montoPagado : total;
    }

    public int getPuntosUsados() {
        return puntosUsados;
    }


    /**
     * Metodo para definir la estrategia de Reembolso
     * @return
     */

    public void definirEstrategiaReembolso(){
        if (listaTickets == null || listaTickets.isEmpty()
                || listaTickets.get(0).getTheFuncion() == null) {
            theEstrategiaReembolso = new SinReembolso();
            return;
        }

        LocalDateTime fechaCancelacion = LocalDateTime.now();

        LocalDateTime fechaFuncion = listaTickets.get(0).getTheFuncion().getFecha();

        int horasFaltantes = (int) Duration.between(fechaCancelacion, fechaFuncion).toHours();

        if(horasFaltantes > 24){
            theEstrategiaReembolso = new ReembolsoCompleto();
        }else if( horasFaltantes <= 24 && horasFaltantes >= 2){
            theEstrategiaReembolso = new ReembolsoParcial();
        }else {
            theEstrategiaReembolso = new SinReembolso();
        }
    }

    /**
     * Metodo para calcular el total del reembolso
     * @return
     */
    public double calcularTotalReembolso(){
        if (theEstrategiaReembolso == null) {
            definirEstrategiaReembolso();
        }
        return theEstrategiaReembolso.calcularReembolso(getMontoPagado());
    }

    /**
     * Metodo para calcular el total de la compra
     * @return
     */
    public double calcularTotal(){
        double suma = 0;
        for(Ticket ticket : listaTickets){
            suma += ticket.getPrecio();
        }
        for (Consumible consumible : listConsumibles) {
            suma += consumible.calcularCosto();
        }
        this.total = suma;
        return this.total;
    }

    /**
     * Metodo para asignar el cliente a la compra y suscribirlo a las funciones de sus tickets
     * @param cliente
     */
    public void asignarCliente (Cliente cliente){
        this.theCliente = cliente;
        if (cliente != null && listaTickets != null) {
            for (Ticket ticket : listaTickets) {
                if (ticket.getTheFuncion() != null) {
                    ticket.getTheFuncion().agregarObservador(cliente);
                }
            }
        }
    }

    /**
     * Metodo para calcular puntos
     * @return
     */
    public int calcularPuntosCompra(){
        int total = 0;
        for(Ticket ticket : listaTickets){
            total += ticket.calcularPuntos();
        }
        for (Consumible consumible : listConsumibles) {
            total += consumible.calcularPuntos();
        }
        return total;
    }

    /**
     * Metodo para agregar un ticket a la compra y suscribir al cliente como observador de la funcion
     * @param ticket
     */
    public void addTicket(Ticket ticket){
        listaTickets.add(ticket);
        if (theCliente != null && ticket != null && ticket.getTheFuncion() != null) {
            ticket.getTheFuncion().agregarObservador(theCliente);
        }
    }

    /**
     * Metodo para agregar un consumible a la compra
     * @param consumible
     */
    public void addConsumible(Consumible consumible){
        listConsumibles.add(consumible);
    }









    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public LocalDate getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(LocalDate fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public int getPuntosCompra() {
        return puntosCompra;
    }

    public void setPuntosCompra(int puntosCompra) {
        this.puntosCompra = puntosCompra;
    }

    public Cliente getTheCliente() {
        return theCliente;
    }

    public void setTheCliente(Cliente theCliente) {
        this.theCliente = theCliente;
    }

    public EstadoCompra getTheEstadoCompra() {
        return theEstadoCompra;
    }

    public void setTheEstadoCompra(EstadoCompra theEstadoCompra) {
        this.theEstadoCompra = theEstadoCompra;
    }

    public List<Ticket> getListaTickets() {
        return listaTickets;
    }

    public void setListaTickets(List<Ticket> listaTickets) {
        this.listaTickets = listaTickets;
    }

    public boolean isUsaPuntos() {
        return usaPuntos;
    }

    public void setUsaPuntos(boolean usaPuntos) {
        this.usaPuntos = usaPuntos;
    }

    public EstrategiaReembolso getTheEstrategiaReembolso() {
        return theEstrategiaReembolso;
    }

    public void setTheEstrategiaReembolso(EstrategiaReembolso theEstrategiaReembolso) {
        this.theEstrategiaReembolso = theEstrategiaReembolso;
    }


    public List<Consumible> getListConsumibles() {
        return listConsumibles;
    }

    public void setListConsumibles(List<Consumible> listConsumibles) {
        this.listConsumibles = listConsumibles;
    }
}
