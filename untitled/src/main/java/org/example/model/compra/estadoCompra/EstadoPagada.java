package org.example.model.compra.estadoCompra;

import org.example.model.Ticket;
import org.example.model.compra.Compra;
import org.example.model.compra.estrategiaReembolso.ReembolsoCompleto;
import org.example.model.compra.estrategiaReembolso.SinReembolso;
import org.example.model.funcionDecorator.EstadoFuncion;
import org.example.model.salaPrototype.Asiento;
import org.example.model.usuarios.Cliente;
import org.example.model.tarjeta.MovimientoTarjeta;
import org.example.model.tarjeta.TipoMovimiento;

public class EstadoPagada implements EstadoCompra {

    /**
     * Una compra ya pagada no puede volver a pagarse
     * @param compra
     */
    @Override
    public void pagar(Compra compra) {
        // La compra ya se encuentra pagada, no se realiza accion
    }

    /**
     * Cancela una compra pagada aplicando la estrategia de reembolso correspondiente
     * y cambia su estado a reembolsada para impedir dobles reembolsos
     * @param compra
     */
    @Override
    public void cancelar(Compra compra) {
        if (compra.getListaTickets() != null && !compra.getListaTickets().isEmpty()
                && compra.getListaTickets().get(0).getTheFuncion() != null) {

            if (compra.getListaTickets().get(0).getTheFuncion().getEstado() == EstadoFuncion.CANCELADA) {
                compra.setTheEstrategiaReembolso(new ReembolsoCompleto());
            }

            if (compra.getTheEstrategiaReembolso() instanceof SinReembolso) {
                if (compra.getTheCliente() != null) {
                    compra.getTheCliente().actualizar("No es posible cancelar la compra " + compra.getCodigo() + " (faltan menos de 2 horas para la función).");
                }
                return;
            }
        }

        Cliente cliente = compra.getTheCliente();
        double montoPagado = obtenerMontoPagado(compra, cliente);
        double montoReembolso = (compra.getTheEstrategiaReembolso() != null)
                ? compra.getTheEstrategiaReembolso().calcularReembolso(montoPagado)
                : montoPagado;

        if (cliente != null && cliente.getTheTarjeta() != null) {
            cliente.getTheTarjeta().reembolsar(montoReembolso, "Reembolso de la compra " + compra.getCodigo());
        }

        if (cliente != null && montoPagado > 0) {
            double proporcionReembolsada = Math.min(1, montoReembolso / montoPagado);
            int puntosUsados = Math.max(0, (int) Math.round((compra.getTotal() - montoPagado) / 200.0));
            int puntosGanadosARetirar = (int) Math.floor(compra.getPuntosCompra() * proporcionReembolsada);
            int puntosUsadosARestaurar = (int) Math.round(puntosUsados * proporcionReembolsada);

            cliente.setPuntos(
                    Math.max(0, cliente.getPuntos() - puntosGanadosARetirar) + puntosUsadosARestaurar
            );
        }

        if (compra.getListaTickets() != null) {
            for (Ticket ticket : compra.getListaTickets()) {
                if (ticket.getTheFuncion() != null && ticket.getTheFuncion().getSala() != null
                        && ticket.getTheFuncion().getSala().getListAsientos() != null) {
                    for (Asiento asiento : ticket.getTheFuncion().getSala().getListAsientos()) {
                        if (asiento != null && asiento.getCodigo().equals(ticket.getCodigoAsiento())) {
                            asiento.setDisponible(true);
                        }
                    }
                }
            }
        }

        compra.cambiarEstado(new EstadoReembolsada());

        if (cliente != null) {
            cliente.actualizar("Compra " + compra.getCodigo() + " cancelada y reembolsada por $" + montoReembolso);
        }
    }
    /**
     * Metodo que obtiene el monto realmente pagado por una compra
     * @param compra
     * @param cliente
     * @return Monto realmente debitado de la tarjeta o el total de la compra si no se encuentra el movimiento
     */
    private double obtenerMontoPagado(Compra compra, Cliente cliente) {
        if (cliente == null || cliente.getTheTarjeta() == null ||
                cliente.getTheTarjeta().getHistorialMovimientos() == null) {
            return compra.getTotal();
        }

        String descripcion = "Compra " + compra.getCodigo();
        for (MovimientoTarjeta movimiento : cliente.getTheTarjeta().getHistorialMovimientos()) {
            if (movimiento.getTipo() == TipoMovimiento.COMPRA &&
                    descripcion.equals(movimiento.getDescripcion())) {
                return movimiento.getMonto();
            }
        }
        return compra.getTotal();
    }

    @Override
    public String getNombre() {
        return "PAGADA";
    }
}
