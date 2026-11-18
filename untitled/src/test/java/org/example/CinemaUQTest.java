package org.example;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.UUID;

import org.example.controller.ComprasController;
import org.example.controller.PeliculasController;
import org.example.controller.PromocionesAdminController;
import org.example.controller.PuntosController;
import org.example.controller.RecargasAdminController;
import org.example.controller.ReportesAdminController;
import org.example.controller.TarjetaController;
import org.example.exceptions.AsientoNoDisponibleException;
import org.example.exceptions.CancelacionNoPermitidaException;
import org.example.exceptions.SaldoInsuficienteException;
import org.example.model.Cine;
import org.example.model.Ticket;
import org.example.model.compra.Compra;
import org.example.model.consumibleComposite.Combo;
import org.example.model.consumibleComposite.Producto;
import org.example.model.funcionDecorator.EstadoFuncion;
import org.example.model.funcionDecorator.Funcion;
import org.example.model.funcionDecorator.FuncionBase;
import org.example.model.funcionDecorator.TresD;
import org.example.model.pelicula.ClasificacionPelicula;
import org.example.model.pelicula.GeneroPelicula;
import org.example.model.pelicula.Pelicula;
import org.example.model.promocion.TipoPromocion;
import org.example.model.reporteAdapter.TipoReporte;
import org.example.model.salaPrototype.Sala;
import org.example.model.tarjeta.PagoPSE;
import org.example.model.usuarios.Cliente;
import org.example.services.CineFacade;
import org.example.services.CompraServices;
import org.example.services.SesionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Clase para probar los requisitos funcionales del sistema CinemaUQ (RF-001 a RF-008)
 */
public class CinemaUQTest {

    private CompraServices compraServices;
    private CineFacade cineFacade;
    private Sala sala;
    private Pelicula pelicula;
    private Cliente cliente;

    @BeforeEach
    public void setUp() throws CloneNotSupportedException {
        DatosIniciales.inicializar();
        DatosIniciales.inicializarConfiteria();
        DatosIniciales.inicializarClientes();

        compraServices = new CompraServices();
        cineFacade = new CineFacade();

        sala = new Sala("Sala-" + UUID.randomUUID());
        sala.creacionAsientos();
        pelicula = new Pelicula("Interestelar", "Christopher Nolan", "Viaje espacial",
                ClasificacionPelicula.MAYORES_12, GeneroPelicula.CIENCIA_FICCION, 169);
        cliente = new Cliente.Builder("ID-" + UUID.randomUUID(), "Daniel Giraldo", "Clave123", 0)
                .telefono("3104567890")
                .email("daniel@uniquindio.edu.co")
                .fechaNacimiento(LocalDate.of(2004, 5, 10))
                .build();
        cliente.getTheTarjeta().recargar(200000);
    }

    private Compra crearCompra(String codigo, Funcion funcion, String asiento) {
        Compra compra = new Compra(codigo, LocalDate.now(), 0, 0);
        compra.asignarCliente(cliente);
        Ticket ticket = new Ticket("T-" + codigo, asiento);
        ticket.setTheFuncion(funcion);
        compra.addTicket(ticket);
        compra.setTotal(compra.calcularTotal());
        return compra;
    }

    @Test
    public void testRF001ProcesoCompraYSeleccionAsientos() throws Exception {
        LocalDateTime sabado = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.SATURDAY)).atTime(18, 0);
        Funcion funcion = new TresD(new FuncionBase("F-RF001", pelicula, sala, sabado, EstadoFuncion.PROGRAMADA));

        Compra compra = crearCompra("C-RF001", funcion, "A1");
        compra.addConsumible(new Producto("Crispeta M", "Mediana", 10000, 50));
        compra.setTotal(compra.calcularTotal());

        compraServices.procesarCompra(compra);

        assertEquals(36000.0, compra.calcularTotal());
        assertEquals("PAGADA", compra.getTheEstadoCompra().getNombre());
        assertFalse(funcion.asientoDisponible("A1"));
        assertThrows(AsientoNoDisponibleException.class, () -> funcion.ocuparAsiento("A1"));
    }

    @Test
    public void testRF002GestionPagosYTarjetaVirtual() {
        Cine.getInstance().crearCliente(cliente);
        cliente.getTheTarjeta().setSaldo(5000);

        Funcion funcion = new FuncionBase("F-RF002", pelicula, sala,
                LocalDateTime.now().plusDays(2), EstadoFuncion.PROGRAMADA);
        Compra compra = crearCompra("C-RF002", funcion, "B1");

        assertThrows(SaldoInsuficienteException.class, () -> compraServices.procesarCompra(compra));

        cliente.getTheTarjeta().solicitarRecarga(50000, new PagoPSE());
        RecargasAdminController recargasController = new RecargasAdminController();
        assertTrue(recargasController.aprobarSolicitud(cliente).exitoso());
        assertEquals(55000.0, cliente.getTheTarjeta().getSaldo());
    }

    @Test
    public void testRF003SistemaDePuntosVigenciaYRedencion() {
        cliente.agregarPuntos(300, LocalDate.now().minusYears(1).minusDays(1), "C-VENCIDA");
        cliente.agregarPuntos(200, LocalDate.now().minusMonths(1), "C-RECIENTE");
        cliente.agregarPuntos(100, LocalDate.now().minusMonths(10), "C-PROXIMA");

        assertEquals(300, cliente.getPuntos());
        assertTrue(cliente.redimirPuntos(120));
        assertEquals(180, cliente.getPuntos());
        assertEquals(0, cliente.getHistorialPuntos().get(2).getPuntosDisponibles());
        assertEquals(180, cliente.getHistorialPuntos().get(1).getPuntosDisponibles());
    }

    @Test
    public void testRF004GestionReembolsosYCancelaciones() throws Exception {
        Funcion funcionParcial = new FuncionBase("F-RF004-A", pelicula, sala,
                LocalDateTime.now().plusHours(10), EstadoFuncion.PROGRAMADA);
        Compra compraParcial = crearCompra("C-RF004-A", funcionParcial, "C1");
        compraServices.procesarCompra(compraParcial);

        double saldoAntes = cliente.getTheTarjeta().getSaldo();
        compraParcial.cancelar();
        assertEquals(saldoAntes + (compraParcial.getTotal() * 0.8), cliente.getTheTarjeta().getSaldo());
        assertThrows(CancelacionNoPermitidaException.class, compraParcial::cancelar);

        Funcion funcionCine = new FuncionBase("F-RF004-B", pelicula, sala,
                LocalDateTime.now().plusHours(5), EstadoFuncion.PROGRAMADA);
        Compra compraCine = crearCompra("C-RF004-B", funcionCine, "C2");
        compraServices.procesarCompra(compraCine);

        double saldoAntesCine = cliente.getTheTarjeta().getSaldo();
        cineFacade.cancelarFuncion(funcionCine, EstadoFuncion.CANCELADA);
        assertEquals(saldoAntesCine + compraCine.getTotal(), cliente.getTheTarjeta().getSaldo());
        assertEquals("REEMBOLSADA", compraCine.getTheEstadoCompra().getNombre());
    }

    @Test
    public void testRF005GeneracionReportesTxtPdfYEstadisticas(@TempDir Path tempDir) throws Exception {
        ReportesAdminController reportes = new ReportesAdminController();
        LocalDate inicio = LocalDate.now().minusDays(7);
        LocalDate fin = LocalDate.now().plusDays(7);

        File txt = tempDir.resolve("reporte.txt").toFile();
        File pdf = tempDir.resolve("reporte.pdf").toFile();

        reportes.exportarTxt(TipoReporte.VENTAS_PERIODO, inicio, fin, txt);
        reportes.exportarPdf(TipoReporte.VENTAS_PERIODO, inicio, fin, pdf);

        assertTrue(txt.exists() && txt.length() > 0);
        assertTrue(pdf.exists() && pdf.length() > 0);
        assertFalse(reportes.obtenerEntradasPorPelicula(inicio, fin).isEmpty());
        assertEquals(2, reportes.obtenerDistribucionVentas(inicio, fin).size());
    }

    @Test
    public void testRF006AutenticacionClienteYAdministrador() {
        Cine.getInstance().crearCliente(cliente);
        SesionService sesion = new SesionService();

        assertNotNull(sesion.iniciarSesionCliente(cliente.getId(), "Clave123"));
        assertNotNull(sesion.iniciarSesionAdministrador("123", "123"));
        assertNull(sesion.iniciarSesionCliente(cliente.getId(), "ClaveErronea"));
    }

    @Test
    public void testRF007GestionAdministrativaDeCatalogo() {
        PeliculasController peliculas = new PeliculasController();
        String nombrePelicula = "Dune-" + UUID.randomUUID();
        assertTrue(peliculas.crearPelicula(nombrePelicula, "Denis Villeneuve",
                "Arrakis", ClasificacionPelicula.MAYORES_12, GeneroPelicula.CIENCIA_FICCION, 155).exitoso());
        assertNotNull(Cine.getInstance().obtenerPelicula(nombrePelicula));

        PromocionesAdminController promos = new PromocionesAdminController();
        String nombrePromo = "Promo-" + UUID.randomUUID();
        assertTrue(promos.crearPromocion(nombrePromo, "50% en entradas",
                TipoPromocion.ENTRADAS, 50, LocalDate.now(), LocalDate.now().plusDays(15), true).exitoso());
        assertNotNull(Cine.getInstance().obtenerPromocion(nombrePromo));

        Combo combo = new Combo("Combo Especial", "Compuesto");
        combo.agregarConsumible(new Producto("Perro", "", 12000, 60));
        combo.agregarConsumible(new Producto("Gaseosa", "", 7000, 35));
        assertEquals(19000.0, combo.calcularCosto());
    }

    @Test
    public void testRF008ConsultarHistorialComprasPuntosYSaldo() throws Exception {
        Funcion funcion = new FuncionBase("F-RF008", pelicula, sala,
                LocalDateTime.now().plusDays(4), EstadoFuncion.PROGRAMADA);
        Compra compra = crearCompra("C-RF008", funcion, "D4");
        compraServices.procesarCompra(compra);

        ComprasController compras = new ComprasController();
        PuntosController puntos = new PuntosController();
        TarjetaController tarjeta = new TarjetaController();

        assertIterableEquals(List.of(compra), compras.obtenerCompras(cliente));
        assertTrue(puntos.obtenerPuntosDisponibles(cliente) > 0);
        assertEquals(200000.0 - compra.getTotal(), tarjeta.obtenerSaldo(cliente));
        assertEquals(2, tarjeta.obtenerHistorial(cliente).size());
    }
}
