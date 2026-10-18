package modelo.tienda;

import java.time.LocalDateTime;
import modelo.usuarios.Cliente;

public class VentaCafeteria extends Venta{
	private int porcentajePropina;
	
	public VentaCafeteria(Cliente cliente, LocalDateTime fechaHora, int porcentajePropina) {
		super(cliente, fechaHora);
		this.porcentajePropina = porcentajePropina;
	}

	public int getPorcentajePropina() {
		return porcentajePropina;
	}
	
	@Override
	public double calcularImpuesto() {
		return 0.08 * getSubtotal();
	}
	
	public double calcularPropina() {
		return (getPorcentajePropina() * getSubtotal()) /100;
	}
}
