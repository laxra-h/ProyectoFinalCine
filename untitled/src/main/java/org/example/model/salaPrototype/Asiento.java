package org.example.model.salaPrototype;

public class Asiento implements Cloneable{

    private char fila;
    private char numero;

    /**
     * Metodo constructor del asiento
     * @param fila
     * @param numero
     */
    public Asiento(char fila, char numero) {
        this.fila = fila;
        this.numero = numero;
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




    public char getFila() {
        return fila;
    }

    public void setFila(char fila) {
        this.fila = fila;
    }

    public char getNumero() {
        return numero;
    }

    public void setNumero(char numero) {
        this.numero = numero;
    }
}
