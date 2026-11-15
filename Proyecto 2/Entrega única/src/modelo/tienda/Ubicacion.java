package modelo.tienda;

public class Ubicacion {
	private String nombre;
	
	public Ubicacion(String nombre) {
		this.nombre = nombre;
	}

	public String getNombre() {
		return nombre;
	}

	@Override
	public String toString() {
		return nombre;
	}
}
