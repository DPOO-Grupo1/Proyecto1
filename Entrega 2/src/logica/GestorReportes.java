package logica;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import excepciones.DatoInvalidoException;
import excepciones.DuplicadoException;
import excepciones.NoEncontradoException;
import excepciones.PermisoDenegadoException;
import modelo.academias.Disciplina;
import modelo.academias.Inscripcion;
import modelo.academias.PartidoPractica;
import modelo.academias.Reserva;
import modelo.competencias.Competencia;
import modelo.entrenamiento.Convocatoria;
import modelo.entrenamiento.EstadoAsistencia;
import modelo.entrenamiento.SesionEntrenamiento;
import modelo.partidos.Partido;
import modelo.partidos.PartidoCompetencia;
import modelo.partidos.PartidoInterno;
import modelo.reportes.FiltroHistorial;
import modelo.reportes.Historial;
import modelo.reportes.Mensualidad;
import modelo.reportes.Periodo;
import modelo.reportes.ReporteDeportivo;
import modelo.reportes.ReporteFinanciero;
import modelo.tienda.Venta;
import modelo.tienda.VentaCafeteria;
import modelo.tienda.VentaTienda;
import modelo.usuarios.Socio;
import modelo.usuarios.Usuario;

/**
 * Maneja los reportes del administrador y el registro de mensualidades.
 * Cubre RF28, RF34 y RF35.
 */
public class GestorReportes extends Gestor {

    public GestorReportes(Club club) {
        super(club);
    }

    //-----------RF35: registrar mensualidad
    public Mensualidad registrarPago(Usuario quien, String socioID, YearMonth periodo, double valor)
            throws PermisoDenegadoException, NoEncontradoException, DuplicadoException, DatoInvalidoException {
        verificarAdministrador(quien);

        Socio socio = club.buscarSocio(socioID);

        if (socio.tieneMensualidadPagada(periodo)) {
            throw new DuplicadoException("El socio " + socioID + " ya pagó la mensualidad de " + periodo);
        }
        if (valor <= 0) {
            throw new DatoInvalidoException("El valor de la mensualidad debe ser mayor a 0");
        }

        Mensualidad mensualidad = new Mensualidad(periodo, valor, club.getFechaActual());
        socio.agregarMensualidad(mensualidad);
        return mensualidad;
    }
    
    //-----------RF28: historial global
    public Historial consultarHistorial(Usuario quien, FiltroHistorial filtro)
            throws PermisoDenegadoException {
        verificarAdministrador(quien);

        Historial historial = new Historial();

        for (PartidoInterno p : club.getPartidosInternos()) {
            if (filtro.acepta(p)) {
                historial.agregarPartido(p);
            }
        }
        for (Competencia c : club.getCompetencias()) {
            for (PartidoCompetencia p : c.getPartidos()) {
                if (filtro.acepta(p)) {
                    historial.agregarPartido(p);
                }
            }
        }
        for (Reserva r : club.getReservas()) {
            if (filtro.acepta(r)) {
                historial.agregarReserva(r);
            }
        }
        for (SesionEntrenamiento s : club.getSesiones()) {
            if (filtro.acepta(s)) {
                historial.agregarSesion(s);
            }
        }
        for (PartidoPractica pp : club.getPartidosPractica()) {
            if (filtro.acepta(pp)) {
                historial.agregarPartidoPractica(pp);
            }
        }

        return historial;
    }

    //-----------RF34: reporte financiero
    public ReporteFinanciero generarReporteFinanciero(Usuario quien, Periodo periodo, LocalDate desde)
            throws PermisoDenegadoException {
        verificarAdministrador(quien);

        LocalDate inicio = periodo.inicio(desde);
        LocalDate fin = periodo.fin(desde);

        double subTienda = 0, impTienda = 0, totTienda = 0;
        double subCafe = 0, impCafe = 0, totCafe = 0;

        for (Venta v : club.getVentas()) {
            if (!enRango(v.getFechaHora().toLocalDate(), inicio, fin)) {
                continue;
            }
            if (v instanceof VentaTienda) {
                subTienda += v.getSubtotal();
                impTienda += v.calcularImpuesto();
                totTienda += v.getMontoTotal();
            } else if (v instanceof VentaCafeteria) {
                subCafe += v.getSubtotal();
                impCafe += v.calcularImpuesto();
                totCafe += v.getMontoTotal();
            }
        }

        double totMensualidades = 0;
        for (Usuario u : club.getUsuarios()) {
            if (u instanceof Socio) {
                for (Mensualidad m : ((Socio) u).getMensualidades()) {
                    if (m.isPagada() && enRango(m.getFechaPago(), inicio, fin)) {
                        totMensualidades += m.getValor();
                    }
                }
            }
        }

        return new ReporteFinanciero(inicio, fin, subTienda, impTienda, totTienda,
                subCafe, impCafe, totCafe, totMensualidades);
    }

    //-----------RF34: reporte deportivo
    public ReporteDeportivo generarReporteDeportivo(Usuario quien, Periodo periodo, LocalDate desde)
            throws PermisoDenegadoException {
        verificarAdministrador(quien);

        LocalDate inicio = periodo.inicio(desde);
        LocalDate fin = periodo.fin(desde);
        ReporteDeportivo reporte = new ReporteDeportivo(inicio, fin);

        List<Partido> partidos = new ArrayList<Partido>();
        partidos.addAll(club.getPartidosInternos());
        for (Competencia c : club.getCompetencias()) {
            partidos.addAll(c.getPartidos());
        }
        for (Partido p : partidos) {
            if (p.isJugado() && enRango(p.getFecha(), inicio, fin)) {
                reporte.agregarResultado("Partido #" + p.getId() + " (" + p.getFecha() + "): "
                        + p.getMarcadorLocal() + " - " + p.getMarcadorVisitante());
            }
        }
        
        //partidos de práctica
        for (PartidoPractica pp : club.getPartidosPractica()) {
            if (pp.isJugado() && pp.getReserva() != null && enRango(pp.getReserva().getFecha(), inicio, fin)) {
                reporte.agregarResultado("Práctica " + pp.getId() + " (" + pp.getReserva().getFecha()
                        + "): ganó " + pp.getGanador().getLogin());
            }
        }
        
        // asistencia a entrenamientos del rango
        int asistio = 0, falto = 0, tarde = 0;
        for (SesionEntrenamiento s : club.getSesiones()) {
            if (enRango(s.getFecha(), inicio, fin)) {
                for (Convocatoria c : s.getConvocatorias()) {
                    if (c.getAsistencia() == EstadoAsistencia.ASISTIO) {
                        asistio++;
                    } else if (c.getAsistencia() == EstadoAsistencia.FALTO) {
                        falto++;
                    } else if (c.getAsistencia() == EstadoAsistencia.TARDE) {
                        tarde++;
                    }
                }
            }
        }
        reporte.agregarResultado("Asistencia a entrenamientos: " + asistio + " asistieron, "
                + falto + " faltaron, " + tarde + " llegaron tarde");

        // ranking ELO actual por disciplina
        for (Disciplina d : club.getDisciplinas()) {
            for (Usuario u : club.getUsuarios()) {
                if (u instanceof Socio) {
                    Socio socio = (Socio) u;
                    Inscripcion ins = socio.getInscripcion(d);
                    if (ins != null) {
                        reporte.agregarResultado("ELO " + d.getNombre() + " - " + socio.getLogin()
                                + ": " + ins.getPuntajeELO());
                    }
                }
            }
        }

        return reporte;
    }

    //-----------auxiliar privado
    private boolean enRango(LocalDate fecha, LocalDate inicio, LocalDate fin) {
        return !fecha.isBefore(inicio) && !fecha.isAfter(fin);
    }

}