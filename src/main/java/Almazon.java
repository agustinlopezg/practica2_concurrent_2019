import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//import java.util.concurrent.locks;

public class Almazon {
	
	static Lock cerrojo_turno8horas = new ReentrantLock();

	public static void persona() {
		try {
			try {
				Thread.sleep(3000);
				cerrojo_turno8horas.lock();
				System.out.println("Empieza el turno de trabajo");
			}finally {
				System.out.println("Termina el turno de trabajo");
				cerrojo_turno8horas.unlock();
			}
		} catch (InterruptedException e) {
			System.out.println("Se ha generado una excepci�n a la hora de dormir el proceso");
			e.printStackTrace();
		}
	}
	
	private void exec() {
		new Thread( ()->persona() ).start();
	}
	
	public static void main(String[] args) {	
		new Almazon().exec();
	}
}
