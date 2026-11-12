package modelo.reportes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReporteDeportivo {
    private LocalDate desde;
    private LocalDate hasta;
    private List<String> resultados;

    public ReporteDeportivo(LocalDate desde, LocalDate hasta) {
        this.desde = desde;
        this.hasta = hasta;
        this.resultados = new ArrayList<String>();
    }

    public void agregarResultado(String resultado) {
        resultados.add(resultado);
    }

    public LocalDate getDesde() { return desde; }
    public LocalDate getHasta() { return hasta; }
    public List<String> getResultados() { return resultados; }
}
