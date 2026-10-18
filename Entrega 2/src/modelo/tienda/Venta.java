package modelo.tienda;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import modelo.usuarios.Cliente;

public abstract class Venta {
	protected Cliente cliente;
	protected LocalDateTime fechaHora;
	protected double montoTotal;
	protected int puntosRedimidos;
	protected List <DetalleVenta> detalles;
	protected CodigoDescuento codigo;
		
	public Venta(Cliente cliente, LocalDateTime fechaHora) {
		this.cliente = cliente;
        this.fechaHora = fechaHora;
        this.detalles = new ArrayList<DetalleVenta>();
        this.codigo = null;
        this.puntosRedimidos = 0;
        this.montoTotal = 0;
	}
	//Getters
	public Cliente getCliente() {
		return cliente;
	}
	public LocalDateTime getFechaHora() {
		return fechaHora;
	}
	public double getMontoTotal() {
		return montoTotal;
	}
	public int getPuntosRedimidos() {
		return puntosRedimidos;
	}
	public List<DetalleVenta> getDetalles() {
		return detalles;
	}
	public CodigoDescuento getCodigo() {
		return codigo;
	}
	
	//métodos
	public void agregarDetalle(Producto producto, int cantidad) {
		DetalleVenta detalle = new DetalleVenta(producto, cantidad);
		detalles.add(detalle);
	}
	
	public void aplicarCodigo(CodigoDescuento codigo) {
		this.codigo = codigo;
	}
	
	public void redimirPuntos(int p) {
		puntosRedimidos = p;
	} 
	
	public double getSubtotal() {
		double subtotal = 0;
		for (DetalleVenta detalle : detalles) {
			subtotal += detalle.getSubtotal();
		}
		return subtotal;
	}
	
	public abstract double calcularImpuesto() ;
	
	//TODO: Decidir orden de cálculo con propina y puntos
	public double calcularTotal() {
		return 0;
	}
	
	public double calcularPuntosGenerados() {
		return 0;
	}
	
}
