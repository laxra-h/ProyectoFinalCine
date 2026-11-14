package org.example.model.usuarios;

public class TarjetaVirtual {

    private String numero;
    private double saldo;
    private boolean aciva;


    /**
     * Metodo constructor
     * @param numero
     * @param saldo
     */
    public TarjetaVirtual(String numero, double saldo) {
        this.numero = numero;
        this.saldo = saldo;
        this.aciva = true;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public boolean isAciva() {
        return aciva;
    }

    public void setAciva(boolean aciva) {
        this.aciva = aciva;
    }
}
