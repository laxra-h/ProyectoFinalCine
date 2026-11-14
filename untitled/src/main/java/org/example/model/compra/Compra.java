package org.example.model.compra;

import org.example.model.Ticket;
import org.example.model.compra.estrategiaReembolso.EstrategiaReembolso;
import org.example.model.compra.estrategiaReembolso.ReembolsoCompleto;
import org.example.model.compra.estrategiaReembolso.ReembolsoParcial;
import org.example.model.compra.estrategiaReembolso.SinReembolso;
import org.example.model.consumibleComposite.Consumible;
import org.example.model.usuarios.Cliente;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Compra {

    private String codigo;
    private LocalDate fechaCompra;
    private double total;
    private int puntosCompra;
    private boolean usaPuntos;
    private Cliente theCliente;
    private Validador theValidador;
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
    }


    /**
     * Metodo para definir la estrategia de Reembolso
     * @param compra
     * @return
     */

    public void definirEstrategiaReembolso(Compra compra){

        LocalDateTime fechaCancelacion = LocalDateTime.now();

        LocalDateTime fechaFuncion = compra.getListaTickets().get(0).getTheFuncion().getFecha();

        int horasFaltantes = (int) Duration.between(fechaCancelacion, fechaFuncion).toHours();

        if(horasFaltantes <= 24){
            theEstrategiaReembolso = new ReembolsoCompleto();
        }else if( horasFaltantes < 24 && horasFaltantes > 2){
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
       return theEstrategiaReembolso.calcularReembolso(total);
    }

    /**
     * Metodo para calcular el total de la compra
     * @return
     */
    public double calcularTotal(){
        double total = 0;
        for(Ticket ticket : listaTickets){
            total += ticket.getPrecio();
        }
        for (Consumible consumible : listConsumibles) {
            total += consumible.calcularCosto();
        }
        return total;
        }

    /**
     * Metodo para asignar el cliente a la compra
     * @param cliente
     */
    public void asignarCliente (Cliente cliente){
        this.theCliente = cliente;
    }

    /**
     * Metodo para agregar un ticket a la compra
     * @param ticket
     */
    public void addTicket(Ticket ticket){
        listaTickets.add(ticket);
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

    public Validador getTheValidador() {
        return theValidador;
    }

    public void setTheValidador(Validador theValidador) {
        this.theValidador = theValidador;
    }

    public DetalleCompra getTheDetalleCompra() {
        return theDetalleCompra;
    }

    public void setTheDetalleCompra(DetalleCompra theDetalleCompra) {
        this.theDetalleCompra = theDetalleCompra;
    }

    public Reembolso getTheReembolso() {
        return theReembolso;
    }

    public void setTheReembolso(Reembolso theReembolso) {
        this.theReembolso = theReembolso;
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
