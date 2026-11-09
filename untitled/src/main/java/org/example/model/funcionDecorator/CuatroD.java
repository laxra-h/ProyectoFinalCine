package org.example.model.funcionDecorator;

public class CuatroD extends DecoratorFuncion {

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
    return funcion.calcularCosto() + 12000;
    }
}
