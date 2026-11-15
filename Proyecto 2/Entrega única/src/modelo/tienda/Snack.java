package modelo.tienda;

import java.util.List;

public class Snack extends ProductoCafeteria{
	private List<String> alergenos;
	
	public Snack(List<String> alergenos, String nombre, double precio) {
		super(nombre, precio);
		this.alergenos = alergenos;
	}

	public List<String> getAlergenos() {
		return alergenos;
	}

	@Override
	public String toString() {
		String texto = nombre + " ($" + (long) precio + ", snack";
		if (alergenos.isEmpty()) {
			return texto + ", sin alérgenos)";
		}
		return texto + ", alérgenos: " + String.join(", ", alergenos) + ")";
	}
}
