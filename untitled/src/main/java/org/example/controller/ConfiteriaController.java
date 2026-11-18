package org.example.controller;

import org.example.model.Cine;
import org.example.model.consumibleComposite.Combo;
import org.example.model.consumibleComposite.Producto;

import java.util.ArrayList;
import java.util.List;

public class ConfiteriaController {

    private final Cine cine;

    public ConfiteriaController() {
        this.cine = Cine.getInstance();
    }

    public List<Combo> obtenerCombos() {
        return new ArrayList<>(cine.getListCombos());
    }

    public List<Producto> obtenerProductos() {
        return new ArrayList<>(cine.getListProductos());
    }
}