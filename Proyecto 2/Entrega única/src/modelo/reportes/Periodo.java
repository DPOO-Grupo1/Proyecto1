package modelo.reportes;

import java.time.LocalDate;

public enum Periodo {
    DIARIO,
    SEMANAL,
    MENSUAL;

    // primer día del rango que empieza en "desde"
    public LocalDate inicio(LocalDate desde) {
        return desde;
    }

    // último día del rango (inclusive)
    public LocalDate fin(LocalDate desde) {
        switch (this) {
            case DIARIO:
                return desde;
            case SEMANAL:
                return desde.plusDays(6);
            default:
                return desde.plusMonths(1).minusDays(1);
        }
    }
}