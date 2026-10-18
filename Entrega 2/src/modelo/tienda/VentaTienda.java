package modelo.tienda;

import java.time.LocalDateTime;
import modelo.usuarios.Cliente;

/**
 * Venta de la tienda. Paga IVA del 19 %.
 */
public class VentaTienda extends Venta {
	
	public VentaTienda(Cliente cliente, LocalDateTime fechaHora) {
		super(cliente, fechaHora);
	}
	
	// El descuento se resta en Venta.calcularTotal()
	@Override
	public double calcularImpuesto() {
		return 0.19 * getSubtotal();
	}
}
