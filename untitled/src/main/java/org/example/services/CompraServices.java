package org.example.services;

import org.example.model.Cine;
import org.example.model.Ticket;
import org.example.model.compra.Compra;
import org.example.model.compra.estrategiaReembolso.EstrategiaReembolso;
import org.example.model.compra.estrategiaReembolso.ReembolsoCompleto;
import org.example.model.compra.estrategiaReembolso.ReembolsoParcial;
import org.example.model.compra.estrategiaReembolso.SinReembolso;
import org.example.model.consumibleComposite.Consumible;
import org.example.model.salaPrototype.Asiento;
import org.example.model.salaPrototype.Sala;
import org.example.model.usuarios.Cliente;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

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
        int puntosCliente = compra.getTheCliente().getPuntos();
        if (puntosCliente >= maximoPuntosDescontar(compra)) {
            return compra.getTheCliente().getPuntos() * valorCambio;
        }
        return 0;
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
        if (compra.isUsaPuntos()) {
            if (descontarPuntosCompra(compra) <= compra.getTheCliente().getTheTarjeta().getSaldo()) {
                bandera = true;
            } else {
                if (compra.calcularTotal() <= compra.getTheCliente().getTheTarjeta().getSaldo()) {
                    bandera = true;
                }
            }
        }
        return bandera;
    }

    /**
     * Metodo para efectuar la compra
     * @param compra
     * @return
     */
    public String  efectuarCompra(Compra compra) {
        Cliente cliente = compra.getTheCliente();
        if (verificarSaldo(compra)) {
            cliente.setPuntos(cliente.getPuntos() - cambiarPuntos(compra));
            cliente.getTheTarjeta().setSaldo(cliente.getTheTarjeta().getSaldo() - descontarPuntosCompra(compra));
            cliente.setPuntos(cliente.getPuntos() + compra.calcularPuntosCompra());
            cambiarDisponibilidadAsiento(compra);
            compra.asignarCliente(cliente);
            compra.pagar();
            cliente.actualizar("Se ha realizado una compra por el valor de: " + compra.calcularTotal());
            return "!Compra exitosa!";
        }
        return "Lo sentimos, ha ocurrido un error en su compra";
    }

    /**
     * Metodo para cambiar la disponibilidad de un asiento
     * @param compra
     */
    public void cambiarDisponibilidadAsiento(Compra compra) {
        List<Ticket> listTickets = compra.getListaTickets();

        for (Ticket ticket : listTickets) {
            String codigo = ticket.getCodigoAsiento();
            for(Asiento asiento : ticket.getTheFuncion().getSala().getListAsientos()) {
                if (asiento.getCodigo().equals(codigo)){
                    asiento.setDisponible(false);
                }
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
        cliente.getTheTarjeta().setSaldo(cliente.getTheTarjeta().getSaldo() + devolucion);


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
