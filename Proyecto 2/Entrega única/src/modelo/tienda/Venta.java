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
	protected int idBono;
	protected int porcentajeBono;
		
	public Venta(Cliente cliente, LocalDateTime fechaHora) {
		this.cliente = cliente;
        this.fechaHora = fechaHora;
        this.detalles = new ArrayList<DetalleVenta>();
        this.codigo = null;
        this.idBono = 0;
        this.porcentajeBono = 0;
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
	/** Id del bono de torneo usado en la venta, o 0 si no se usó. */
	public int getIdBono() {
		return idBono;
	}
	public int getPorcentajeBono() {
		return porcentajeBono;
	}
	
	//métodos
	public void agregarDetalle(Producto producto, int cantidad) {
		DetalleVenta detalle = new DetalleVenta(producto, cantidad);
		detalles.add(detalle);
	}
	
	public void aplicarCodigo(CodigoDescuento codigo) {
		this.codigo = codigo;
	}
	
	/**
	 * Bono de torneo recreativo. Se guarda su id y su porcentaje, no el objeto,
	 * para que la venta se pueda cargar antes que los torneos.
	 */
	public void aplicarBono(int idBono, int porcentajeBono) {
		this.idBono = idBono;
		this.porcentajeBono = porcentajeBono;
	}
	
	public boolean tieneBono() {
		return idBono != 0;
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
	
	// El bono no se suma con el código: si hay bono, solo cuenta el bono
	public double getDescuento() {
	    if (tieneBono()) {
	        return (getSubtotal() + calcularImpuesto()) * porcentajeBono / 100.0;
	    }
	    if (codigo == null) {
	        return 0;
	    }
	    return (getSubtotal() + calcularImpuesto()) * codigo.porcentajePara(cliente);
	}

	public double calcularPropina() {
	    return 0;   // VentaCafeteria lo sobrescribe
	}

	public double calcularTotal() {
	    // subtotal + impuesto - descuento - puntos + propina
	    montoTotal = getSubtotal() + calcularImpuesto() - getDescuento() - puntosRedimidos + calcularPropina();
	    return montoTotal;
	}

	public int calcularPuntosGenerados() {
	    return (int) (montoTotal * 0.02);
	}

	
}
