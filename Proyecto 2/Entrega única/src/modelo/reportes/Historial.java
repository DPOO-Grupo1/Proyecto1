package modelo.reportes;

import java.util.ArrayList;
import java.util.List;

import modelo.academias.PartidoPractica;
import modelo.academias.Reserva;
import modelo.entrenamiento.SesionEntrenamiento;
import modelo.partidos.Partido;

public class Historial {
    private List<Partido> partidos;
    private List<Reserva> reservas;
    private List<SesionEntrenamiento> sesiones;
    private List<PartidoPractica> partidosPractica;

    public Historial() {
        this.partidos = new ArrayList<Partido>();
        this.reservas = new ArrayList<Reserva>();
        this.sesiones = new ArrayList<SesionEntrenamiento>();
        this.partidosPractica = new ArrayList<PartidoPractica>();
    }

    public void agregarPartido(Partido p) { partidos.add(p); }
    public void agregarReserva(Reserva r) { reservas.add(r); }
    public void agregarSesion(SesionEntrenamiento s) { sesiones.add(s); }
    public void agregarPartidoPractica(PartidoPractica p) { partidosPractica.add(p); }

    public List<Partido> getPartidos() { return partidos; }
    public List<Reserva> getReservas() { return reservas; }
    public List<SesionEntrenamiento> getSesiones() { return sesiones; }
    public List<PartidoPractica> getPartidosPractica() { return partidosPractica; }
}