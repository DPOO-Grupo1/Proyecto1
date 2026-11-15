package modelo.tienda;

public class Articulo extends Producto{
	private String categoria;
	
	public Articulo(String nombre, double precio, String categoria) {
		super(nombre, precio);
		this.categoria = categoria;
	}

	public String getCategoria() {
		return categoria;
	}

	@Override
	public String toString() {
		return nombre + " ($" + (long) precio + ", artículo de " + categoria + ")";
	}
}
