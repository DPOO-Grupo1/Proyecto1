package modelo.personal;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

public class SolicitudCambioTurno {
    private Turno turno;
    private LocalDate fechaSolicitud;
    private DayOfWeek nuevoDia;
    private LocalTime nuevaHoraInicio;
    private LocalTime nuevaHoraFin;
    private EstadoSolicitud estado;

    public SolicitudCambioTurno(Turno turno, LocalDate fechaSolicitud, DayOfWeek nuevoDia,
            LocalTime nuevaHoraInicio, LocalTime nuevaHoraFin) {
        this.turno = turno;
        this.fechaSolicitud = fechaSolicitud;
        this.nuevoDia = nuevoDia;
        this.nuevaHoraInicio = nuevaHoraInicio;
        this.nuevaHoraFin = nuevaHoraFin;
        this.estado = EstadoSolicitud.PENDIENTE;
    }

    public Turno getTurno() { return turno; }
    public LocalDate getFechaSolicitud() { return fechaSolicitud; }
    public DayOfWeek getNuevoDia() { return nuevoDia; }
    public LocalTime getNuevaHoraInicio() { return nuevaHoraInicio; }
    public LocalTime getNuevaHoraFin() { return nuevaHoraFin; }
    public EstadoSolicitud getEstado() { return estado; }

    public boolean estaPendiente() {
        return estado == EstadoSolicitud.PENDIENTE;
    }

    public void aprobar() {
        this.estado = EstadoSolicitud.APROBADA;
    }

    public void rechazar() {
        this.estado = EstadoSolicitud.RECHAZADA;
    }
}
