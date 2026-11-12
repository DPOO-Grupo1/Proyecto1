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
	
}
