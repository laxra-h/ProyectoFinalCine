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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DatosIniciales {

    public static void inicializar() {

        Cine cine = Cine.getInstance();

        if (!cine.getListPeliculas().isEmpty() || !cine.getListSalas().isEmpty() || !cine.getListFunciones().isEmpty()) {
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

        cine.crearSala("Sala 1");
        cine.crearSala("Sala 2");
        cine.crearSala("Sala 3");

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
}