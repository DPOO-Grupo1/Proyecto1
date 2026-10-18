package modelo.personal;

import java.time.LocalDate;

import modelo.usuarios.Empleado;

public class Sugerencia {
    private Empleado empleado;
    private String nombreProducto;
    private String tipo;
    private String descripcion;
    private LocalDate fecha;
    private EstadoSolicitud estado;
    private String motivoRechazo;

    public Sugerencia(Empleado empleado, String nombreProducto, String tipo, String descripcion, LocalDate fecha) {
        this.empleado = empleado;
        this.nombreProducto = nombreProducto;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.estado = EstadoSolicitud.PENDIENTE;
        this.motivoRechazo = null;
    }

    public Empleado getEmpleado() { return empleado; }
    public String getNombreProducto() { return nombreProducto; }
    public String getTipo() { return tipo; }
    public String getDescripcion() { return descripcion; }
    public LocalDate getFecha() { return fecha; }
    public EstadoSolicitud getEstado() { return estado; }
    public String getMotivoRechazo() { return motivoRechazo; }

    public boolean estaPendiente() {
        return estado == EstadoSolicitud.PENDIENTE;
    }

    public void aprobar() {
        this.estado = EstadoSolicitud.APROBADA;
    }

    public void rechazar(String motivo) {
        this.estado = EstadoSolicitud.RECHAZADA;
        this.motivoRechazo = motivo;
    }
}