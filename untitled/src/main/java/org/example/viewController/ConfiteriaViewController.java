package org.example.viewController;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.controller.ConfiteriaController;
import org.example.model.consumibleComposite.Combo;
import org.example.model.consumibleComposite.Consumible;
import org.example.model.consumibleComposite.Producto;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ConfiteriaViewController {

    @FXML
    private VBox tarjetaCombo1;
    @FXML
    private VBox tarjetaCombo2;
    @FXML
    private VBox tarjetaCombo3;
    @FXML
    private VBox tarjetaCombo4;

    @FXML
    private Label lblNombreCombo1;
    @FXML
    private Label lblNombreCombo2;
    @FXML
    private Label lblNombreCombo3;
    @FXML
    private Label lblNombreCombo4;

    @FXML
    private Label lblDescripcionCombo1;
    @FXML
    private Label lblDescripcionCombo2;
    @FXML
    private Label lblDescripcionCombo3;
    @FXML
    private Label lblDescripcionCombo4;

    @FXML
    private Label lblPrecioCombo1;
    @FXML
    private Label lblPrecioCombo2;
    @FXML
    private Label lblPrecioCombo3;
    @FXML
    private Label lblPrecioCombo4;

    @FXML
    private Label lblPuntosCombo1;
    @FXML
    private Label lblPuntosCombo2;
    @FXML
    private Label lblPuntosCombo3;
    @FXML
    private Label lblPuntosCombo4;

    @FXML
    private VBox incluyeCombo1;
    @FXML
    private VBox incluyeCombo2;
    @FXML
    private VBox incluyeCombo3;
    @FXML
    private VBox incluyeCombo4;

    @FXML
    private VBox fichaProducto1;
    @FXML
    private VBox fichaProducto2;
    @FXML
    private VBox fichaProducto3;
    @FXML
    private VBox fichaProducto4;
    @FXML
    private VBox fichaProducto5;
    @FXML
    private VBox fichaProducto6;

    @FXML
    private Label lblNombreProducto1;
    @FXML
    private Label lblNombreProducto2;
    @FXML
    private Label lblNombreProducto3;
    @FXML
    private Label lblNombreProducto4;
    @FXML
    private Label lblNombreProducto5;
    @FXML
    private Label lblNombreProducto6;

    @FXML
    private Label lblPrecioProducto1;
    @FXML
    private Label lblPrecioProducto2;
    @FXML
    private Label lblPrecioProducto3;
    @FXML
    private Label lblPrecioProducto4;
    @FXML
    private Label lblPrecioProducto5;
    @FXML
    private Label lblPrecioProducto6;

    @FXML
    private Label lblPuntosProducto1;
    @FXML
    private Label lblPuntosProducto2;
    @FXML
    private Label lblPuntosProducto3;
    @FXML
    private Label lblPuntosProducto4;
    @FXML
    private Label lblPuntosProducto5;
    @FXML
    private Label lblPuntosProducto6;

    private final ConfiteriaController controller = new ConfiteriaController();

    private VBox[] tarjetas;
    private Label[] nombres;
    private Label[] descripciones;
    private Label[] precios;
    private Label[] puntos;
    private VBox[] contenidos;

    private final String[] nombresCombos = {
            "Clásico",
            "Familiar",
            "Junior",
            "Pro"
    };

    @FXML
    private void initialize() {

        tarjetas = new VBox[]{
                tarjetaCombo1,
                tarjetaCombo2,
                tarjetaCombo3,
                tarjetaCombo4
        };

        nombres = new Label[]{
                lblNombreCombo1,
                lblNombreCombo2,
                lblNombreCombo3,
                lblNombreCombo4
        };

        descripciones = new Label[]{
                lblDescripcionCombo1,
                lblDescripcionCombo2,
                lblDescripcionCombo3,
                lblDescripcionCombo4
        };

        precios = new Label[]{
                lblPrecioCombo1,
                lblPrecioCombo2,
                lblPrecioCombo3,
                lblPrecioCombo4
        };

        puntos = new Label[]{
                lblPuntosCombo1,
                lblPuntosCombo2,
                lblPuntosCombo3,
                lblPuntosCombo4
        };

        contenidos = new VBox[]{
                incluyeCombo1,
                incluyeCombo2,
                incluyeCombo3,
                incluyeCombo4
        };

        cargarCombos();
        cargarProductos();
    }

    private void cargarCombos() {

        List<Combo> combos = controller.obtenerCombos();

        for (int i = 0; i < nombresCombos.length; i++) {

            Combo combo = buscarCombo(combos, nombresCombos[i]);

            if (combo == null) {
                tarjetas[i].setVisible(false);
                tarjetas[i].setManaged(false);
                continue;
            }

            nombres[i].setText(combo.getNombre());
            descripciones[i].setText(combo.getDescripcion());

            precios[i].setText(String.format("$%,.0f", combo.calcularCosto()));

            puntos[i].setText(combo.calcularPuntos() + " puntos");

            contenidos[i].getChildren().clear();

            cargarContenidoCombo(contenidos[i], combo);
        }
    }

    private Combo buscarCombo(List<Combo> combos, String nombre) {

        for (Combo combo : combos) {
            if (combo.getNombre().equalsIgnoreCase(nombre)) {
                return combo;
            }
        }

        return null;
    }

    private void cargarContenidoCombo(VBox contenedor, Combo combo) {

        Map<String, Integer> cantidades = new LinkedHashMap<>();

        for (Consumible consumible : combo.getListaConsumibles()) {

            if (consumible instanceof Producto) {
                cantidades.merge(consumible.getNombre(), 1, Integer::sum);
            }
        }

        for (Map.Entry<String, Integer> entrada : cantidades.entrySet()) {

            Label elemento = new Label(
                    entrada.getKey() +
                            (entrada.getValue() > 1 ? " x" + entrada.getValue() : "")
            );

            elemento.getStyleClass().add("combo-item");
            contenedor.getChildren().add(elemento);
        }

        for (Consumible consumible : combo.getListaConsumibles()) {

            if (consumible instanceof Combo comboInterno) {

                Label nombreCombo = new Label(comboInterno.getNombre());
                nombreCombo.getStyleClass().add("combo-item");

                contenedor.getChildren().add(nombreCombo);

                cargarContenidoCombo(contenedor, comboInterno);
            }
        }
    }

    private void cargarProductos() {

        List<Producto> productos = controller.obtenerProductos();

        VBox[] fichas = {
                fichaProducto1,
                fichaProducto2,
                fichaProducto3,
                fichaProducto4,
                fichaProducto5,
                fichaProducto6
        };

        Label[] nombres = {
                lblNombreProducto1,
                lblNombreProducto2,
                lblNombreProducto3,
                lblNombreProducto4,
                lblNombreProducto5,
                lblNombreProducto6
        };

        Label[] precios = {
                lblPrecioProducto1,
                lblPrecioProducto2,
                lblPrecioProducto3,
                lblPrecioProducto4,
                lblPrecioProducto5,
                lblPrecioProducto6
        };

        Label[] puntos = {
                lblPuntosProducto1,
                lblPuntosProducto2,
                lblPuntosProducto3,
                lblPuntosProducto4,
                lblPuntosProducto5,
                lblPuntosProducto6
        };

        for (int i = 0; i < fichas.length; i++) {

            if (i >= productos.size()) {
                fichas[i].setVisible(false);
                fichas[i].setManaged(false);
                continue;
            }

            Producto producto = productos.get(i);

            fichas[i].setVisible(true);
            fichas[i].setManaged(true);

            nombres[i].setText(producto.getNombre());

            precios[i].setText(String.format(
                    "$%,.0f",
                    producto.calcularCosto()
            ));

            puntos[i].setText(
                    producto.calcularPuntos() + " puntos"
            );
        }
    }
}