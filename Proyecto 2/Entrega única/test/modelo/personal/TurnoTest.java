package modelo.personal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import modelo.usuarios.Entrenador;

/**
 * Pruebas unitarias de Turno, SolicitudCambioTurno y HorarioAtencion.
 */
public class TurnoTest {

	private Entrenador entrenador;
	private Turno lunesManana;

	@BeforeEach
	public void setUp() {
		entrenador = new Entrenador("e1", "pass", "Marta", "López", 1);
		lunesManana = new Turno(1, entrenador, DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(12, 0));
	}

	@Test
	public void dosTurnosSeSolapanSoloSiSonElMismoDiaYSeCruzan() {
		Turno cruzado = new Turno(2, entrenador, DayOfWeek.MONDAY, LocalTime.of(11, 0), LocalTime.of(14, 0));
		Turno seguido = new Turno(3, entrenador, DayOfWeek.MONDAY, LocalTime.of(12, 0), LocalTime.of(16, 0));
		Turno otroDia = new Turno(4, entrenador, DayOfWeek.TUESDAY, LocalTime.of(8, 0), LocalTime.of(12, 0));

		assertTrue(lunesManana.seSolapaCon(cruzado));
		assertFalse(lunesManana.seSolapaCon(seguido));
		assertFalse(lunesManana.seSolapaCon(otroDia));
	}

	@Test
	public void elTurnoCubreDesdeElInicioHastaAntesDelFin() {
		assertTrue(lunesManana.cubre(DayOfWeek.MONDAY, LocalTime.of(8, 0)));
		assertTrue(lunesManana.cubre(DayOfWeek.MONDAY, LocalTime.of(11, 0)));
		assertFalse(lunesManana.cubre(DayOfWeek.MONDAY, LocalTime.of(12, 0)));
		assertFalse(lunesManana.cubre(DayOfWeek.TUESDAY, LocalTime.of(9, 0)));
	}

	@Test
	public void unTurnoConSolicitudPendienteNoAdmiteOtraHastaQueSeResuelva() {
		SolicitudCambioTurno solicitud = new SolicitudCambioTurno(lunesManana, LocalDate.of(2026, 11, 2),
				DayOfWeek.TUESDAY, LocalTime.of(8, 0), LocalTime.of(12, 0));
		lunesManana.agregarSolicitud(solicitud);
		assertTrue(lunesManana.tieneSolicitudPendiente());

		solicitud.rechazar();

		assertFalse(lunesManana.tieneSolicitudPendiente());
		assertEquals(EstadoSolicitud.RECHAZADA, solicitud.getEstado());
	}

	@Test
	public void elHorarioDeAtencionEsDeLunesASabadoDeSeisAVeinte() {
		HorarioAtencion horario = new HorarioAtencion();

		assertTrue(horario.contiene(DayOfWeek.SATURDAY, LocalTime.of(6, 0), LocalTime.of(20, 0)));
		assertFalse(horario.contiene(DayOfWeek.SUNDAY, LocalTime.of(8, 0), LocalTime.of(10, 0)));
		assertFalse(horario.contiene(DayOfWeek.MONDAY, LocalTime.of(19, 0), LocalTime.of(21, 0)));
		assertFalse(horario.contiene(DayOfWeek.MONDAY, LocalTime.of(12, 0), LocalTime.of(10, 0)));
		assertEquals(14, horario.getBloques().size());
	}
}
