package logica;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import excepciones.AutenticacionException;
import modelo.usuarios.Administrador;
import modelo.usuarios.Usuario;

/**
 * Pruebas del inicio de sesión. Cada prueba arma su propio club en memoria.
 */
public class GestorUsuariosTest {

	private Club club;
	private GestorUsuarios gestor;

	@BeforeEach
	public void setUp() {
		club = new Club();
		club.agregarUsuario(new Administrador("admin", "123"));
		gestor = new GestorUsuarios(club);
	}

	@Test
	public void autenticaConLoginYPasswordCorrectos() throws AutenticacionException {
		Usuario usuario = gestor.autenticar("admin", "123");
		assertEquals("admin", usuario.getLogin());
	}

	@Test
	public void rechazaPasswordIncorrecta() {
		assertThrows(AutenticacionException.class, () -> gestor.autenticar("admin", "otra"));
	}

	@Test
	public void rechazaLoginInexistente() {
		assertThrows(AutenticacionException.class, () -> gestor.autenticar("nadie", "123"));
	}
}
