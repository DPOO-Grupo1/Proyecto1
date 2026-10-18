package modelo.reportes;

import java.time.LocalDate;

import modelo.academias.PartidoPractica;
import modelo.academias.Reserva;
import modelo.deportes.Equipo;
import modelo.entrenamiento.SesionEntrenamiento;
import modelo.partidos.Partido;

public class FiltroHistorial {
    private LocalDate desde;   // null = sin límite
    private LocalDate hasta;   // null = sin límite
    private Equipo equipo;     // null = cualquier equipo (solo aplica a partidos)

    public FiltroHistorial(LocalDate desde, LocalDate hasta, Equipo equipo) {
        this.desde = desde;
        this.hasta = hasta;
        this.equipo = equipo;
    }

    private boolean fechaSirve(LocalDate fecha) {
        if (desde != null && fecha.isBefore(desde)) return false;
        if (hasta != null && fecha.isAfter(hasta)) return false;
        return true;
    }

    public boolean acepta(Partido partido) {
        if (!fechaSirve(partido.getFecha())) return false;
        if (equipo != null && !partido.participa(equipo)) return false;
        return true;
    }

    public boolean acepta(Reserva reserva) {
        return equipo == null && fechaSirve(reserva.getFecha());
    }

    public boolean acepta(SesionEntrenamiento sesion) {
        if (!fechaSirve(sesion.getFecha())) return false;
        return equipo == null || sesion.getEquipo() == equipo;
    }

    public boolean acepta(PartidoPractica partido) {
        if (equipo != null || partido.getReserva() == null) return false;
        return fechaSirve(partido.getReserva().getFecha());
    }
}