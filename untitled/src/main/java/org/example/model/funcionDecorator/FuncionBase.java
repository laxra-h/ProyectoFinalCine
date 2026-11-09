package org.example.model.funcionDecorator;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

public class FuncionBase implements Funcion {

    private LocalDate fecha;
    private LocalTime hora;
    private final int DESCUENTO_SEMANA = 40;
    private final double COSTO = 20000;

    /**
     * Metodo constuctor para la funcion base
     * @param fecha
     * @param hora
     */
    public FuncionBase(LocalDate fecha, LocalTime hora) {
        this.fecha = fecha;
        this.hora = hora;
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
