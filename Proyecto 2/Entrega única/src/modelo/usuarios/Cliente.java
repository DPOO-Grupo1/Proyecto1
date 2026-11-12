package modelo.usuarios;

import java.util.ArrayList;
import java.util.List;
import modelo.tienda.Venta;

/**
 * Socios y empleados: compran en el club y acumulan puntos de fidelidad.
 */
public abstract class Cliente extends Usuario {

	protected String nombre;
	protected String apellidos;
	protected int puntosFidelidad;
	protected List<Venta> compras;

	public Cliente(String login, String password, String nombre, String apellidos) {
		super(login, password);
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.puntosFidelidad = 0;
		this.compras = new ArrayList<Venta>();
	}

	public String getNombre() {
		return nombre;
	}

	public String getApellidos() {
		return apellidos;
	}

	public String getNombreCompleto() {
		return nombre + " " + apellidos;
	}

	public int getPuntosFidelidad() {
		return puntosFidelidad;
	}

	public void setPuntosFidelidad(int puntosFidelidad) {
		this.puntosFidelidad = puntosFidelidad;
	}
	
	public void sumarPuntos(int puntos) {
	    this.puntosFidelidad += puntos;
	}

	public void restarPuntos(int puntos) {
	    this.puntosFidelidad -= puntos;
	}

	public void agregarCompra(Venta venta) {
	    compras.add(venta);
	}

	public List<Venta> getCompras() {
	    return compras;
	}
}
