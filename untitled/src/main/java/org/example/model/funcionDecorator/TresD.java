package org.example.model.funcionDecorator;

public class TresD extends DecoratorFuncion {

    private static final double RECARGO = 6000;

    /**
     * Metodo constructor para la clase TresD
     * @param funcion
     */
    public TresD(Funcion funcion) {
        super(funcion);
    }

    /**
     * Metodo para calcular el valor de una funcion 3D
     * @return
     */
    @Override
    public double calcularCosto() {
        return funcion.calcularCosto() + RECARGO;
    }

    /**
     * Metodo para obtener los puntos de la funcion 3D
     * @return
     */
    @Override
    public int getPuntosRedmidos() {
        return funcion.getPuntosRedmidos() + (int) (RECARGO / FuncionBase.VALOR_PUNTO);
    }
}
