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
	
}
