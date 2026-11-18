package org.example.services;

import org.example.exceptions.AsientoNoDisponibleException;
import org.example.exceptions.SaldoInsuficienteException;
import org.example.exceptions.TarjetaInactivaException;
import org.example.model.Cine;
import org.example.model.consumibleComposite.Ticket;
import org.example.model.compra.Compra;
import org.example.model.funcionDecorator.Funcion;
import org.example.model.salaPrototype.Asiento;
import org.example.model.salaPrototype.Sala;
import org.example.model.usuarios.Cliente;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CompraServices {
    Cine cine = Cine.getInstance();
    public final int valorCambio = 200;

    /**
     * Metodo para ver cual es la mayor cantidad de puntos que se pueden descontar
     *
     * @param compra
     * @return
     */
    public int maximoPuntosDescontar(Compra compra) {
        return (int) compra.calcularTotal() / valorCambio;
    }

    /**
     * Metodo para hacer la conversion de puntos  a dinero real
     *
     * @param compra
     * @return
     */
    public int cambiarPuntos(Compra compra) {
        if (!compra.isUsaPuntos()) {
            return 0;
        }
        int puntosCliente = compra.getTheCliente().getPuntos();
        int puntosAUsar = Math.min(puntosCliente, maximoPuntosDescontar(compra));
        return puntosAUsar * valorCambio;
    }

    /**
     * Metodo para aplicar el descuento de los puntos
     *
     * @param compra
     * @return
     */
    public double descontarPuntosCompra(Compra compra) {
        return compra.calcularTotal() - cambiarPuntos(compra);
    }

    /**
     * Metodo para verificar el saldo al hacer la compra
     *
     * @param compra
     * @return
     */
    public boolean verificarSaldo(Compra compra) {
        boolean bandera = false;
        if (descontarPuntosCompra(compra) <= compra.getTheCliente().getTheTarjeta().getSaldo()) {
            bandera = true;
        }
        return bandera;
    }

    /**
     * Metodo para efectuar la compra
     * @param compra
     * @return
     */
    public String efectuarCompra(Compra compra) {
        try {
            procesarCompra(compra);
            return "!Compra exitosa!";
        } catch (SaldoInsuficienteException | TarjetaInactivaException | AsientoNoDisponibleException e) {
            return e.getMessage();
        }
    }

    /**
     * Metodo para validar y procesar el pago de la compra
     * @param compra
     * @throws TarjetaInactivaException
     * @throws AsientoNoDisponibleException
     * @throws SaldoInsuficienteException
     */
    public void procesarCompra(Compra compra)
            throws TarjetaInactivaException, AsientoNoDisponibleException, SaldoInsuficienteException {

        Cliente cliente = compra.getTheCliente();
        if (cliente == null || cliente.getTheTarjeta() == null || !cliente.getTheTarjeta().isActiva()) {
            throw new TarjetaInactivaException("La tarjeta virtual no está activa, no es posible realizar la compra.");
        }

        validarAsientos(compra);

        int puntosUsados = cambiarPuntos(compra) / valorCambio;
        double valorPagar = descontarPuntosCompra(compra);

        if (!verificarSaldo(compra)) {
            throw new SaldoInsuficienteException("Saldo insuficiente. Valor a pagar: $" + valorPagar
                    + " - Saldo disponible: $" + cliente.getTheTarjeta().getSaldo());
        }

        cambiarDisponibilidadAsiento(compra);

        if (valorPagar > 0 && !cliente.getTheTarjeta().pagar(valorPagar, "Compra " + compra.getCodigo())) {
            liberarAsientos(compra);
            throw new SaldoInsuficienteException("No fue posible descontar $" + valorPagar + " de la tarjeta.");
        }

        compra.registrarPago(valorPagar, puntosUsados);
        compra.asignarCliente(cliente);
        compra.setPuntosCompra(compra.calcularPuntosCompra());
        compra.pagar();
        cliente.getListCompras().add(compra);
        cine.getListCompras().add(compra);
        if (puntosUsados > 0) {
            cliente.redimirPuntos(puntosUsados);
        }
        LocalDate fecha = (compra.getFechaCompra() != null) ? compra.getFechaCompra() : LocalDate.now();
        cliente.agregarPuntos(compra.getPuntosCompra(), fecha, compra.getCodigo());
        cliente.actualizar("Se ha realizado una compra por el valor de: " + compra.calcularTotal());
    }

    /**
     * Metodo para validar la disponibilidad de los asientos de la compra
     * @param compra
     * @throws AsientoNoDisponibleException
     */
    public void validarAsientos(Compra compra) throws AsientoNoDisponibleException {
        Set<String> vistos = new HashSet<>();
        for (Ticket ticket : compra.getListaTickets()) {
            Funcion funcion = ticket.getTheFuncion();
            String codigo = ticket.getCodigoAsiento();
            if (funcion == null) {
                throw new AsientoNoDisponibleException("El ticket " + ticket.getCodigo() + " no tiene función asignada.");
            }
            if (!vistos.add(funcion.getCodigo() + "|" + codigo)) {
                throw new AsientoNoDisponibleException("El asiento " + codigo + " está repetido en la compra.");
            }
            if (!funcion.asientoDisponible(codigo)) {
                throw new AsientoNoDisponibleException("El asiento " + codigo + " no está disponible para la función "
                        + funcion.getCodigo() + ".");
            }
        }
    }

    /**
     * Metodo para cambiar la disponibilidad de un asiento
     * @param compra
     * @throws AsientoNoDisponibleException
     */
    public void cambiarDisponibilidadAsiento(Compra compra) throws AsientoNoDisponibleException {
        List<Ticket> ocupados = new ArrayList<>();
        try {
            for (Ticket ticket : compra.getListaTickets()) {
                ticket.getTheFuncion().ocuparAsiento(ticket.getCodigoAsiento());
                ocupados.add(ticket);
            }
        } catch (AsientoNoDisponibleException e) {
            for (Ticket ticket : ocupados) {
                ticket.getTheFuncion().liberarAsiento(ticket.getCodigoAsiento());
            }
            throw e;
        }
    }

    /**
     * Metodo para liberar los asientos de una compra
     * @param compra
     */
    private void liberarAsientos(Compra compra) {
        for (Ticket ticket : compra.getListaTickets()) {
            if (ticket.getTheFuncion() != null) {
                ticket.getTheFuncion().liberarAsiento(ticket.getCodigoAsiento());
            }
        }
    }

    /**
     * Metodo para realizar el reembolso en la tarjeta vitual del cliente
     * @param cliente
     * @param codigoCompra
     */
    public void realizarReembolso(Cliente cliente, String codigoCompra) {

        Compra compra = null;
        for (Compra c : cine.getListCompras()) {
            if (c.getCodigo().equals(codigoCompra)) {
                compra = c;
            }
        }
        compra.definirEstrategiaReembolso();
        double devolucion = compra.getTheEstrategiaReembolso().calcularReembolso(compra.getTotal());
        cliente.getTheTarjeta().reembolsar(devolucion, "Reembolso por cancelación de función - Compra " + compra.getCodigo());

    }



    public Asiento buscarAsiento(String codigoBuscar, Sala sala) {
        for (Asiento a : sala.getListAsientos()) {
             if (a.getCodigo().equals(codigoBuscar)) {
                 return a;
             }

        }
         return null;
    }


}
