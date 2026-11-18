package org.example.viewController;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import org.example.model.Cine;
import org.example.model.Ticket;
import org.example.model.compra.Compra;
import org.example.model.funcionDecorator.Funcion;
import org.example.model.salaPrototype.Asiento;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

    public class SeleccionarAsientoViewController {

        @FXML private GridPane gridAsientos;
        @FXML private Label lblFuncion;
        @FXML private Label lblCantidadEntradas;
        @FXML private Label lblCantidadSeleccionada;
        @FXML private Button btnAceptar;

        private Funcion funcion;
        private int cantidadEntradas;

        private final List<String> asientosSeleccionados = new ArrayList<>();
        private final Set<String> asientosOcupados = new HashSet<>();

        private Stage ventana;

        public void configurar(Funcion funcion, int cantidadEntradas, Stage ventana) {

            this.funcion = funcion;
            this.cantidadEntradas = cantidadEntradas;
            this.ventana = ventana;

            lblFuncion.setText("Función: " + funcion.getCodigo());
            lblCantidadEntradas.setText("Entradas: " + cantidadEntradas);

            cargarAsientosOcupados();
            generarAsientos();

            actualizarSeleccion();
        }

        private void cargarAsientosOcupados() {

            for (Compra compra : Cine.getInstance().getListCompras()) {

                if (compra.getTheEstadoCompra() == null ||
                        !compra.getTheEstadoCompra().getNombre().equals("PAGADA")) {
                    continue;
                }

                for (Ticket ticket : compra.getListaTickets()) {

                    if (ticket.getTheFuncion() != null &&
                            ticket.getTheFuncion().getCodigo().equals(funcion.getCodigo())) {

                        asientosOcupados.add(ticket.getCodigoAsiento());
                    }
                }
            }
        }

        private void generarAsientos() {

            gridAsientos.getChildren().clear();

            Asiento[] asientos = funcion.getSala().getListAsientos();

            Label espacio = new Label("");
            gridAsientos.add(espacio, 0, 0);

            for (int columna = 1; columna <= funcion.getSala().getNumeroColumnas(); columna++) {

                Label numero = new Label(String.valueOf(columna));
                numero.getStyleClass().add("fila-asiento");

                gridAsientos.add(numero, columna, 0);
            }

            for (int fila = 0; fila < funcion.getSala().getNumeroFilas(); fila++) {

                char letraFila = (char) ('A' + fila);

                Label etiquetaFila = new Label(String.valueOf(letraFila));
                etiquetaFila.getStyleClass().add("fila-asiento");

                gridAsientos.add(etiquetaFila, 0, fila + 1);

                for (int columna = 1; columna <= funcion.getSala().getNumeroColumnas(); columna++) {

                    String codigo = letraFila + String.valueOf(columna);

                    Button asiento = new Button(String.valueOf(columna));

                    asiento.getStyleClass().add("asiento-boton");

                    boolean ocupado = asientosOcupados.contains(codigo);

                    if (ocupado) {

                        asiento.getStyleClass().add("asiento-ocupado");
                        asiento.setDisable(true);

                    } else {

                        asiento.getStyleClass().add("asiento-disponible");

                        asiento.setOnAction(event -> seleccionarAsiento(codigo, asiento));
                    }

                    gridAsientos.add(asiento, columna, fila + 1);
                }
            }
        }

        private void seleccionarAsiento(String codigo, Button boton) {

            if (asientosSeleccionados.contains(codigo)) {

                asientosSeleccionados.remove(codigo);

                boton.getStyleClass().remove("asiento-seleccionado");
                boton.getStyleClass().add("asiento-disponible");

            } else {

                if (asientosSeleccionados.size() >= cantidadEntradas) {
                    return;
                }

                asientosSeleccionados.add(codigo);

                boton.getStyleClass().remove("asiento-disponible");
                boton.getStyleClass().add("asiento-seleccionado");
            }

            actualizarSeleccion();
        }

        private void actualizarSeleccion() {

            lblCantidadSeleccionada.setText(
                    "Asientos seleccionados: " + asientosSeleccionados.size()
            );

            btnAceptar.setDisable(
                    asientosSeleccionados.size() != cantidadEntradas
            );
        }

        @FXML
        private void aceptarSeleccion() {

            if (asientosSeleccionados.size() != cantidadEntradas) {
                return;
            }

            ventana.close();
        }

        @FXML
        private void cancelarSeleccion() {
            asientosSeleccionados.clear();
            ventana.close();
        }

        public List<String> getAsientosSeleccionados() {
            return new ArrayList<>(asientosSeleccionados);
        }
    }

