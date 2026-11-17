package org.example.viewController;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.controller.CarteleraController;
import org.example.model.funcionDecorator.Funcion;
import org.example.model.pelicula.Pelicula;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class CarteleraViewController {

    private final CarteleraController controller = new CarteleraController();

    private static final int MAX_PELICULAS = 3;
    private static final int MAX_FUNCIONES = 3;

    private static final Locale LOCALE = Locale.forLanguageTag("es-CO");

    private static final DateTimeFormatter FORMATO_DIA =
            DateTimeFormatter.ofPattern("EEE d MMM", LOCALE);

    private static final DateTimeFormatter FORMATO_HORA =
            DateTimeFormatter.ofPattern("HH:mm", LOCALE);

    @FXML private VBox tarjetaPelicula1;
    @FXML private VBox tarjetaPelicula2;
    @FXML private VBox tarjetaPelicula3;

    @FXML private Label lblClasificacion1;
    @FXML private Label lblClasificacion2;
    @FXML private Label lblClasificacion3;

    @FXML private Label lblTitulo1;
    @FXML private Label lblTitulo2;
    @FXML private Label lblTitulo3;

    @FXML private Label lblMeta1;
    @FXML private Label lblMeta2;
    @FXML private Label lblMeta3;

    @FXML private Label lblDirector1;
    @FXML private Label lblDirector2;
    @FXML private Label lblDirector3;

    @FXML private Label lblDescripcion1;
    @FXML private Label lblDescripcion2;
    @FXML private Label lblDescripcion3;

    @FXML private VBox funcionesPelicula1;
    @FXML private VBox funcionesPelicula2;
    @FXML private VBox funcionesPelicula3;

    @FXML private HBox filaFuncion1_1;
    @FXML private HBox filaFuncion1_2;
    @FXML private HBox filaFuncion1_3;

    @FXML private HBox filaFuncion2_1;
    @FXML private HBox filaFuncion2_2;
    @FXML private HBox filaFuncion2_3;

    @FXML private HBox filaFuncion3_1;
    @FXML private HBox filaFuncion3_2;
    @FXML private HBox filaFuncion3_3;

    @FXML private Label lblDia1_1;
    @FXML private Label lblDia1_2;
    @FXML private Label lblDia1_3;

    @FXML private Label lblHora1_1;
    @FXML private Label lblHora1_2;
    @FXML private Label lblHora1_3;

    @FXML private Label lblSala1_1;
    @FXML private Label lblSala1_2;
    @FXML private Label lblSala1_3;

    @FXML private Label lblFormato1_1;
    @FXML private Label lblFormato1_2;
    @FXML private Label lblFormato1_3;

    @FXML private Label lblPrecio1_1;
    @FXML private Label lblPrecio1_2;
    @FXML private Label lblPrecio1_3;

    @FXML private Label lblDia2_1;
    @FXML private Label lblDia2_2;
    @FXML private Label lblDia2_3;

    @FXML private Label lblHora2_1;
    @FXML private Label lblHora2_2;
    @FXML private Label lblHora2_3;

    @FXML private Label lblSala2_1;
    @FXML private Label lblSala2_2;
    @FXML private Label lblSala2_3;

    @FXML private Label lblFormato2_1;
    @FXML private Label lblFormato2_2;
    @FXML private Label lblFormato2_3;

    @FXML private Label lblPrecio2_1;
    @FXML private Label lblPrecio2_2;
    @FXML private Label lblPrecio2_3;

    @FXML private Label lblDia3_1;
    @FXML private Label lblDia3_2;
    @FXML private Label lblDia3_3;

    @FXML private Label lblHora3_1;
    @FXML private Label lblHora3_2;
    @FXML private Label lblHora3_3;

    @FXML private Label lblSala3_1;
    @FXML private Label lblSala3_2;
    @FXML private Label lblSala3_3;

    @FXML private Label lblFormato3_1;
    @FXML private Label lblFormato3_2;
    @FXML private Label lblFormato3_3;

    @FXML private Label lblPrecio3_1;
    @FXML private Label lblPrecio3_2;
    @FXML private Label lblPrecio3_3;

    private record Espacio(
            VBox tarjeta,
            Label clasificacion,
            Label titulo,
            Label meta,
            Label director,
            Label descripcion,
            HBox[] filas,
            Label[][] datosFunciones
    ) {}

    @FXML
    private void initialize() {

        List<Espacio> espacios = List.of(

                new Espacio(
                        tarjetaPelicula1,
                        lblClasificacion1,
                        lblTitulo1,
                        lblMeta1,
                        lblDirector1,
                        lblDescripcion1,
                        new HBox[]{filaFuncion1_1, filaFuncion1_2, filaFuncion1_3},
                        new Label[][]{
                                {lblDia1_1, lblHora1_1, lblSala1_1, lblFormato1_1, lblPrecio1_1},
                                {lblDia1_2, lblHora1_2, lblSala1_2, lblFormato1_2, lblPrecio1_2},
                                {lblDia1_3, lblHora1_3, lblSala1_3, lblFormato1_3, lblPrecio1_3}
                        }
                ),

                new Espacio(
                        tarjetaPelicula2,
                        lblClasificacion2,
                        lblTitulo2,
                        lblMeta2,
                        lblDirector2,
                        lblDescripcion2,
                        new HBox[]{filaFuncion2_1, filaFuncion2_2, filaFuncion2_3},
                        new Label[][]{
                                {lblDia2_1, lblHora2_1, lblSala2_1, lblFormato2_1, lblPrecio2_1},
                                {lblDia2_2, lblHora2_2, lblSala2_2, lblFormato2_2, lblPrecio2_2},
                                {lblDia2_3, lblHora2_3, lblSala2_3, lblFormato2_3, lblPrecio2_3}
                        }
                ),

                new Espacio(
                        tarjetaPelicula3,
                        lblClasificacion3,
                        lblTitulo3,
                        lblMeta3,
                        lblDirector3,
                        lblDescripcion3,
                        new HBox[]{filaFuncion3_1, filaFuncion3_2, filaFuncion3_3},
                        new Label[][]{
                                {lblDia3_1, lblHora3_1, lblSala3_1, lblFormato3_1, lblPrecio3_1},
                                {lblDia3_2, lblHora3_2, lblSala3_2, lblFormato3_2, lblPrecio3_2},
                                {lblDia3_3, lblHora3_3, lblSala3_3, lblFormato3_3, lblPrecio3_3}
                        }
                )
        );

        List<Pelicula> peliculas = controller.obtenerPeliculas();

        for (int i = 0; i < MAX_PELICULAS; i++) {

            Espacio espacio = espacios.get(i);

            if (i < peliculas.size()) {
                llenarEspacio(espacio, peliculas.get(i));
            } else {
                espacio.tarjeta().setVisible(false);
                espacio.tarjeta().setManaged(false);
            }
        }
    }

    private void llenarEspacio(Espacio espacio, Pelicula pelicula) {

        espacio.titulo().setText(pelicula.getNombre().toUpperCase(LOCALE));

        espacio.meta().setText(pelicula.getGenero().name().replace('_', ' ') + "  |  " + pelicula.getDuracionMin() + " MIN");

        espacio.director().setText("Dirigida por " + pelicula.getDirector());

        espacio.descripcion().setText(pelicula.getDescripcion());

        espacio.clasificacion().setText(obtenerClasificacion(pelicula));

        llenarFunciones(espacio, pelicula);
    }

    private void llenarFunciones(Espacio espacio, Pelicula pelicula) {

        List<Funcion> funciones = controller.obtenerFuncionesDisponibles(pelicula);

        for (int i = 0; i < MAX_FUNCIONES; i++) {

            HBox fila = espacio.filas()[i];

            if (i >= funciones.size()) {
                fila.setVisible(false);
                fila.setManaged(false);
                continue;
            }

            fila.setVisible(true);
            fila.setManaged(true);

            llenarFila(
                    espacio.datosFunciones()[i],
                    funciones.get(i)
            );
        }
    }

    private void llenarFila(Label[] datos, Funcion funcion) {

        datos[0].setText(
                capitalizar(funcion.getFecha().format(FORMATO_DIA))
        );

        datos[1].setText(
                funcion.getFecha().format(FORMATO_HORA)
        );

        datos[2].setText(
                funcion.getSala() != null
                        ? "Sala " + soloNumero(funcion.getSala().getNumeroSala())
                        : ""
        );

        String formato = controller.obtenerFormato(funcion);

        datos[3].setText(formato);

        datos[3].getStyleClass().removeAll("formato-3d", "formato-4d");

        if (formato.equals("3D")) {
            datos[3].getStyleClass().add("formato-3d");
        } else if (formato.equals("4D")) {
            datos[3].getStyleClass().add("formato-4d");
        }

        datos[4].setText(
                String.format(LOCALE, "$%,.0f", funcion.calcularCosto())
        );
    }

    private String obtenerClasificacion(Pelicula pelicula) {

        String nombre = pelicula.getClasificacion().name();

        if (nombre.startsWith("MAYORES_")) {
            return "+" + nombre.substring("MAYORES_".length());
        }

        return nombre;
    }

    private String soloNumero(String numero) {
        return numero.replaceAll("(?i)^sala\\s*", "");
    }

    private String capitalizar(String texto) {

        return texto.isEmpty()
                ? texto
                : texto.substring(0, 1).toUpperCase(LOCALE) + texto.substring(1);
    }
}