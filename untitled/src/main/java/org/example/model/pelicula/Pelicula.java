package org.example.model.pelicula;

public class Pelicula {

    private String nombre;
    private String director;
    private String descripcion;
    private ClasificacionPelicula clasificacion;
    private GeneroPelicula genero;
    private int duracionMin;


    /**
     * Metodo constructor de pelicula
     * @param nombre
     * @param director
     * @param descripcion
     * @param clasificacion
     * @param genero
     * @param duracionMin
     */
    public Pelicula(String nombre, String director, String descripcion, ClasificacionPelicula clasificacion, GeneroPelicula genero, int duracionMin) {
        this.nombre = nombre;
        this.director = director;
        this.descripcion = descripcion;
        this.clasificacion = clasificacion;
        this.genero = genero;
        this.duracionMin = duracionMin;
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public ClasificacionPelicula getClasificacion() {
        return clasificacion;
    }

    public void setClasificacion(ClasificacionPelicula clasificacion) {
        this.clasificacion = clasificacion;
    }

    public GeneroPelicula getGenero() {
        return genero;
    }

    public void setGenero(GeneroPelicula genero) {
        this.genero = genero;
    }

    public int getDuracionMin() {
        return duracionMin;
    }

    public void setDuracionMin(int duracionMin) {
        this.duracionMin = duracionMin;
    }
}
