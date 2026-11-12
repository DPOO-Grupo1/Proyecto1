package modelo.reportes;

import java.time.LocalDate;

public class ReporteFinanciero {
    private LocalDate desde;
    private LocalDate hasta;
    private double subtotalTienda, impuestoTienda, totalTienda;
    private double subtotalCafeteria, impuestoCafeteria, totalCafeteria;
    private double totalMensualidades;

    public ReporteFinanciero(LocalDate desde, LocalDate hasta,
            double subtotalTienda, double impuestoTienda, double totalTienda,
            double subtotalCafeteria, double impuestoCafeteria, double totalCafeteria,
            double totalMensualidades) {
        this.desde = desde;
        this.hasta = hasta;
        this.subtotalTienda = subtotalTienda;
        this.impuestoTienda = impuestoTienda;
        this.totalTienda = totalTienda;
        this.subtotalCafeteria = subtotalCafeteria;
        this.impuestoCafeteria = impuestoCafeteria;
        this.totalCafeteria = totalCafeteria;
        this.totalMensualidades = totalMensualidades;
    }

    public LocalDate getDesde() { return desde; }
    public LocalDate getHasta() { return hasta; }
    public double getSubtotalTienda() { return subtotalTienda; }
    public double getImpuestoTienda() { return impuestoTienda; }
    public double getTotalTienda() { return totalTienda; }
    public double getSubtotalCafeteria() { return subtotalCafeteria; }
    public double getImpuestoCafeteria() { return impuestoCafeteria; }
    public double getTotalCafeteria() { return totalCafeteria; }
    public double getTotalMensualidades() { return totalMensualidades; }
}