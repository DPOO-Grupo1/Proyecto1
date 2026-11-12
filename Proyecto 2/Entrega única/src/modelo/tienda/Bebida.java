package modelo.tienda;

public class Bebida extends ProductoCafeteria{
	private String temperatura;
	private int tamanio;
	
	public Bebida(String temperatura, int tamanio, String nombre, double precio) {
		super(nombre, precio);
		this.temperatura = temperatura;
		this.tamanio = tamanio;
	}

	public String getTemperatura() {
		return temperatura;
	}
	public int getTamanio() {
		return tamanio;
	}
	
	//método
	public boolean esCaliente() {
	    return temperatura.equalsIgnoreCase("CALIENTE");
	}
}
