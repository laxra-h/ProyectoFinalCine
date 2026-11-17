package org.example.model.salaPrototype;

public class Asiento implements Cloneable {

    private char fila;
    private int numero;
    private boolean disponible;

    /**
     * Metodo constructor del asiento
     * @param fila
     * @param numero
     */
    public Asiento(char fila, int numero) {
        this.fila = fila;
        this.numero = numero;
        this.disponible = true;
    }

    /**
     * Metodo para clonar el asiento
     * @return
     * @throws CloneNotSupportedException
     */
    @Override
    public Asiento clone() throws CloneNotSupportedException {
        return (Asiento) super.clone();
    }

    public String getCodigo() {
        return "" + fila + numero;
    }

    public char getFila() {
        return fila;
    }

    public void setFila(char fila) {
        this.fila = fila;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
}
