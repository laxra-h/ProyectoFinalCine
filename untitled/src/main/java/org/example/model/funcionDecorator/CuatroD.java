package org.example.model.funcionDecorator;

public class CuatroD extends DecoratorFuncion {

    private static final double RECARGO = 12000;

    /**
     * Metodo para constructor de la calse cuatroD
     * @param funcion
     */
    public CuatroD(Funcion funcion) {
        super(funcion);
    }

    /**
     * Metodo para calcular el costo de una funcion 4D
     * @return
     */
    @Override
    public double calcularCosto() {
        return funcion.calcularCosto() + RECARGO;
    }

    /**
     * Metodo para obtener los puntos de la funcion 4D
     * @return
     */
    @Override
    public int getPuntosRedmidos() {
        return funcion.getPuntosRedmidos() + (int) (RECARGO / FuncionBase.VALOR_PUNTO);
    }
}
