import java.util.ArrayList;
import java.util.List;

public class Pedido {

	//static porque aumenta en una unidad cada vez que se crea un pedido
	static int idGlobal;
	int idPedido;
	List<Producto> productos;
	Producto producto;
	String direccionCliente;
	
	
	public Pedido(List<Producto> productos, String direccionCliente) {
		idGlobal++;
		this.idPedido = idGlobal;
		this.productos = productos;
		this.direccionCliente = direccionCliente;
	}
	
	public Pedido(Producto producto, String direccionCliente) {
		idGlobal++;
		this.idPedido = idGlobal;
		this.producto = producto;
		this.direccionCliente = direccionCliente;
	}


	public int getIdPedido() {
		return this.idPedido;
	}


	public void setIdPedido(int idPedido) {
		this.idPedido = idPedido;
	}


	public List<Producto> getProductos() {
		return productos;
	}


	public void setProductos(List<Producto> productos) {
		this.productos = productos;
	}


	public String getDireccionCliente() {
		return direccionCliente;
	}


	public void setDireccionCliente(String direccionCliente) {
		this.direccionCliente = direccionCliente;
	}


	@Override
	public String toString() {
		return "Pedido [idPedido=" + idPedido + ", productos=" + productos + ", direccionCliente=" + direccionCliente
				+ "]";
	}
	
	
}