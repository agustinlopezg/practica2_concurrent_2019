public class Producto {

	String nombre;
	String clase;
	
	public Producto(String nombre, String clase) {
		this.nombre = nombre;
		this.clase = clase;
	}
	
	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public String getClase() {
		return clase;
	}

	public void setClase(String clase) {
		this.clase = clase;
	}

	@Override
	public String toString() {
		return (nombre+ "|" + clase);
	}
}