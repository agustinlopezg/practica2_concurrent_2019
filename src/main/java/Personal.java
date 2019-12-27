 
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Personal {
	static final int N_EMPLEADOS_ADMINISTRATIVO = 5;
	static final int N_EMPLEADOS_RECOGEPEDIDOS = 5;
	static final int N_EMPLEADOS_EMPAQUETAPEDIDOS = 5;
	static final int N_EMPLEADOS_LIMPIEZA = 5;
	static final int N_EMPLEADOS_ENCARGADO = 1;
	static final int N_CLIENTES = 10;
	
	static final int CAPACIDAD_PLAYA = 10;
	static final int N_PEDIDOS_PARA_LIMPIAR_PLAYAS = 20;
	static final int MAX_PRIORITY = 10;
	
	//(24/CONVERSIÓN_TIEMPO) SERÁ EL TIEMPO REAL DE DURACIÓN DEL PROGRAMA (EN MINUTOS)
	static final int CONVERSION_TIEMPO = 2;
	
	volatile static int n_empleados = 0;
	volatile static int n_pedidosRealizados = 0;
	volatile static int num_playa = (int) (Math.random() * 3);
	//EmpleadoEmpaquetaPedidos le llame para que 
	//limpie una playa concreta debido a algún accidente ocurrido en la misma
	//(se estima que estas actuaciones suponen solo el 5% de los casos).
	volatile static int probabilidad_playaConcreta = (int) (Math.random() * 100);
	volatile static int playa_concreta = 0;
	
	static List<Pedido> almacen_pedidos = Collections.synchronizedList(new ArrayList<>());
	static List<Pedido> almacen_pedidos_revisados = Collections.synchronizedList(new ArrayList<>());
	static List<Pedido> almacen_pedidos_recogidos = Collections.synchronizedList(new ArrayList<>());
	static List<Pedido> almacen_pedidos_erroneos = Collections.synchronizedList(new ArrayList<>());
	static List<Pedido> cinta_de_salida = Collections.synchronizedList(new ArrayList<>());
	static List<Producto> productosDisponibles = Collections.synchronizedList(new ArrayList<>());
	static List<Producto> productosEnRebajas = Collections.synchronizedList(new ArrayList<>());
	static List<Producto> productosNovedosos = Collections.synchronizedList(new ArrayList<>());
	static List<Producto> productosAgotados = Collections.synchronizedList(new ArrayList<>());
	static List<Thread> lista_hilos = Collections.synchronizedList(new ArrayList<>());

	static List<Producto> playa1 = new ArrayList<>(CAPACIDAD_PLAYA);
	static List<Producto> playa2 = new ArrayList<>(CAPACIDAD_PLAYA);
	static List<Producto> playa3 = new ArrayList<>(CAPACIDAD_PLAYA);
	
	static Lock pedido_revisado = new ReentrantLock();
	static Lock pedido_recogido = new ReentrantLock();
	static Lock pedido_empaquetado = new ReentrantLock();
	static Lock limpiar_playas = new ReentrantLock();
	static Lock hacer_pedido = new ReentrantLock();
	static Lock enviar_mensaje = new ReentrantLock();
	
	static Semaphore sem_pedidos_realizados = new Semaphore(0);
	static Semaphore sem_listo_para_recoger = new Semaphore(0);
	static Semaphore sem_listo_para_empaquetar = new Semaphore(0);
	static Semaphore sem_listo_para_enviar = new Semaphore(0);
	static Semaphore sem_limpiar_playas = new Semaphore(0);
	static Semaphore sem_empezar_jornada = new Semaphore(0);
	static Semaphore sem_pedido_erroneo = new Semaphore(0);
	static Semaphore sem_limpiar_playaConcreta = new Semaphore(0);
	/*public Personal() {
		productosDisponibles.add(new Producto("Telefono Movil", "Telefonia"));
		productosDisponibles.add(new Producto("PS4", "Entretenimiento"));
		productosDisponibles.add(new Producto("Camisa", "Ropa"));
		productosDisponibles.add(new Producto("Mesa", "Inmobiliario"));
		
		productosEnRebajas.add(new Producto("Sudadera", "Ropa"));
		productosEnRebajas.add(new Producto("Ordenador", "Tecnologia"));
		productosEnRebajas.add(new Producto("Silla", "Inmobiliario"));
		productosEnRebajas.add(new Producto("Congelador", "Inmobiliario"));
		
		productosNovedosos.add(new Producto("Cargador", "Tecnologia"));
		productosNovedosos.add(new Producto("Tablet", "Tecnologia"));
		productosNovedosos.add(new Producto("Cuadro", "Decoracion"));
		productosNovedosos.add(new Producto("Cuaderno", "Colegio"));
		
		productosAgotados.add(new Producto("Camiseta", "Ropa"));
		productosAgotados.add(new Producto("Mesa", "Inmobiliario"));
		productosAgotados.add(new Producto("Ordenador", "Tecnologia"));
		productosAgotados.add(new Producto("Lampara", "Decoracion"));
	}*/
	
	public static void cliente(int idCliente) {
		while(true) {
			try {
			
				int num_pedido = (int) (Math.random() * 4);
				Pedido p = null;
				switch(num_pedido) {
					case 0: 
							hacer_pedido.lock();
							p = new Pedido(productosDisponibles, "cliente" + idCliente + "@almazon.es");
							hacer_pedido.unlock();
							almacen_pedidos.add(p);
							break;
					case 1: 	
							hacer_pedido.lock();
							p = new Pedido(productosEnRebajas, "cliente" + idCliente + "@almazon.es");
							hacer_pedido.unlock();
							almacen_pedidos.add(p);
							break;
					case 2:
							hacer_pedido.lock();
							p = new Pedido(productosNovedosos, "cliente" + idCliente + "@almazon.es");
							hacer_pedido.unlock();
							almacen_pedidos.add(p);
							break;
					case 3:
							hacer_pedido.lock();
							p = new Pedido(productosAgotados, "cliente" + idCliente + "@almazon.es");
							hacer_pedido.unlock();
							almacen_pedidos.add(p);
							break;		
				}
				
				//System.out.println("Tamaño " + almacen_pedidos.size());
				System.out.println("El cliente " + idCliente + " acaba de realizar el pedido nº " + 
				p.getIdPedido() + " y se va a buscar los artículos para realizar el siguiente pedido");
				sem_pedidos_realizados.release();
				//Se duerme un tiempo aleatorio
				Thread.sleep(1000 * (num_pedido + 1));
			}catch(InterruptedException e) {
				System.out.println("Lo siento, el cliente " + idCliente + " se tiene que esperar a la siguiente jornada para seguir realizando pedidos");
				break;
			}
		}
	}
	
	public static void empleado_administrativo(int nEmpleado)  {
		while(true) {
			try {
				sem_empezar_jornada.acquire();
				sem_pedidos_realizados.acquire();
				n_empleados++;
				pedido_revisado.lock();
				try {
					System.out.println("Pedido nº " + almacen_pedidos.get(0).getIdPedido() + " revisado. Listo para ser procesado");
					almacen_pedidos_revisados.add(almacen_pedidos.remove(0));
				}finally {
					pedido_revisado.unlock();
				}
				
				sem_listo_para_recoger.release();
				sem_listo_para_enviar.acquire();
				
				
				enviar_mensaje.lock();
				try {
					System.out.println("¡Mensaje enviado al cliente " + cinta_de_salida.remove(0).getDireccionCliente() + "!");
				} finally {
					enviar_mensaje.unlock();
				}
				limpiar_playas.lock();
				try {
					n_pedidosRealizados ++;
					if(n_pedidosRealizados == N_PEDIDOS_PARA_LIMPIAR_PLAYAS) {
						sem_limpiar_playas.release();
					}
				} finally {
					limpiar_playas.unlock();
				}
			} catch (InterruptedException e) {
				System.out.println("El empleado Administrativo " + nEmpleado + " se va a casa");
				n_empleados--;
				break;
			}
		}
	}
	
	public static void empleado_recogepedidos(int nEmpleado) {
		while(true) {
			try {
				sem_listo_para_recoger.acquire();
				n_empleados++;
				Pedido p;
				pedido_recogido.lock();
				try {
					p = almacen_pedidos_revisados.get(0);
					almacen_pedidos_recogidos.add(almacen_pedidos_revisados.remove(0));
				} finally {
					pedido_recogido.unlock();
				}
				
				//Hay que ponerle una condicion para que pare cuando llegue a...
				System.out.println("Procesando pedido nº " + p.getIdPedido() + "...");
				n_pedidosRealizados++;
				Thread.sleep(500 * p.getProductos().size());
				System.out.println("Recogidos los productos " + p.getProductos().toString() + " del pedido nº " + p.getIdPedido() + " del almacén y llevados a la playa nº " + num_playa);
			
				switch(num_playa) {
					case 0: 
						playa1.addAll(p.getProductos());
						break;
					case 1:
						playa2.addAll(p.getProductos());
						break;
					case 2:
						playa3.addAll(p.getProductos());
						break;
				}
				
				if(Thread.currentThread().getId() == 30) { //por ejemplo
					sem_pedido_erroneo.release();
				}
				sem_listo_para_empaquetar.release();
				
			} catch (InterruptedException e) {
				System.out.println("El empleado RecogePedidos " + nEmpleado + " se va a casa");
				n_empleados--;
				break;
			}
		}
	}
	
	public static void empleado_empaquetapedidos(int nEmpleado){
		while(true) {
			try {
				
				sem_listo_para_empaquetar.acquire();
				n_empleados++;
				if(sem_pedido_erroneo.availablePermits() == 0) {
					pedido_empaquetado.lock();
					try {
						System.out.println("Pedido nº " + almacen_pedidos_recogidos.get(0).getIdPedido() + " revisado y recogido de la playa. Puesta la pegatina y enviado a la cinta de salida");
						cinta_de_salida.add(almacen_pedidos_recogidos.remove(0));
					} finally {
						pedido_empaquetado.unlock();
					}
					
					sem_listo_para_enviar.release();
				}else {
					sem_pedido_erroneo.acquire();
					Thread.currentThread().setPriority(MAX_PRIORITY);
					System.out.println("Se ha detectado un fallo en el pedido nº " + almacen_pedidos_recogidos.get(0).getIdPedido());
					almacen_pedidos_erroneos.add(almacen_pedidos_recogidos.remove(0));
					Thread.sleep(2000);
					System.out.println("Se han solucionado los fallos de los pedidos erroneos");
					almacen_pedidos_erroneos.clear();
				}
				
				limpiar_playas.lock();
				try {
					if(probabilidad_playaConcreta < 5) { //probabilidad del 5% de que le salga de borrar una playa concreta
						playa_concreta = (int) (Math.random() * 4);
						sem_limpiar_playaConcreta.release();
					}
				} finally {
					limpiar_playas.unlock();
				}
				
			} catch(InterruptedException e) {
				System.out.println("El empleado EmpaquetaPedidos " + nEmpleado + " se va a casa");
				n_empleados--;
				break;
			}
		}
	}
	
	public static void empleado_limpieza(int nEmpleado){
		while(true) {
			try {
				n_empleados++;
				sem_limpiar_playas.acquire();
				playa1.clear();
				playa2.clear();
				playa3.clear();
				
				
				switch(playa_concreta) {
					case 1: 
						playa1.clear();
						System.out.println("El empleado de Limpieza " + nEmpleado + " ha limpiado la playa nº1");
						break;
					case 2: 	
						playa2.clear();
						System.out.println("El empleado de Limpieza " + nEmpleado + " ha limpiado la playa nº2");
						break;
					case 3:
						playa3.clear();
						System.out.println("El empleado de Limpieza " + nEmpleado + " ha limpiado la playa nº3");
						break;
				}		
				System.out.println("El empleado de Limpieza " + nEmpleado + " ha limpiado las playas");
			} catch(InterruptedException e) {
				System.out.println("El empleado de Limpieza " + nEmpleado + " se va a casa");
				n_empleados--;
				break;
			}
		}
	}
	
	public static void empleado_encargado(int nEmpleado) {
		n_empleados++;
		System.out.println("COMIENZA LA JORNADA DE TRABAJO !!!");
		sem_empezar_jornada.release(N_EMPLEADOS_ENCARGADO - 1 + N_EMPLEADOS_LIMPIEZA + 
				N_EMPLEADOS_EMPAQUETAPEDIDOS + N_EMPLEADOS_RECOGEPEDIDOS + N_EMPLEADOS_ADMINISTRATIVO);
	    
		try {
			Thread.sleep((24 / CONVERSION_TIEMPO) * 1000); //Simula la duración de la jornada de trabajo
		} catch (InterruptedException e) {
			System.out.println("El empleado Encargado " + nEmpleado + " se va a casa");
			n_empleados--;
		}
		
		System.out.println("SE ACABÓ LA JORNADA !!!");
		
		for(Thread t : lista_hilos) {
			t.interrupt();
		}
	    
	    //Finalmente, es el último empleado que termina de ejecutarse
		try {
			Thread.sleep(1000); //Simula la duración de la jornada de trabajo
		} catch (InterruptedException e) {
			System.out.println("El empleado Encargado " + nEmpleado + " se va a casa");
			n_empleados--;
		}
		System.out.println("El empleado Encargado " + nEmpleado + " cierra el almacen y se va a casa");
		n_empleados--;
	}

	private void exec() {
		
		productosDisponibles.add(new Producto("Telefono Movil", "Telefonia"));
		productosDisponibles.add(new Producto("PS4", "Entretenimiento"));
		productosDisponibles.add(new Producto("Camisa", "Ropa"));
		productosDisponibles.add(new Producto("Mesa", "Inmobiliario"));
		
		productosEnRebajas.add(new Producto("Sudadera", "Ropa"));
		productosEnRebajas.add(new Producto("Ordenador", "Tecnologia"));
		productosEnRebajas.add(new Producto("Silla", "Inmobiliario"));
		productosEnRebajas.add(new Producto("Congelador", "Inmobiliario"));
		
		productosNovedosos.add(new Producto("Cargador", "Tecnologia"));
		productosNovedosos.add(new Producto("Tablet", "Tecnologia"));
		productosNovedosos.add(new Producto("Cuadro", "Decoracion"));
		productosNovedosos.add(new Producto("Cuaderno", "Colegio"));
		
		productosAgotados.add(new Producto("Camiseta", "Ropa"));
		productosAgotados.add(new Producto("Mesa", "Inmobiliario"));
		productosAgotados.add(new Producto("Ordenador", "Tecnologia"));
		productosAgotados.add(new Producto("Lampara", "Decoracion"));
		
		for(int i=0; i<N_CLIENTES; i++) {
			int a = i;
			Thread t = new Thread( ()->cliente(a + 1));
			t.start();
			lista_hilos.add(t);
		}
		
		for(int i=0; i<N_EMPLEADOS_ADMINISTRATIVO; i++) {
			int a = i;
			Thread t = new Thread( ()->empleado_administrativo(a + 1));
			t.start();
			lista_hilos.add(t);
		}
		
		for(int i=0; i<N_EMPLEADOS_RECOGEPEDIDOS; i++) {
			int a = i;
			Thread t = new Thread( ()->empleado_recogepedidos(a + 1));
			t.start();
			lista_hilos.add(t);
		}
		
		for(int i=0; i<N_EMPLEADOS_EMPAQUETAPEDIDOS; i++) {
			int a = i;
			Thread t = new Thread( ()->empleado_empaquetapedidos(a + 1));
			t.start();
			lista_hilos.add(t);
		}
		
		for(int i=0; i<N_EMPLEADOS_LIMPIEZA; i++) {
			int a = i;
			Thread t = new Thread( ()->empleado_limpieza(a + 1));
			t.start();
			lista_hilos.add(t);
		}

		for(int i=0; i<N_EMPLEADOS_ENCARGADO; i++) {
			int a = i;
			new Thread( ()->empleado_encargado(a + 1)).start();
		}
	}
	
	public static void main(String[] args) {	
		new Personal().exec();
	}


}
