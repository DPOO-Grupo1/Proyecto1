package logica;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import excepciones.CoberturaTurnosException;
import excepciones.ConflictoHorarioException;
import excepciones.DatoInvalidoException;
import excepciones.EstadoInvalidoException;
import excepciones.PermisoDenegadoException;
import modelo.personal.SolicitudCambioTurno;
import modelo.personal.Sugerencia;
import modelo.personal.Turno;
import modelo.usuarios.Empleado;
import modelo.usuarios.Entrenador;
import modelo.usuarios.Fisioterapeuta;
import modelo.usuarios.Usuario;

/**
 * Maneja los turnos, las solicitudes de cambio de turno y las sugerencias de productos.
 * Cubre RF18, RF19, RF21, RF32 y RF33.
 */
public class GestorPersonal extends Gestor {

    public GestorPersonal(Club club) {
        super(club);
    }

    //-----------RF18, RF19: solicitar cambio de turno
    public SolicitudCambioTurno solicitarCambio(Usuario quien, Turno turno, DayOfWeek nuevoDia,
            LocalTime inicio, LocalTime fin)
            throws PermisoDenegadoException, DatoInvalidoException, EstadoInvalidoException {
        verificarEmpleado(quien);

        if (turno.getEmpleado() != quien) {
            throw new PermisoDenegadoException("Solo puede pedir cambio de sus propios turnos");
        }
        if (!club.getHorario().contiene(nuevoDia, inicio, fin)) {
            throw new DatoInvalidoException("El nuevo horario queda fuera del horario de atención del club");
        }
        if (turno.tieneSolicitudPendiente()) {
            throw new EstadoInvalidoException("Este turno ya tiene una solicitud de cambio pendiente");
        }

        SolicitudCambioTurno solicitud = new SolicitudCambioTurno(turno, club.getFechaActual(),
                nuevoDia, inicio, fin);
        turno.agregarSolicitud(solicitud);
        return solicitud;
    }

    //-----------RF19: consultar mis solicitudes
    public List<SolicitudCambioTurno> consultarSolicitudes(Usuario quien)
            throws PermisoDenegadoException {
        verificarEmpleado(quien);

        List<SolicitudCambioTurno> resultado = new ArrayList<SolicitudCambioTurno>();
        for (Turno t : ((Empleado) quien).getTurnos()) {
            resultado.addAll(t.getSolicitudes());
        }
        return resultado;
    }

    //-----------RF21: sugerir producto
    public Sugerencia sugerirProducto(Usuario quien, String nombre, String tipo, String descripcion)
            throws PermisoDenegadoException, DatoInvalidoException {
        verificarEmpleado(quien);

        if (nombre == null || nombre.trim().isEmpty() || descripcion == null || descripcion.trim().isEmpty()) {
            throw new DatoInvalidoException("El nombre y la descripción de la sugerencia no pueden estar vacíos");
        }

        Empleado empleado = (Empleado) quien;
        Sugerencia sugerencia = new Sugerencia(empleado, nombre, tipo, descripcion, club.getFechaActual());
        club.agregarSugerencia(sugerencia);
        empleado.agregarSugerencia(sugerencia);
        return sugerencia;
    }

    //-----------RF32: resolver sugerencia
    public void resolverSugerencia(Usuario quien, Sugerencia sugerencia, boolean aprobar, String motivo)
            throws PermisoDenegadoException, EstadoInvalidoException, DatoInvalidoException {
        verificarAdministrador(quien);

        if (!sugerencia.estaPendiente()) {
            throw new EstadoInvalidoException("La sugerencia ya fue resuelta");
        }
        if (aprobar) {
            sugerencia.aprobar();   // el producto se registra después con GestorTienda.registrarProducto
        } else {
            if (motivo == null || motivo.trim().isEmpty()) {
                throw new DatoInvalidoException("Debe dar un motivo para rechazar la sugerencia");
            }
            sugerencia.rechazar(motivo);
        }
    }

    //-----------RF33: publicar turnos
    public void publicarTurnos(Usuario quien, List<Turno> turnos)
            throws PermisoDenegadoException, ConflictoHorarioException, CoberturaTurnosException {
        verificarAdministrador(quien);

        verificarSinSolapes(turnos);
        verificarCobertura(turnos);

        club.setTurnos(turnos);
        for (Turno t : turnos) {
            Empleado e = t.getEmpleado();
            if (!e.getTurnos().contains(t)) {
                e.agregarTurno(t);
            }
        }
    }

    //-----------RF33: resolver solicitud de cambio de turno
    public void resolverSolicitud(Usuario quien, SolicitudCambioTurno solicitud, boolean aprobar)
            throws PermisoDenegadoException, EstadoInvalidoException, CoberturaTurnosException,
            ConflictoHorarioException {
        verificarAdministrador(quien);

        if (!solicitud.estaPendiente()) {
            throw new EstadoInvalidoException("La solicitud ya fue resuelta");
        }

        if (!aprobar) {
            solicitud.rechazar();
            return;
        }

        // copia de los turnos con el cambio aplicado, para revisar antes de modificar de verdad
        List<Turno> copia = new ArrayList<Turno>();
        for (Turno t : club.getTurnos()) {
            if (t == solicitud.getTurno()) {
                copia.add(new Turno(t.getId(), t.getEmpleado(), solicitud.getNuevoDia(),
                        solicitud.getNuevaHoraInicio(), solicitud.getNuevaHoraFin()));
            } else {
                copia.add(t);
            }
        }
        verificarSinSolapes(copia);
        verificarCobertura(copia);

        solicitud.getTurno().cambiar(solicitud.getNuevoDia(), solicitud.getNuevaHoraInicio(),
                solicitud.getNuevaHoraFin());
        solicitud.aprobar();
    }

    //-----------privados
    private void verificarEmpleado(Usuario quien) throws PermisoDenegadoException {
        if (!(quien instanceof Entrenador) && !(quien instanceof Fisioterapeuta)) {
            throw new PermisoDenegadoException("Solo un entrenador o fisioterapeuta puede hacer esto");
        }
    }

    // Ningún empleado puede tener dos turnos que se solapen
    private void verificarSinSolapes(List<Turno> turnos) throws ConflictoHorarioException {
        for (int i = 0; i < turnos.size(); i++) {
            for (int j = i + 1; j < turnos.size(); j++) {
                Turno a = turnos.get(i);
                Turno b = turnos.get(j);
                if (a.getEmpleado() == b.getEmpleado() && a.seSolapaCon(b)) {
                    throw new ConflictoHorarioException("El empleado " + a.getEmpleado().getLogin()
                            + " tiene dos turnos que se solapan el " + a.getDia());
                }
            }
        }
    }

    // En cada bloque del horario: mínimo 2 entrenadores y 1 fisioterapeuta
    private void verificarCobertura(List<Turno> turnos) throws CoberturaTurnosException {
        for (DayOfWeek dia : club.getHorario().getDias()) {
            for (LocalTime bloque : club.getHorario().getBloques()) {
                int entrenadores = 0;
                int fisios = 0;
                for (Turno t : turnos) {
                    if (t.cubre(dia, bloque)) {
                        if (t.getEmpleado() instanceof Entrenador) {
                            entrenadores++;
                        } else if (t.getEmpleado() instanceof Fisioterapeuta) {
                            fisios++;
                        }
                    }
                }
                if (entrenadores < 2 || fisios < 1) {
                    throw new CoberturaTurnosException("El " + dia + " a las " + bloque
                            + " no hay cobertura mínima (2 entrenadores y 1 fisioterapeuta): hay "
                            + entrenadores + " entrenador(es) y " + fisios + " fisioterapeuta(s)");
                }
            }
        }
    }
}