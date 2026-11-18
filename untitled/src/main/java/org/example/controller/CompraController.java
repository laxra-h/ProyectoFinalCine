package org.example.controller;

import org.example.model.Cine;
import org.example.model.consumibleComposite.Consumible;
import org.example.model.funcionDecorator.Funcion;
import org.example.model.pelicula.Pelicula;
import org.example.services.CompraServices;
import org.example.services.SesionActual;
import org.example.model.Ticket;
import org.example.model.compra.Compra;
import org.example.model.usuarios.Cliente;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CompraController {

    private final Cine cine = Cine.getInstance();
    private final CarteleraController carteleraController = new CarteleraController();
    private final CompraServices compraServices = new CompraServices();

    public List<Pelicula> obtenerPeliculas() {
        return carteleraController.obtenerPeliculas();
    }

    public List<Funcion> obtenerFuncionesDisponibles(Pelicula pelicula) {
        return carteleraController.obtenerFuncionesDisponibles(pelicula);
    }

    public String obtenerFormato(Funcion funcion) {
        return carteleraController.obtenerFormato(funcion);
    }

    public List<Consumible> obtenerConfiteria() {

        List<Consumible> consumibles = new ArrayList<>();

        consumibles.addAll(cine.getListCombos());
        consumibles.addAll(cine.getListProductos());

        return consumibles;
    }

    public int obtenerPuntosCliente() {

        if (SesionActual.getInstance().getClienteActual() == null) {
            return 0;
        }
        return SesionActual.getInstance().getClienteActual().getPuntos();
    }

    public double calcularSubtotalEntradas(Funcion funcion, int cantidad) {
        if (funcion == null) {
            return 0;
        }

        return funcion.calcularCosto() * cantidad;
    }

    public double calcularSubtotalConfiteria(Map<Consumible, Integer> seleccionados) {

        double subtotal = 0;

        for (Map.Entry<Consumible, Integer> entrada : seleccionados.entrySet()) {

            subtotal += entrada.getKey().calcularCosto() * entrada.getValue();
        }

        return subtotal;
    }

    public int calcularPuntosConfiteria(Map<Consumible, Integer> seleccionados) {

        int puntos = 0;

        for (Map.Entry<Consumible, Integer> entrada : seleccionados.entrySet()) {

            puntos += entrada.getKey().calcularPuntos() * entrada.getValue();
        }

        return puntos;
    }

    public int calcularPuntosGanados(Funcion funcion, int cantidadEntradas,
                                     Map<Consumible, Integer> seleccionados) {

        int puntosEntradas = 0;

        if (funcion != null) {
            puntosEntradas = funcion.getPuntosRedmidos() * cantidadEntradas;
        }

        return puntosEntradas + calcularPuntosConfiteria(seleccionados);
    }

    public int calcularPuntosUsados(boolean usaPuntos, double subtotal) {

        if (!usaPuntos) {
            return 0;
        }

        int puntosCliente = obtenerPuntosCliente();
        int maximoPuntos = (int) subtotal / compraServices.valorCambio;

        return Math.min(puntosCliente, maximoPuntos);
    }

    public double calcularDescuentoPuntos(int puntosUsados) {
        return puntosUsados * compraServices.valorCambio;
    }

    public String confirmarCompra(
            Funcion funcion,
            List<String> asientos,
            Map<Consumible, Integer> consumibles,
            boolean usaPuntos
    ) {

        Cliente cliente = SesionActual.getInstance().getClienteActual();

        if (cliente == null || funcion == null || asientos == null || asientos.isEmpty()) {
            return "Datos incompletos para realizar la compra";
        }

        String codigo = "C" + String.format("%03d", cine.getListCompras().size() + 1);

        Compra compra = new Compra(codigo, LocalDate.now(), 0, 0);

        for (int i = 0; i < asientos.size(); i++) {

            Ticket ticket = new Ticket(
                    codigo + "-T" + (i + 1),
                    asientos.get(i)
            );

            ticket.setTheFuncion(funcion);
            compra.addTicket(ticket);
        }

        for (Map.Entry<Consumible, Integer> entrada : consumibles.entrySet()) {

            for (int i = 0; i < entrada.getValue(); i++) {
                compra.addConsumible(entrada.getKey());
            }
        }

        compra.asignarCliente(cliente);
        compra.setUsaPuntos(usaPuntos);
        compra.setTotal(compra.calcularTotal());
        compra.setPuntosCompra(compra.calcularPuntosCompra());

        return compraServices.efectuarCompra(compra);
    }
}