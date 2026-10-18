package modelo.reportes;

import java.time.LocalDate;
import java.time.YearMonth;

public class Mensualidad {
    private YearMonth periodo;
    private double valor;
    private LocalDate fechaPago;
    private boolean pagada;

    public Mensualidad(YearMonth periodo, double valor, LocalDate fechaPago) {
        this.periodo = periodo;
        this.valor = valor;
        this.fechaPago = fechaPago;
        this.pagada = true;
    }

    public YearMonth getPeriodo() {
        return periodo;
    }

    public double getValor() {
        return valor;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public boolean isPagada() {
        return pagada;
    }
}