package org.example.model;

import org.example.model.compra.Compra;
import org.example.model.consumibleComposite.Combo;
import org.example.model.consumibleComposite.Producto;
import org.example.model.funcionDecorator.Funcion;
import org.example.model.pelicula.Pelicula;
import org.example.model.salaPrototype.Sala;
import org.example.model.usuarios.Administrador;
import org.example.model.usuarios.Cliente;

import java.util.ArrayList;
import java.util.List;

public final class Cine {


    private static Cine instance;
    private String nombre;
    private String nit;
    private String paginaWeb;
    private List<Administrador> listAministradores;
    private List<Cliente> listClientes;
    private List<Producto> listProductos;
    private List<Combo> listCombos;
    private List<Sala> listSalas;
    private List<Pelicula> listPeliculas;
    private List<Funcion> listFunciones;
    private List<Compra> listCompras;



    /**
     * Metodo constructor privado del cine
     */
    private Cine() {
        this.nombre = "Cinema UQ";
        this.nit = "202611";
        this.paginaWeb = "https://cinemauq.com";
        this.listAministradores = new ArrayList<>();
        this.listClientes = new ArrayList<>();
        this.listProductos = new ArrayList<>();
        this.listCombos = new ArrayList<>();
        this.listSalas = new ArrayList<>();
        this.listPeliculas = new ArrayList<>();
        this.listFunciones = new ArrayList<>();
        this.listCompras = new ArrayList<>();
    }

    /**
     * Metodo de instancia unica para cine
     * @return
     */
    public static Cine getInstance(){
        if (instance == null){
            instance = new Cine();
        }
        return instance;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getPaginaWeb() {
        return paginaWeb;
    }

    public void setPaginaWeb(String paginaWeb) {
        this.paginaWeb = paginaWeb;
    }

    public List<Administrador> getListAministradores() {
        return listAministradores;
    }

    public List<Cliente> getListClientes() {
        return listClientes;
    }

    public List<Producto> getListProductos() {
        return listProductos;
    }

    public List<Combo> getListCombos() {
        return listCombos;
    }

    public List<Sala> getListSalas() {
        return listSalas;
    }

    public List<Pelicula> getListPeliculas() {
        return listPeliculas;
    }

    public List<Funcion> getListFunciones() {
        return listFunciones;
    }

    public List<Compra> getListCompras() {
        return listCompras;
    }

}
