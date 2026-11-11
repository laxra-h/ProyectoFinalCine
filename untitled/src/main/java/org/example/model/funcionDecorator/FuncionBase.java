package org.example.model.funcionDecorator;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

public class FuncionBase implements Funcion {

    private String codigo;
    private LocalDate fecha;
    private LocalTime hora;
    private final int DESCUENTO_SEMANA = 40;
    private final double COSTO = 20000;
    private EstadoFuncion estado;


    /**
     * Metodo constructor de la funcionBase
     * @param codigo
     * @param fecha
     * @param hora
     * @param estado
     */
    public FuncionBase(String codigo, LocalDate fecha, LocalTime hora, EstadoFuncion estado) {
        this.codigo = codigo;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = estado;
    }



    /**
     * Metodo para calcular el costo de la funcion base si es fin de semana o dia entre semana
     * Se utiliza un getter pues solo debe devolver el costo base
     * @return
     */
    @Override
    public double calcularCosto() {
        DayOfWeek dia = LocalDate.now().getDayOfWeek();
        if (dia == DayOfWeek.SATURDAY || dia == DayOfWeek.SUNDAY) {
            return COSTO;
        } else {
            return calcularCosto();
        }
    }

    /**
     * Metodo que calcula el costo con promocion de semana
     * @return
     */
    public double calcularCostoSemana() {
        return (COSTO * DESCUENTO_SEMANA) / 100;
    }
}
