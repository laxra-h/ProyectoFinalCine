package org.example.model;

import org.example.model.compra.Compra;
import org.example.model.consumibleComposite.Combo;
import org.example.model.consumibleComposite.Producto;
import org.example.model.funcionDecorator.Funcion;
import org.example.model.pelicula.Pelicula;
import org.example.model.salaPrototype.Sala;
import org.example.model.usuarios.Administrador;
import org.example.model.usuarios.Cliente;

import java.time.LocalDate;
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




    /*
    Metodos CRUD
     */

    // ===================== CRUD ADMINISTRADOR =====================

    /**
     * Metodo para crear un administrador (id unico)
     * @param administrador
     * @return true si se creo, false si es nulo o ya existe
     */
    public boolean crearAdministrador(Administrador administrador) {
        if (administrador == null || obtenerAdministrador(administrador.getId()) != null) {
            return false;
        }
        listAministradores.add(administrador);
        return true;
    }

    /**
     * Metodo para buscar un administrador por id
     * @param id
     * @return el administrador o null si no existe
     */
    public Administrador obtenerAdministrador(String id) {
        for (Administrador administrador : listAministradores) {
            if (administrador.getId().equals(id)) {
                return administrador;
            }
        }
        return null;
    }

    /**
     * Metodo para actualizar los datos de un administrador
     * @return true si se actualizo, false si no existe
     */
    public boolean actualizarAdministrador(String id, String nombre, String telefono, String email, String contrasenia) {
        Administrador administrador = obtenerAdministrador(id);
        if (administrador == null) {
            return false;
        }
        administrador.setNombre(nombre);
        administrador.setTelefono(telefono);
        administrador.setEmail(email);
        administrador.setContrasenia(contrasenia);
        return true;
    }

    /**
     * Metodo para eliminar un administrador por id
     * @return true si se elimino, false si no existe
     */
    public boolean eliminarAdministrador(String id) {
        Administrador administrador = obtenerAdministrador(id);
        return administrador != null && listAministradores.remove(administrador);
    }


// ===================== CRUD CLIENTE =====================

    /**
     * Metodo para crear un cliente (id unico)
     * @return true si se creo, false si es nulo o ya existe
     */
    public boolean crearCliente(Cliente cliente) {
        if (cliente == null || obtenerCliente(cliente.getId()) != null) {
            return false;
        }
        listClientes.add(cliente);
        return true;
    }

    /**
     * Metodo para buscar un cliente por id
     * @return el cliente o null si no existe
     */
    public Cliente obtenerCliente(String id) {
        for (Cliente cliente : listClientes) {
            if (cliente.getId().equals(id)) {
                return cliente;
            }
        }
        return null;
    }

    /**
     * Metodo para actualizar los datos de un cliente
     * @return true si se actualizo, false si no existe
     */
    public boolean actualizarCliente(String id, String nombre, String telefono, String email,
                                     String contrasenia, LocalDate fechaNacimiento) {
        Cliente cliente = obtenerCliente(id);
        if (cliente == null) {
            return false;
        }
        cliente.setNombre(nombre);
        cliente.setTelefono(telefono);
        cliente.setEmail(email);
        cliente.setContrasenia(contrasenia);
        cliente.setFechaNacimiento(fechaNacimiento);
        return true;
    }

    /**
     * Metodo para eliminar un cliente por id
     * @return true si se elimino, false si no existe
     */
    public boolean eliminarCliente(String id) {
        Cliente cliente = obtenerCliente(id);
        return cliente != null && listClientes.remove(cliente);
    }


// ===================== CRUD PRODUCTO =====================

    /**
     * Metodo para crear un producto (nombre unico)
     * @return true si se creo, false si es nulo o ya existe
     */
    public boolean crearProducto(Producto producto) {
        if (producto == null || obtenerProducto(producto.getNombre()) != null) {
            return false;
        }
        listProductos.add(producto);
        return true;
    }

    /**
     * Metodo para buscar un producto por nombre
     * @return el producto o null si no existe
     */
    public Producto obtenerProducto(String nombre) {
        for (Producto producto : listProductos) {
            if (producto.getNombre().equalsIgnoreCase(nombre)) {
                return producto;
            }
        }
        return null;
    }

    /**
     * Metodo para actualizar un producto
     * @return true si se actualizo, false si no existe
     */
    public boolean actualizarProducto(String nombre, String descripcion, double costo, int puntosRedimir) {
        Producto producto = obtenerProducto(nombre);
        if (producto == null) {
            return false;
        }
        producto.setDescripcion(descripcion);
        producto.setCosto(costo);
        producto.setPuntosRedimir(puntosRedimir);
        return true;
    }

    /**
     * Metodo para eliminar un producto; tambien lo quita de los combos que lo contengan
     * @return true si se elimino, false si no existe
     */
    public boolean eliminarProducto(String nombre) {
        Producto producto = obtenerProducto(nombre);
        if (producto == null) {
            return false;
        }
        for (Combo combo : listCombos) {
            combo.eliminarConsumible(producto);
        }
        return listProductos.remove(producto);
    }


// ===================== CRUD COMBO =====================

    /**
     * Metodo para crear un combo (nombre unico)
     * @return true si se creo, false si es nulo o ya existe
     */
    public boolean crearCombo(Combo combo) {
        if (combo == null || obtenerCombo(combo.getNombre()) != null) {
            return false;
        }
        listCombos.add(combo);
        return true;
    }

    /**
     * Metodo para buscar un combo por nombre
     * @return el combo o null si no existe
     */
    public Combo obtenerCombo(String nombre) {
        for (Combo combo : listCombos) {
            if (combo.getNombre().equalsIgnoreCase(nombre)) {
                return combo;
            }
        }
        return null;
    }

    /**
     * Metodo para actualizar la descripcion de un combo
     * @return true si se actualizo, false si no existe
     */
    public boolean actualizarCombo(String nombre, String descripcion) {
        Combo combo = obtenerCombo(nombre);
        if (combo == null) {
            return false;
        }
        combo.setDescripcion(descripcion);
        return true;
    }

    /**
     * Metodo para eliminar un combo por nombre
     * @return true si se elimino, false si no existe
     */
    public boolean eliminarCombo(String nombre) {
        Combo combo = obtenerCombo(nombre);
        return combo != null && listCombos.remove(combo);
    }


// ===================== CRUD SALA =====================

    /**
     * Metodo para crear una sala con sus asientos (patron Prototype)
     * @return true si se creo, false si ya existe o fallo la clonacion
     */
    public boolean crearSala(String numeroSala) {
        if (numeroSala == null || obtenerSala(numeroSala) != null) {
            return false;
        }
        Sala sala = new Sala(numeroSala);
        try {
            sala.creacionAsientos();
        } catch (CloneNotSupportedException e) {
            return false;
        }
        listSalas.add(sala);
        return true;
    }

    /**
     * Metodo para buscar una sala por numero
     * @return la sala o null si no existe
     */
    public Sala obtenerSala(String numeroSala) {
        for (Sala sala : listSalas) {
            if (sala.getNumeroSala().equals(numeroSala)) {
                return sala;
            }
        }
        return null;
    }

    /**
     * Metodo para actualizar el numero de una sala
     * @return true si se actualizo, false si no existe o el nuevo numero ya esta en uso
     */
    public boolean actualizarSala(String numeroActual, String numeroNuevo) {
        Sala sala = obtenerSala(numeroActual);
        if (sala == null || numeroNuevo == null || obtenerSala(numeroNuevo) != null) {
            return false;
        }
        sala.setNumeroSala(numeroNuevo);
        return true;
    }

    /**
     * Metodo para eliminar una sala por numero
     * @return true si se elimino, false si no existe
     */
    public boolean eliminarSala(String numeroSala) {
        Sala sala = obtenerSala(numeroSala);
        return sala != null && listSalas.remove(sala);
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
