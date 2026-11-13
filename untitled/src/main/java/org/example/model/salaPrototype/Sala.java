package org.example.model.salaPrototype;

public class Sala {

    private String numeroSala;
    private Asiento[] listAsientos;
    private final int numeroFilas = 8;
    private final int numeroColumnas = 12;

    /**
     * Metodo constructor de la Sala
     * @param numeroSala
     */
    public Sala(String numeroSala) {
        this.numeroSala = numeroSala;
        this.listAsientos= new Asiento[numeroFilas*numeroColumnas];
    }


    /**
     * Metodo para crear los asientos de la sala de manera automatica con el patron prototype
     * @throws CloneNotSupportedException
     */
    public void creacionAsientos() throws CloneNotSupportedException {

        Asiento asientoBase = new Asiento('A', 1);

        int posicion = 0;

        for (int i = 0; i < numeroFilas; i++) {

            for (int j = 1; j <= numeroColumnas; j++) {

                Asiento nuevoAsiento = asientoBase.clone();

                nuevoAsiento.setFila((char) ('A' + i));
                nuevoAsiento.setNumero((char)j);
                nuevoAsiento.setDisponible(true);

                listAsientos[posicion] = nuevoAsiento;

                posicion++;
            }
        }
    }


    public String getNumeroSala() {
        return numeroSala;
    }

    public void setNumeroSala(String numeroSala) {
        this.numeroSala = numeroSala;
    }

    public Asiento[] getListAsientos() {
        return listAsientos;
    }

    public int getNumeroFilas() {
        return numeroFilas;
    }

    public int getNumeroColumnas() {
        return numeroColumnas;
    }
}
