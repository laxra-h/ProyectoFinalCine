package org.example.model;

public final class Cine {


    private static Cine instance;
    private String nombre;
    private String nit;
    private String paginaWeb;

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
