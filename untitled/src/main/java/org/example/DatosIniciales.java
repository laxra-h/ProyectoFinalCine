package org.example;

import org.example.model.Cine;
import org.example.model.consumibleComposite.Combo;
import org.example.model.consumibleComposite.Producto;
import org.example.model.funcionDecorator.CuatroD;
import org.example.model.funcionDecorator.EstadoFuncion;
import org.example.model.funcionDecorator.Funcion;
import org.example.model.funcionDecorator.FuncionBase;
import org.example.model.funcionDecorator.TresD;
import org.example.model.pelicula.ClasificacionPelicula;
import org.example.model.pelicula.GeneroPelicula;
import org.example.model.pelicula.Pelicula;
import org.example.model.salaPrototype.Sala;
import org.example.model.usuarios.Cliente;
import org.example.model.Ticket;
import org.example.model.compra.Compra;
import org.example.model.compra.estadoCompra.EstadoCompra;
import org.example.model.compra.estadoCompra.EstadoPagada;
import org.example.model.compra.estadoCompra.EstadoCancelada;
import org.example.model.compra.estadoCompra.EstadoReembolsada;
import org.example.model.consumibleComposite.Consumible;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DatosIniciales {

    public static void inicializar() {

        Cine cine = Cine.getInstance();

        inicializarPeliculas(cine);
        inicializarSalas(cine);
        inicializarFunciones(cine);
    }

    private static void inicializarPeliculas(Cine cine) {

        if (!cine.getListPeliculas().isEmpty()) {
            return;
        }

        Pelicula conFaldas = new Pelicula(
                "Con faldas y a lo loco",
                "Billy Wilder",
                "Dos músicos presencian un crimen y, para escapar de los responsables, deciden disfrazarse de mujeres e integrarse en una banda musical femenina.",
                ClasificacionPelicula.MAYORES_12,
                GeneroPelicula.COMEDIA,
                121
        );

        Pelicula casablanca = new Pelicula(
                "Casablanca",
                "Michael Curtiz",
                "Durante la Segunda Guerra Mundial, el dueño de un café en Casablanca debe enfrentarse a su pasado cuando reaparece un antiguo amor.",
                ClasificacionPelicula.TODOS,
                GeneroPelicula.ROMANCE,
                102
        );

        Pelicula dracula = new Pelicula(
                "Drácula",
                "Tod Browning",
                "El misterioso conde Drácula viaja desde Transilvania hasta Inglaterra, donde comienza a aterrorizar a quienes lo rodean.",
                ClasificacionPelicula.MAYORES_18,
                GeneroPelicula.TERROR,
                75
        );

        cine.getListPeliculas().add(conFaldas);
        cine.getListPeliculas().add(casablanca);
        cine.getListPeliculas().add(dracula);
    }

    private static void inicializarSalas(Cine cine) {

        if (!cine.getListSalas().isEmpty()) {
            return;
        }

        cine.crearSala("Sala 1");
        cine.crearSala("Sala 2");
        cine.crearSala("Sala 3");
    }

    private static void inicializarFunciones(Cine cine) {

        if (cine.getListPeliculas().size() < 3 ||
                cine.getListSalas().size() < 3 ||
                !cine.getListFunciones().isEmpty()) {
            return;
        }

        Pelicula conFaldas = cine.getListPeliculas().get(0);
        Pelicula casablanca = cine.getListPeliculas().get(1);
        Pelicula dracula = cine.getListPeliculas().get(2);

        Sala sala1 = cine.obtenerSala("Sala 1");
        Sala sala2 = cine.obtenerSala("Sala 2");
        Sala sala3 = cine.obtenerSala("Sala 3");

        crearFunciones(cine, conFaldas, sala1, "CF", 1,
                new LocalTime[]{
                        LocalTime.of(10, 0),
                        LocalTime.of(14, 0),
                        LocalTime.of(19, 0)
                },
                new String[]{"NORMAL", "3D", "4D"},
                EstadoFuncion.FINALIZADA
        );

        crearFunciones(cine, casablanca, sala2, "CA", 2,
                new LocalTime[]{
                        LocalTime.of(11, 0),
                        LocalTime.of(15, 0),
                        LocalTime.of(20, 0)
                },
                new String[]{"3D", "NORMAL", "4D"},
                EstadoFuncion.CANCELADA
        );

        crearFunciones(cine, dracula, sala3, "DR", 3,
                new LocalTime[]{
                        LocalTime.of(9, 0),
                        LocalTime.of(16, 0),
                        LocalTime.of(21, 0)
                },
                new String[]{"4D", "NORMAL", "3D"},
                EstadoFuncion.EN_CURSO
        );
    }

    private static void crearFunciones(
            Cine cine,
            Pelicula pelicula,
            Sala sala,
            String prefijo,
            int diaInicial,
            LocalTime[] horarios,
            String[] formatos,
            EstadoFuncion estadoAdicional
    ) {

        for (int i = 0; i < 3; i++) {

            LocalDate fecha = LocalDate.now().plusDays(diaInicial + i);

            LocalDateTime fechaFuncion = LocalDateTime.of(
                    fecha,
                    horarios[i]
            );

            Funcion funcion = crearFuncion(
                    prefijo + (i + 1),
                    pelicula,
                    sala,
                    fechaFuncion,
                    EstadoFuncion.PROGRAMADA,
                    formatos[i]
            );

            cine.getListFunciones().add(funcion);
        }

        LocalDateTime fechaAdicional;

        if (estadoAdicional == EstadoFuncion.EN_CURSO) {
            fechaAdicional = LocalDateTime.now().withSecond(0).withNano(0);
        } else {
            fechaAdicional = LocalDateTime.of(
                    LocalDate.now().minusDays(1),
                    LocalTime.of(8, 0)
            );
        }

        Funcion funcionAdicional = crearFuncion(
                prefijo + "4",
                pelicula,
                sala,
                fechaAdicional,
                estadoAdicional,
                "NORMAL"
        );

        cine.getListFunciones().add(funcionAdicional);
    }

    private static Funcion crearFuncion(
            String codigo,
            Pelicula pelicula,
            Sala sala,
            LocalDateTime fecha,
            EstadoFuncion estado,
            String formato
    ) {

        Funcion funcion = new FuncionBase(
                codigo,
                pelicula,
                sala,
                fecha,
                estado
        );

        switch (formato) {
            case "3D":
                funcion = new TresD(funcion);
                break;

            case "4D":
                funcion = new CuatroD(funcion);
                break;
        }

        return funcion;
    }



    public static void inicializarConfiteria() {

        Cine cine = Cine.getInstance();

        if (!cine.getListProductos().isEmpty() || !cine.getListCombos().isEmpty()) {
            return;
        }

        Producto gaseosa = new Producto(
                "Gaseosa",
                "Gaseosa personal",
                7000,
                35
        );

        Producto crispetaPequena = new Producto(
                "Crispeta P",
                "Crispeta tamaño pequeño",
                7000,
                35
        );

        Producto crispetaMediana = new Producto(
                "Crispeta M",
                "Crispeta tamaño mediano",
                10000,
                50
        );

        Producto crispetaGrande = new Producto(
                "Crispeta G",
                "Crispeta tamaño grande",
                14000,
                70
        );

        Producto perroCaliente = new Producto(
                "Perro",
                "Perro caliente tradicional",
                12000,
                60
        );

        Producto chocolatina = new Producto(
                "Chocolatina",
                "Chocolatina individual",
                5000,
                25
        );

        cine.crearProducto(gaseosa);
        cine.crearProducto(crispetaPequena);
        cine.crearProducto(crispetaMediana);
        cine.crearProducto(crispetaGrande);
        cine.crearProducto(perroCaliente);
        cine.crearProducto(chocolatina);

        Combo clasico = new Combo(
                "Clásico",
                "La combinación perfecta para disfrutar"
        );

        clasico.agregarConsumible(gaseosa);
        clasico.agregarConsumible(crispetaMediana);

        cine.crearCombo(clasico);

        Combo familiar = new Combo(
                "Familiar",
                "Ideal para compartir en familia o con amigos"
        );

        familiar.agregarConsumible(crispetaGrande);
        familiar.agregarConsumible(gaseosa);
        familiar.agregarConsumible(gaseosa);
        familiar.agregarConsumible(gaseosa);
        familiar.agregarConsumible(perroCaliente);

        cine.crearCombo(familiar);

        Combo junior = new Combo(
                "Junior",
                "Especial para disfrutar de los clásicos"
        );

        junior.agregarConsumible(crispetaPequena);
        junior.agregarConsumible(gaseosa);
        junior.agregarConsumible(chocolatina);

        cine.crearCombo(junior);

        Combo pro = new Combo(
                "Pro",
                "La experiencia completa para compartir"
        );

        pro.agregarConsumible(familiar);
        pro.agregarConsumible(junior);

        cine.crearCombo(pro);
    }

    public static void inicializarClientes() {

        Cine cine = Cine.getInstance();

        if (!cine.getListClientes().isEmpty()) {
            return;
        }

        Cliente cliente1 = new Cliente.Builder("0", "Laura Gómez", "0", 0)
                .telefono("3104567890")
                .email("laura.gomez@gmail.com")
                .fechaNacimiento(LocalDate.of(2005, 10, 3))
                .build();

        Cliente cliente2 = new Cliente.Builder("1002345678", "Andrés Ramírez", "Andres123", 0)
                .telefono("3125678901")
                .email("andres.ramirez@gmail.com")
                .fechaNacimiento(LocalDate.of(2002, 4, 15))
                .build();

        Cliente cliente3 = new Cliente.Builder("1003456789", "Valentina Torres", "Vale123", 0)
                .telefono("3156789012")
                .email("valentina.torres@gmail.com")
                .fechaNacimiento(LocalDate.of(2003, 8, 22))
                .build();

        Cliente cliente4 = new Cliente.Builder("1004567890", "Santiago López", "Santi123", 0)
                .telefono("3007890123")
                .email("santiago.lopez@gmail.com")
                .fechaNacimiento(LocalDate.of(2001, 12, 10))
                .build();

        Cliente cliente5 = new Cliente.Builder("1005678901", "Camila Martínez", "Cami123", 0)
                .telefono("3188901234")
                .email("camila.martinez@gmail.com")
                .fechaNacimiento(LocalDate.of(2004, 6, 18))
                .build();

        cine.crearCliente(cliente1);
        cine.crearCliente(cliente2);
        cine.crearCliente(cliente3);
        cine.crearCliente(cliente4);
        cine.crearCliente(cliente5);
    }

    public static void inicializarCompras() {

        Cine cine = Cine.getInstance();

        if (!cine.getListCompras().isEmpty()) {
            return;
        }

        if (cine.getListClientes().size() < 5 ||
                cine.getListPeliculas().size() < 3 ||
                cine.getListSalas().size() < 3 ||
                cine.getListProductos().isEmpty() ||
                cine.getListCombos().isEmpty()) {
            return;
        }

        Cliente laura = cine.getListClientes().get(0);
        Cliente andres = cine.getListClientes().get(1);
        Cliente valentina = cine.getListClientes().get(2);
        Cliente santiago = cine.getListClientes().get(3);
        Cliente camila = cine.getListClientes().get(4);

        Pelicula conFaldas = cine.getListPeliculas().get(0);
        Pelicula casablanca = cine.getListPeliculas().get(1);
        Pelicula dracula = cine.getListPeliculas().get(2);

        Sala sala1 = cine.obtenerSala("Sala 1");
        Sala sala2 = cine.obtenerSala("Sala 2");
        Sala sala3 = cine.obtenerSala("Sala 3");

        LocalDate hoy = LocalDate.now();
        LocalDate ayer = hoy.minusDays(1);
        LocalDate manana = hoy.plusDays(1);

        Funcion funcionLaura = crearFuncion(
                "R001", conFaldas, sala1,
                manana.plusDays(1).atTime(12, 0),
                EstadoFuncion.PROGRAMADA, "NORMAL"
        );

        Funcion funcionAndres = crearFuncion(
                "R002", casablanca, sala2,
                manana.atTime(18, 0),
                EstadoFuncion.PROGRAMADA, "3D"
        );

        Funcion funcionValentina = crearFuncion(
                "R003", dracula, sala3,
                manana.atTime(11, 30),
                EstadoFuncion.PROGRAMADA, "4D"
        );

        Funcion funcionSantiago = crearFuncion(
                "R004", conFaldas, sala2,
                manana.plusDays(1).atTime(15, 0),
                EstadoFuncion.PROGRAMADA, "3D"
        );

        Funcion funcionCamila = crearFuncion(
                "R005", casablanca, sala3,
                manana.atTime(16, 0),
                EstadoFuncion.PROGRAMADA, "NORMAL"
        );

        Funcion funcionLaura2 = crearFuncion(
                "H001", conFaldas, sala1,
                ayer.atTime(16, 0),
                EstadoFuncion.FINALIZADA, "NORMAL"
        );

        Funcion funcionAndres2 = crearFuncion(
                "H002", casablanca, sala2,
                ayer.atTime(18, 0),
                EstadoFuncion.FINALIZADA, "3D"
        );

        Funcion funcionValentina2 = crearFuncion(
                "H003", dracula, sala3,
                manana.atTime(19, 0),
                EstadoFuncion.PROGRAMADA, "4D"
        );

        Funcion funcionSantiago2 = crearFuncion(
                "H004", conFaldas, sala1,
                ayer.atTime(20, 0),
                EstadoFuncion.FINALIZADA, "NORMAL"
        );

        Funcion funcionCamila2 = crearFuncion(
                "H005", casablanca, sala2,
                ayer.atTime(21, 0),
                EstadoFuncion.FINALIZADA, "3D"
        );

        cine.getListFunciones().add(funcionLaura);
        cine.getListFunciones().add(funcionAndres);
        cine.getListFunciones().add(funcionValentina);
        cine.getListFunciones().add(funcionSantiago);
        cine.getListFunciones().add(funcionCamila);

        cine.getListFunciones().add(funcionLaura2);
        cine.getListFunciones().add(funcionAndres2);
        cine.getListFunciones().add(funcionValentina2);
        cine.getListFunciones().add(funcionSantiago2);
        cine.getListFunciones().add(funcionCamila2);

        Consumible clasico = cine.obtenerCombo("Clásico");
        Consumible familiar = cine.obtenerCombo("Familiar");
        Consumible junior = cine.obtenerCombo("Junior");
        Consumible pro = cine.obtenerCombo("Pro");

        Consumible gaseosa = cine.obtenerProducto("Gaseosa");
        Consumible crispetaGrande = cine.obtenerProducto("Crispeta G");
        Consumible crispetaMediana = cine.obtenerProducto("Crispeta M");
        Consumible chocolatina = cine.obtenerProducto("Chocolatina");
        Consumible perro = cine.obtenerProducto("Perro");

        crearCompra(
                cine, laura, "C001", funcionLaura,
                new String[]{"A1", "A2"},
                hoy, new EstadoPagada(),
                clasico
        );

        crearCompra(
                cine, andres, "C002", funcionAndres,
                new String[]{"B1"},
                hoy, new EstadoPagada(),
                familiar
        );

        crearCompra(
                cine, valentina, "C003", funcionValentina,
                new String[]{"C1", "C2"},
                hoy, new EstadoPagada(),
                junior
        );

        crearCompra(
                cine, santiago, "C004", funcionSantiago,
                new String[]{"D1"},
                hoy, new EstadoPagada(),
                pro
        );

        crearCompra(
                cine, camila, "C005", funcionCamila,
                new String[]{"E1"},
                hoy, new EstadoPagada(),
                perro, gaseosa
        );

        crearCompra(
                cine, laura, "C006", funcionLaura2,
                new String[]{"A3"},
                ayer, new EstadoReembolsada(),
                chocolatina
        );

        crearCompra(
                cine, andres, "C007", funcionAndres2,
                new String[]{"B2"},
                ayer, new EstadoCancelada(),
                gaseosa
        );

        crearCompra(
                cine, valentina, "C008", funcionValentina2,
                new String[]{"C3"},
                hoy, null,
                crispetaGrande
        );

        crearCompra(
                cine, santiago, "C009", funcionSantiago2,
                new String[]{"D2", "D3"},
                ayer, new EstadoPagada(),
                junior, chocolatina
        );

        crearCompra(
                cine, camila, "C010", funcionCamila2,
                new String[]{"E2"},
                ayer, new EstadoReembolsada(),
                clasico
        );
    }


    private static void crearCompra(
            Cine cine,
            Cliente cliente,
            String codigo,
            Funcion funcion,
            String[] asientos,
            LocalDate fechaCompra,
            EstadoCompra estado,
            Consumible... consumibles
    ) {

        Compra compra = new Compra(
                codigo,
                fechaCompra,
                0,
                0
        );

        compra.asignarCliente(cliente);

        for (int i = 0; i < asientos.length; i++) {

            Ticket ticket = new Ticket(
                    codigo + "-T" + (i + 1),
                    asientos[i]
            );

            ticket.setTheFuncion(funcion);
            ticket.setPrecio(funcion.calcularCosto());

            compra.addTicket(ticket);
        }

        for (Consumible consumible : consumibles) {
            if (consumible != null) {
                compra.addConsumible(consumible);
            }
        }

        compra.setTotal(compra.calcularTotal());
        compra.setPuntosCompra(compra.calcularPuntosCompra());

        if (estado instanceof EstadoPagada) {
            compra.pagar();
        } else if (estado != null) {
            compra.cambiarEstado(estado);
        }

        cliente.getListCompras().add(compra);
        cine.getListCompras().add(compra);
    }
}