package modelo.academias;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import excepciones.DatoInvalidoException;
import excepciones.EstadoInvalidoException;
import excepciones.PermisoDenegadoException;
import excepciones.ReglaDeportivaException;
import modelo.usuarios.Socio;
import modelo.usuarios.Usuario;

/**
 * Partido de práctica jugado sobre una reserva de instalación.
 * Registra a los participantes, valida y guarda estadísticas y actualiza el ELO (RF9, RF10).
 */
public class PartidoPractica {

	private String id;
	private Reserva reserva;
	private List<Socio> participantes;
	private boolean cancelado;
	private boolean jugado;
	private Socio ganador;
	private Map<Socio, EstadisticaPractica> estadisticas;

	public PartidoPractica(String id, Reserva reserva) {
		this(id, reserva, new ArrayList<Socio>());
	}

	public PartidoPractica(String id, Reserva reserva, List<Socio> participantes) {
		this.id = id;
		this.reserva = reserva;
		this.participantes = new ArrayList<Socio>(participantes);
		this.cancelado = false;
		this.jugado = false;
		this.ganador = null;
		this.estadisticas = new HashMap<Socio, EstadisticaPractica>();
		if (reserva != null) {
			reserva.setPartido(this);
		}
	}

	public String getId() {
		return id;
	}

	public Reserva getReserva() {
		return reserva;
	}

	public List<Socio> getParticipantes() {
		return participantes;
	}

	public void agregarParticipante(Socio socio) {
		if (socio != null && !participantes.contains(socio)) {
			participantes.add(socio);
		}
	}

	public boolean tieneParticipante(Socio socio) {
		return participantes.contains(socio);
	}

	public boolean estaCancelado() {
		return cancelado;
	}

	public boolean isJugado() {
		return jugado;
	}

	public Socio getGanador() {
		return ganador;
	}

	public Map<Socio, EstadisticaPractica> getEstadisticas() {
		return estadisticas;
	}

	public EstadisticaPractica getEstadistica(Socio socio) {
		return estadisticas.get(socio);
	}

	/**
	 * Se cancela automáticamente si la reserva asociada se cancela (RF8).
	 */
	public void cancelar() {
		this.cancelado = true;
	}

	/**
	 * Registra el resultado y estadísticas del partido de práctica (RF10).
	 * Valida permisos, disciplina, coherencia de marcador y actualiza el ELO de los participantes.
	 */
	public void registrarResultado(Usuario quienRegistra, Map<Socio, EstadisticaPractica> stats)
			throws EstadoInvalidoException, PermisoDenegadoException, ReglaDeportivaException, DatoInvalidoException {
		if (cancelado) {
			throw new EstadoInvalidoException("No se pueden registrar estadísticas en un partido cancelado");
		}
		if (jugado) {
			throw new EstadoInvalidoException("El resultado del partido ya fue registrado previamente: solo se registra una vez");
		}
		if (!(quienRegistra instanceof Socio) || !tieneParticipante((Socio) quienRegistra)) {
			throw new PermisoDenegadoException("Solo un socio participante del partido puede registrar su resultado");
		}
		if (stats == null || stats.isEmpty()) {
			throw new DatoInvalidoException("Debe ingresar las estadísticas de los participantes");
		}
		for (Socio participante : participantes) {
			if (!stats.containsKey(participante)) {
				throw new DatoInvalidoException("Falta registrar la estadística del participante " + participante.getLogin());
			}
		}

		Disciplina disciplina = reserva != null && reserva.getInstalacion() != null
				? reserva.getInstalacion().getDisciplina() : null;

		Socio posibleGanador = null;
		int maxSets = -1;
		boolean empateSets = false;

		for (Map.Entry<Socio, EstadisticaPractica> entry : stats.entrySet()) {
			Socio socio = entry.getKey();
			EstadisticaPractica stat = entry.getValue();

			if (!tieneParticipante(socio)) {
				throw new ReglaDeportivaException("El socio '" + socio.getLogin() + "' no es participante de este partido");
			}

			// Validar que la disciplina admita este tipo de estadística (RF10, EP10.2a)
			if (disciplina != null && !disciplina.admiteEstadistica(stat)) {
				throw new ReglaDeportivaException("La estadística ingresada no corresponde a la disciplina "
						+ disciplina.getNombre());
			}

			// Validar no negativos (EP10.2d)
			if (stat.getSetsGanados() < 0) {
				throw new DatoInvalidoException("Los sets ganados no pueden ser negativos");
			}
			if (stat instanceof EstadisticaTenisPadel) {
				EstadisticaTenisPadel etp = (EstadisticaTenisPadel) stat;
				if (etp.getGamesGanados() < 0 || etp.getAces() < 0 || etp.getErroresNoForzados() < 0) {
					throw new DatoInvalidoException("Los valores de games, aces y errores no forzados no pueden ser negativos");
				}
			} else if (stat instanceof EstadisticaTenisMesa) {
				EstadisticaTenisMesa etm = (EstadisticaTenisMesa) stat;
				if (etm.getPuntosTotales() < 0) {
					throw new DatoInvalidoException("Los puntos totales no pueden ser negativos");
				}
			}

			// Calcular ganador por sets
			if (stat.getSetsGanados() > maxSets) {
				maxSets = stat.getSetsGanados();
				posibleGanador = socio;
				empateSets = false;
			} else if (stat.getSetsGanados() == maxSets) {
				empateSets = true;
			}
		}

		if (empateSets || posibleGanador == null) {
			throw new ReglaDeportivaException("Marcador incoherente: en partidos de academia debe haber un ganador y no puede haber empate en sets");
		}

		this.ganador = posibleGanador;
		this.estadisticas = new HashMap<Socio, EstadisticaPractica>(stats);
		this.jugado = true;

		if (reserva != null) {
			reserva.setEstado(EstadoReserva.FINALIZADA);
		}

		// Actualizar ELO de los participantes en la disciplina (RF10, EP10.1)
		if (disciplina != null && participantes.size() == 2) {
			Socio s1 = participantes.get(0);
			Socio s2 = participantes.get(1);
			Inscripcion ins1 = s1.getInscripcion(disciplina);
			Inscripcion ins2 = s2.getInscripcion(disciplina);
			if (ins1 != null && ins2 != null) {
				int eloPrevio1 = ins1.getPuntajeELO();
				int eloPrevio2 = ins2.getPuntajeELO();
				ins1.actualizarELO(eloPrevio2, s1 == ganador);
				ins2.actualizarELO(eloPrevio1, s2 == ganador);
			}
		}

		// Conectar el partido en el historial de las inscripciones (RF11)
		if (disciplina != null) {
			for (Socio p : participantes) {
				Inscripcion ins = p.getInscripcion(disciplina);
				if (ins != null && !ins.getPartidos().contains(this)) {
					ins.agregarPartido(this);
				}
			}
		}
	}

	@Override
	public String toString() {
		String res = "Partido " + id;
		if (cancelado) {
			res += " (cancelado)";
		} else if (jugado) {
			res += " (finalizado, ganador: " + (ganador != null ? ganador.getLogin() : "-") + ")";
		} else {
			res += " (programado)";
		}
		return res;
	}
}
