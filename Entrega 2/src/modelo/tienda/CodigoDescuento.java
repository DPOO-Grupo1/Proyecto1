package modelo.tienda;

import modelo.usuarios.Cliente;
import modelo.usuarios.Empleado;

public class CodigoDescuento {
	private int idDescuento;
	private boolean activo;
	private Empleado dueno;
	
	public CodigoDescuento(int idDescuento, Empleado dueno) {
		this.idDescuento = idDescuento;
		activo = true;
	}

	public int getIdDescuento() {
		return idDescuento;
	}
	public boolean isActivo() {
		return activo;
	}
	
	//métodos:
	public double porcentajePara(Cliente cliente) {
		if (cliente == dueno) {
			return 0.15;
		}
		else {
			return 0.08;
		}
	}
	
}
