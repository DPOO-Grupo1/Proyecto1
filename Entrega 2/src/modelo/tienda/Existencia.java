package modelo.tienda;

public class Existencia {
	private int cantidad;
	private Producto producto;
	private Ubicacion ubicacion;
	
	public Existencia(Producto producto, Ubicacion ubicacion, int cantidad) {
		this.cantidad = cantidad;
		this.producto = producto;
		this.ubicacion = ubicacion;
	}

	public int getCantidad() {
		return cantidad;
	}
	public Producto getProducto() {
		return producto;
	}
	public Ubicacion getUbicacion() {
		return ubicacion;
	}
	
	//métodos:
	public void descontar(int cantidad) {
	    this.cantidad -= cantidad;
	}

	public void sumar(int cantidad) {
	    this.cantidad += cantidad;
	}
}
