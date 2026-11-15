package consola;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

import excepciones.AutenticacionException;
import excepciones.PersistenciaException;
import logica.Club;
import logica.GestorUsuarios;
import modelo.usuarios.Usuario;
import persistencia.CentralPersistencia;

/**
 * Lo que comparten las tres consolas: cargar los datos, pedir login y contraseña,
 * mostrar el menú principal con los menús del rol, y guardar al salir.
 * También tiene los métodos para leer entradas validadas, que usan todos los menús.
 */
public abstract class Consola {

	private static final int INTENTOS_LOGIN = 3;

	private Scanner scanner;
	private CentralPersistencia persistencia;
	private String carpetaDatos;
	protected Club club;
	protected Usuario usuario;

	public Consola() {
		scanner = new Scanner(System.in);
		persistencia = new CentralPersistencia();
	}

	public Club getClub() {
		return club;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	// Lo que define cada consola

	/** Nombre que se muestra al iniciar, por ejemplo "Consola del socio". */
	protected abstract String getTitulo();

	/** Nombre del rol en minúscula, para los mensajes: "socio", "empleado", "administrador". */
	protected abstract String getNombreRol();

	/** Si el usuario que entró puede usar esta consola. */
	protected abstract boolean rolValido(Usuario usuario);

	/** Los menús que ve este rol, en el orden del menú principal. Se llama después del login. */
	protected abstract List<Menu> crearMenus();

	// Flujo principal

	public void ejecutar() {
		System.out.println("=== Club Deportivo Séneca: " + getTitulo() + " ===");
		if (!cargar()) {
			return;
		}
		if (!iniciarSesion()) {
			return;
		}
		menuPrincipal();
		guardar();
	}

	private boolean cargar() {
		carpetaDatos = CentralPersistencia.carpetaPorDefecto();
		try {
			club = persistencia.cargarClub(carpetaDatos);
			System.out.println("Datos cargados de " + carpetaDatos + "/");
			return true;
		} catch (PersistenciaException e) {
			System.out.println("No se pudieron cargar los datos: " + e.getMessage());
			return false;
		}
	}

	private boolean iniciarSesion() {
		GestorUsuarios gestor = new GestorUsuarios(club);
		for (int intento = 1; intento <= INTENTOS_LOGIN; intento++) {
			String login = leerTexto("Login");
			String password = leerTexto("Contraseña");
			try {
				Usuario candidato = gestor.autenticar(login, password);
				if (!rolValido(candidato)) {
					System.out.println("El usuario '" + login + "' no es " + getNombreRol()
							+ ". Use la consola que corresponde a su tipo de usuario.");
					continue;
				}
				usuario = candidato;
				System.out.println("Bienvenido, " + usuario.getLogin() + ".");
				return true;
			} catch (AutenticacionException e) {
				System.out.println(e.getMessage() + " (intento " + intento + " de " + INTENTOS_LOGIN + ")");
			}
		}
		System.out.println("Demasiados intentos. Hasta luego.");
		return false;
	}

	private void menuPrincipal() {
		List<Menu> menus = crearMenus();
		List<String> titulos = new java.util.ArrayList<String>();
		for (Menu menu : menus) {
			titulos.add(menu.getTitulo());
		}
		while (true) {
			int opcion = leerOpcion("Menú principal", titulos, "Salir y guardar");
			if (opcion == 0) {
				return;
			}
			menus.get(opcion - 1).mostrar();
		}
	}

	private void guardar() {
		try {
			persistencia.guardarClub(club, carpetaDatos);
			System.out.println("Cambios guardados en " + carpetaDatos + "/. Hasta luego.");
		} catch (PersistenciaException e) {
			System.out.println("No se pudieron guardar los cambios: " + e.getMessage());
		}
	}

	// Lectura de entradas (las usan los menús)

	/** Lee un texto que no puede quedar vacío. */
	public String leerTexto(String mensaje) {
		while (true) {
			System.out.print(mensaje + ": ");
			String texto = scanner.nextLine().trim();
			if (!texto.isEmpty()) {
				return texto;
			}
			System.out.println("No puede quedar vacío.");
		}
	}

	/** Lee un texto que puede quedar vacío (devuelve "" si el usuario solo da Enter). */
	public String leerTextoOpcional(String mensaje) {
		System.out.print(mensaje + " (Enter para omitir): ");
		return scanner.nextLine().trim();
	}

	/** Lee un entero entre min y max, los dos incluidos. */
	public int leerEntero(String mensaje, int min, int max) {
		while (true) {
			System.out.print(mensaje + " [" + min + " a " + max + "]: ");
			String texto = scanner.nextLine().trim();
			try {
				int valor = Integer.parseInt(texto);
				if (valor >= min && valor <= max) {
					return valor;
				}
				System.out.println("Debe estar entre " + min + " y " + max + ".");
			} catch (NumberFormatException e) {
				System.out.println("'" + texto + "' no es un número entero.");
			}
		}
	}

	/** Lee una lista de enteros separados por comas, por ejemplo "1, 4, 7". No puede quedar vacía. */
	public List<Integer> leerEnteros(String mensaje) {
		while (true) {
			System.out.print(mensaje + " (separados por comas): ");
			String[] partes = scanner.nextLine().split(",");
			List<Integer> numeros = new java.util.ArrayList<Integer>();
			boolean validos = true;
			for (String parte : partes) {
				String texto = parte.trim();
				try {
					numeros.add(Integer.parseInt(texto));
				} catch (NumberFormatException e) {
					System.out.println("'" + texto + "' no es un número entero.");
					validos = false;
				}
			}
			if (validos && !numeros.isEmpty()) {
				return numeros;
			}
		}
	}

	/** Lee un número con decimales mayor o igual a min. */
	public double leerDecimal(String mensaje, double min) {
		while (true) {
			System.out.print(mensaje + ": ");
			String texto = scanner.nextLine().trim().replace(',', '.');
			try {
				double valor = Double.parseDouble(texto);
				if (valor >= min) {
					return valor;
				}
				System.out.println("Debe ser mayor o igual a " + min + ".");
			} catch (NumberFormatException e) {
				System.out.println("'" + texto + "' no es un número.");
			}
		}
	}

	/** Lee una fecha en formato AAAA-MM-DD. */
	public LocalDate leerFecha(String mensaje) {
		while (true) {
			System.out.print(mensaje + " (AAAA-MM-DD): ");
			String texto = scanner.nextLine().trim();
			try {
				return LocalDate.parse(texto);
			} catch (DateTimeParseException e) {
				System.out.println("'" + texto + "' no es una fecha válida. Ejemplo: 2026-10-05");
			}
		}
	}

	/** Lee una hora en formato HH:MM. */
	public LocalTime leerHora(String mensaje) {
		while (true) {
			System.out.print(mensaje + " (HH:MM): ");
			String texto = scanner.nextLine().trim();
			try {
				return LocalTime.parse(texto);
			} catch (DateTimeParseException e) {
				System.out.println("'" + texto + "' no es una hora válida. Ejemplo: 14:30");
			}
		}
	}

	/** Pregunta sí o no. */
	public boolean confirmar(String mensaje) {
		while (true) {
			System.out.print(mensaje + " (s/n): ");
			String texto = scanner.nextLine().trim().toLowerCase();
			if (texto.equals("s") || texto.equals("si") || texto.equals("sí")) {
				return true;
			}
			if (texto.equals("n") || texto.equals("no")) {
				return false;
			}
			System.out.println("Responda s o n.");
		}
	}

	/**
	 * Muestra las opciones numeradas desde 1 y la opción 0 para salir o volver.
	 * Devuelve el número elegido (0 si eligió salir).
	 */
	public int leerOpcion(String titulo, List<String> opciones, String textoSalir) {
		System.out.println();
		System.out.println("--- " + titulo + " ---");
		for (int i = 0; i < opciones.size(); i++) {
			System.out.println("  " + (i + 1) + ". " + opciones.get(i));
		}
		System.out.println("  0. " + textoSalir);
		return leerEntero("Opción", 0, opciones.size());
	}

	/**
	 * Muestra los elementos numerados y devuelve el elegido, o null si el usuario
	 * eligió 0 para cancelar. Sirve para que no haya que escribir nombres o ids a mano.
	 */
	public <T> T elegir(String mensaje, List<T> elementos) {
		if (elementos.isEmpty()) {
			System.out.println("No hay elementos para elegir.");
			return null;
		}
		List<String> textos = new java.util.ArrayList<String>();
		for (T elemento : elementos) {
			textos.add(elemento.toString());
		}
		int opcion = leerOpcion(mensaje, textos, "Cancelar");
		if (opcion == 0) {
			return null;
		}
		return elementos.get(opcion - 1);
	}

	/** Imprime una lista con sangría, o un aviso si está vacía. */
	public void mostrarLista(String titulo, List<?> elementos) {
		System.out.println(titulo);
		if (elementos.isEmpty()) {
			System.out.println("   (sin información disponible)");
		}
		for (Object elemento : elementos) {
			System.out.println("   " + elemento);
		}
	}
}
