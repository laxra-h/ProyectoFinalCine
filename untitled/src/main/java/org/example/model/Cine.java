package org.example.model;

import org.example.model.compra.Compra;
import org.example.model.consumibleComposite.Combo;
import org.example.model.consumibleComposite.Producto;
import org.example.model.funcionDecorator.Funcion;
import org.example.model.pelicula.Pelicula;
import org.example.model.salaPrototype.Sala;
import org.example.model.usuarios.Administrador;
import org.example.model.usuarios.Cliente;

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
     * Metodo constructor del cine
     */
    public Cine() {
        this.nombre = "Cinema UQ";
        this.nit = "202611";
        this.paginaWeb = "https://cinemauq.com";
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

}
