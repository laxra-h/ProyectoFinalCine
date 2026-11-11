package org.example.model.funcionDecorator;

public abstract class DecoratorFuncion implements Funcion{

    protected final Funcion funcion;

    /**
     * Metodo constructor del Decorator
     * @param funcion
     */
    public DecoratorFuncion(Funcion funcion) {
        this.funcion = funcion;
    }


    public Funcion getFuncion() {
        return funcion;
    }
}
