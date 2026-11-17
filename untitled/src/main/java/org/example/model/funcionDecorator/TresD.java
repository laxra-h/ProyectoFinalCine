package org.example.model.funcionDecorator;

public class TresD extends DecoratorFuncion {


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
        return funcion.calcularCosto() + 6000;
    }
}
