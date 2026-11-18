package org.example.controller;

import org.example.model.Cine;
import org.example.model.consumibleComposite.Combo;
import org.example.model.consumibleComposite.Consumible;
import org.example.model.consumibleComposite.Producto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConfiteriaAdminController {

    private final Cine cine;

    public record ResultadoOperacion(boolean exitoso, String mensaje) {
    }

    public ConfiteriaAdminController() {
        this.cine = Cine.getInstance();
    }

    public List<Combo> obtenerCombos() {
        return new ArrayList<>(cine.getListCombos());
    }

    public List<Producto> obtenerProductos() {
        return new ArrayList<>(cine.getListProductos());
    }

    public List<Consumible> obtenerComponentesDisponibles(Combo comboEditando) {
        List<Consumible> componentes = new ArrayList<>();
        componentes.addAll(cine.getListProductos());

        for (Combo combo : cine.getListCombos()) {
            if (combo != comboEditando && !contieneCombo(combo, comboEditando, new HashSet<>())) {
                componentes.add(combo);
            }
        }

        return componentes;
    }

    public ResultadoOperacion crearProducto(
            String nombre,
            String descripcion,
            double costo,
            int puntos
    ) {
        ResultadoOperacion validacion = validarDatosProducto(nombre, descripcion, costo, puntos);
        if (!validacion.exitoso()) {
            return validacion;
        }

        Producto producto = new Producto(nombre.trim(), descripcion.trim(), costo, puntos);
        if (!cine.crearProducto(producto)) {
            return new ResultadoOperacion(false, "Ya existe un producto con ese nombre.");
        }

        return new ResultadoOperacion(true, "Producto creado correctamente.");
    }

    public ResultadoOperacion actualizarProducto(
            Producto producto,
            String nombre,
            String descripcion,
            double costo,
            int puntos
    ) {
        if (producto == null || !cine.getListProductos().contains(producto)) {
            return new ResultadoOperacion(false, "Selecciona un producto registrado.");
        }

        ResultadoOperacion validacion = validarDatosProducto(nombre, descripcion, costo, puntos);
        if (!validacion.exitoso()) {
            return validacion;
        }

        Producto productoConNombre = cine.obtenerProducto(nombre.trim());
        if (productoConNombre != null && productoConNombre != producto) {
            return new ResultadoOperacion(false, "Ya existe otro producto con ese nombre.");
        }

        producto.setNombre(nombre.trim());
        producto.setDescripcion(descripcion.trim());
        producto.setCosto(costo);
        producto.setPuntosRedimir(puntos);

        return new ResultadoOperacion(true, "Producto actualizado correctamente.");
    }

    public ResultadoOperacion eliminarProducto(Producto producto) {
        if (producto == null || !cine.getListProductos().contains(producto)) {
            return new ResultadoOperacion(false, "Selecciona un producto registrado.");
        }

        for (Combo combo : cine.getListCombos()) {
            if (contieneConsumible(combo, producto, new HashSet<>())) {
                return new ResultadoOperacion(
                        false,
                        "No se puede eliminar porque el producto hace parte del combo \"" +
                                combo.getNombre() + "\". Retíralo primero de los combos."
                );
            }
        }

        if (!cine.eliminarProducto(producto.getNombre())) {
            return new ResultadoOperacion(false, "No fue posible eliminar el producto.");
        }

        return new ResultadoOperacion(true, "Producto eliminado correctamente.");
    }

    public ResultadoOperacion crearCombo(
            String nombre,
            String descripcion,
            List<Consumible> componentes
    ) {
        ResultadoOperacion validacion = validarDatosCombo(nombre, descripcion, componentes);
        if (!validacion.exitoso()) {
            return validacion;
        }

        if (cine.obtenerCombo(nombre.trim()) != null) {
            return new ResultadoOperacion(false, "Ya existe un combo con ese nombre.");
        }

        Combo combo = new Combo(nombre.trim(), descripcion.trim());
        for (Consumible componente : componentes) {
            if (!esComponenteRegistrado(componente)) {
                return new ResultadoOperacion(false, "Uno de los componentes ya no está disponible.");
            }
            combo.agregarConsumible(componente);
        }

        if (!cine.crearCombo(combo)) {
            return new ResultadoOperacion(false, "No fue posible crear el combo.");
        }

        return new ResultadoOperacion(true, "Combo creado correctamente.");
    }

    public ResultadoOperacion actualizarCombo(
            Combo combo,
            String nombre,
            String descripcion,
            List<Consumible> componentes
    ) {
        if (combo == null || !cine.getListCombos().contains(combo)) {
            return new ResultadoOperacion(false, "Selecciona un combo registrado.");
        }

        ResultadoOperacion validacion = validarDatosCombo(nombre, descripcion, componentes);
        if (!validacion.exitoso()) {
            return validacion;
        }

        Combo comboConNombre = cine.obtenerCombo(nombre.trim());
        if (comboConNombre != null && comboConNombre != combo) {
            return new ResultadoOperacion(false, "Ya existe otro combo con ese nombre.");
        }

        for (Consumible componente : componentes) {
            if (!esComponenteRegistrado(componente)) {
                return new ResultadoOperacion(false, "Uno de los componentes ya no está disponible.");
            }
            if (componente == combo ||
                    (componente instanceof Combo comboComponente &&
                            contieneCombo(comboComponente, combo, new HashSet<>()))) {
                return new ResultadoOperacion(
                        false,
                        "No puedes agregar este combo porque produciría una composición circular."
                );
            }
        }

        combo.setNombre(nombre.trim());
        combo.setDescripcion(descripcion.trim());
        combo.getListaConsumibles().clear();
        combo.getListaConsumibles().addAll(componentes);

        return new ResultadoOperacion(true, "Combo actualizado correctamente.");
    }

    public ResultadoOperacion eliminarCombo(Combo combo) {
        if (combo == null || !cine.getListCombos().contains(combo)) {
            return new ResultadoOperacion(false, "Selecciona un combo registrado.");
        }

        for (Combo otroCombo : cine.getListCombos()) {
            if (otroCombo != combo && contieneConsumible(otroCombo, combo, new HashSet<>())) {
                return new ResultadoOperacion(
                        false,
                        "No se puede eliminar porque el combo \"" + otroCombo.getNombre() +
                                "\" lo utiliza como componente."
                );
            }
        }

        if (!cine.eliminarCombo(combo.getNombre())) {
            return new ResultadoOperacion(false, "No fue posible eliminar el combo.");
        }

        return new ResultadoOperacion(true, "Combo eliminado correctamente.");
    }

    private ResultadoOperacion validarDatosProducto(
            String nombre,
            String descripcion,
            double costo,
            int puntos
    ) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return new ResultadoOperacion(false, "El nombre del producto es obligatorio.");
        }
        if (descripcion == null || descripcion.trim().isEmpty()) {
            return new ResultadoOperacion(false, "La descripción del producto es obligatoria.");
        }
        if (!Double.isFinite(costo) || costo <= 0) {
            return new ResultadoOperacion(false, "El precio debe ser un número mayor que cero.");
        }
        if (puntos < 0) {
            return new ResultadoOperacion(false, "Los puntos no pueden ser negativos.");
        }
        return new ResultadoOperacion(true, "Datos válidos.");
    }

    private ResultadoOperacion validarDatosCombo(
            String nombre,
            String descripcion,
            List<Consumible> componentes
    ) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return new ResultadoOperacion(false, "El nombre del combo es obligatorio.");
        }
        if (descripcion == null || descripcion.trim().isEmpty()) {
            return new ResultadoOperacion(false, "La descripción del combo es obligatoria.");
        }
        if (componentes == null || componentes.isEmpty()) {
            return new ResultadoOperacion(false, "Agrega al menos un producto o combo.");
        }
        return new ResultadoOperacion(true, "Datos válidos.");
    }

    private boolean esComponenteRegistrado(Consumible consumible) {
        if (consumible instanceof Producto producto) {
            return cine.getListProductos().contains(producto);
        }
        if (consumible instanceof Combo combo) {
            return cine.getListCombos().contains(combo);
        }
        return false;
    }

    private boolean contieneCombo(Combo origen, Combo buscado, Set<Combo> visitados) {
        if (origen == null || buscado == null || !visitados.add(origen)) {
            return false;
        }
        for (Consumible consumible : origen.getListaConsumibles()) {
            if (consumible == buscado) {
                return true;
            }
            if (consumible instanceof Combo hijo && contieneCombo(hijo, buscado, visitados)) {
                return true;
            }
        }
        return false;
    }

    private boolean contieneConsumible(
            Combo combo,
            Consumible buscado,
            Set<Combo> visitados
    ) {
        if (combo == null || !visitados.add(combo)) {
            return false;
        }
        for (Consumible consumible : combo.getListaConsumibles()) {
            if (consumible == buscado) {
                return true;
            }
            if (consumible instanceof Combo hijo &&
                    contieneConsumible(hijo, buscado, visitados)) {
                return true;
            }
        }
        return false;
    }
}
