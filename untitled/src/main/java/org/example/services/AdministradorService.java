package org.example.services;

import org.example.model.funcionDecorator.EstadoFuncion;
import org.example.model.funcionDecorator.Funcion;

public class AdministradorService {

public void cambiarEstado(Funcion funcion, EstadoFuncion estado){

    funcion.setEstado(estado);

}

}
