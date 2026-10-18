package modelo.reportes;

import java.time.LocalDate;

import modelo.academias.PartidoPractica;
import modelo.academias.Reserva;
import modelo.deportes.Categoria;
import modelo.deportes.DeporteEquipo;
import modelo.deportes.Equipo;
import modelo.entrenamiento.SesionEntrenamiento;
import modelo.partidos.Partido;
import modelo.usuarios.Entrenador;
import modelo.usuarios.Socio;

public class FiltroHistorial {
    private LocalDate desde;
    private LocalDate hasta;
    private Equipo equipo;
    private Socio socio;
    private Entrenador entrenador;
    private DeporteEquipo deporte;
    private Categoria categoria;

    // Filtro solo por fechas y equipo
    public FiltroHistorial(LocalDate desde, LocalDate hasta, Equipo equipo) {
        this(desde, hasta, equipo, null, null, null, null);
    }

    public FiltroHistorial(LocalDate desde, LocalDate hasta, Equipo equipo, Socio socio,
            Entrenador entrenador, DeporteEquipo deporte, Categoria categoria) {
        this.desde = desde;
        this.hasta = hasta;
        this.equipo = equipo;
        this.socio = socio;
        this.entrenador = entrenador;
        this.deporte = deporte;
        this.categoria = categoria;
    }

    private boolean fechaSirve(LocalDate fecha) {
        if (desde != null && fecha.isBefore(desde)) return false;
        if (hasta != null && fecha.isAfter(hasta)) return false;
        return true;
    }

    // true si el socio filtrado juega en alguno de los dos equipos
    private boolean socioEnEquipo(Equipo e) {
        for (modelo.deportes.JugadorEquipo j : e.getJugadores()) {
            if (j.getSocio() == socio) return true;
        }
        return false;
    }

    public boolean acepta(Partido partido) {
        if (!fechaSirve(partido.getFecha())) return false;
        if (equipo != null && !partido.participa(equipo)) return false;
        if (entrenador != null) return false;   // los partidos no tienen entrenador propio
        if (deporte != null || categoria != null || socio != null) {
            boolean alguno = false;
            for (Equipo e : equiposDe(partido)) {
                boolean ok = true;
                if (deporte != null && e.getDeporte() != deporte) ok = false;
                if (categoria != null && e.getCategoria() != categoria) ok = false;
                if (socio != null && !socioEnEquipo(e)) ok = false;
                if (ok) alguno = true;
            }
            if (!alguno) return false;
        }
        return true;
    }

    // Equipos del club que juegan el partido
    private java.util.List<Equipo> equiposDe(Partido partido) {
        java.util.List<Equipo> lista = new java.util.ArrayList<Equipo>();
        if (partido instanceof modelo.partidos.PartidoInterno) {
            modelo.partidos.PartidoInterno pi = (modelo.partidos.PartidoInterno) partido;
            lista.add(pi.getLocal());
            lista.add(pi.getVisitante());
        } else if (partido instanceof modelo.partidos.PartidoCompetencia) {
            modelo.partidos.PartidoCompetencia pc = (modelo.partidos.PartidoCompetencia) partido;
            if (pc.getLocal() instanceof modelo.competencias.EquipoInscrito) {
                lista.add(((modelo.competencias.EquipoInscrito) pc.getLocal()).getEquipo());
            }
            if (pc.getVisitante() instanceof modelo.competencias.EquipoInscrito) {
                lista.add(((modelo.competencias.EquipoInscrito) pc.getVisitante()).getEquipo());
            }
        }
        return lista;
    }

    public boolean acepta(Reserva reserva) {
        if (equipo != null || deporte != null || categoria != null) return false;
        if (entrenador != null) return false;
        if (socio != null && reserva.getTitular() != socio
                && !(reserva.getPartido() != null && reserva.getPartido().tieneParticipante(socio))) return false;
        return fechaSirve(reserva.getFecha());
    }

    public boolean acepta(SesionEntrenamiento sesion) {
        if (!fechaSirve(sesion.getFecha())) return false;
        if (equipo != null && sesion.getEquipo() != equipo) return false;
        if (entrenador != null && sesion.getEntrenador() != entrenador) return false;
        if (socio != null && !sesion.tieneConvocado(socio)) return false;
        if (deporte != null && (sesion.getEquipo() == null || sesion.getEquipo().getDeporte() != deporte)) return false;
        if (categoria != null && (sesion.getEquipo() == null || sesion.getEquipo().getCategoria() != categoria)) return false;
        return true;
    }

    public boolean acepta(PartidoPractica partido) {
        if (equipo != null || deporte != null || categoria != null || entrenador != null) return false;
        if (partido.getReserva() == null) return false;
        if (socio != null && !partido.tieneParticipante(socio)) return false;
        return fechaSirve(partido.getReserva().getFecha());
    }
}