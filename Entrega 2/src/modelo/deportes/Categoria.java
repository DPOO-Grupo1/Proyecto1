package modelo.deportes;

import java.time.LocalDate;

import modelo.usuarios.Socio;

/**
 * Categoría por edad (Sub-15, Sub-17, Mayores...).
 * La edad que cuenta es la que el socio tenía el 1 de enero del año.
 */
public class Categoria {

	/**
	 * Para categorías sin edad máxima, como Mayores.
	 */
	public static final int SIN_EDAD_MAXIMA = 0;

	private String nombre;
	private int edadMinima;
	private int edadMaxima;

	public Categoria(String nombre, int edadMinima, int edadMaxima) {
		this.nombre = nombre;
		this.edadMinima = edadMinima;
		this.edadMaxima = edadMaxima;
	}

	public String getNombre() {
		return nombre;
	}

	public int getEdadMinima() {
		return edadMinima;
	}

	public int getEdadMaxima() {
		return edadMaxima;
	}

	public boolean tieneEdadMaxima() {
		return edadMaxima != SIN_EDAD_MAXIMA;
	}

	public boolean admiteEdad(int edad) {
		if (edad < edadMinima) {
			return false;
		}
		if (tieneEdadMaxima() && edad > edadMaxima) {
			return false;
		}
		return true;
	}

	/**
	 * Revisa la edad que tenía el socio el 1 de enero del año de la fecha.
	 */
	public boolean admite(Socio socio, LocalDate fecha) {
		LocalDate inicioDeAnio = LocalDate.of(fecha.getYear(), 1, 1);
		return admiteEdad(socio.getEdad(inicioDeAnio));
	}
}
