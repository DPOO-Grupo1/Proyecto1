package modelo.tienda;

import modelo.tienda.Producto;

public class DetalleVenta {
	int cantidad;
	double precioUnitario;
	Producto producto;
	
	public DetalleVenta(Producto producto, int cantidad){
		this.producto = producto;
		this.precioUnitario = producto.getPrecio();
		this.cantidad = cantidad;
	}

	public int getCantidad() {
		return cantidad;
	}
	public double getPrecioUnitario() {
		return precioUnitario;
	}
	public Producto getProducto() {
		return producto;
	}
	
	//método
	public double getSubtotal() {
		return cantidad * precioUnitario;
	}
	
}
