package modelo.tienda;

import java.time.LocalDateTime;
import modelo.usuarios.Cliente;

public class VentaTienda extends Venta{
	
	public VentaTienda(Cliente cliente, LocalDateTime fechaHora) {
		super(cliente, fechaHora);
	}
	
	//método TODO: mirar donde queda descuento. restar???
	@Override
	public double calcularImpuesto() {
		return 0.19 * getSubtotal();
	}
}
