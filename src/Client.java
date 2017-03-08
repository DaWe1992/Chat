import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Class Client.
 * @author Daniel
 *
 */
public class Client {
	
	/**
	 * Constructor.
	 */
	private Client() {}
	
	/**
	 * Main method.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			//get registry
			Registry registry = LocateRegistry.getRegistry("localhost");
			
			//get service
			IHello stub = (IHello) registry.lookup("Hello");
			
			System.out.println(stub.sayHello());
		} catch(RemoteException e) {
			e.printStackTrace();
		} catch(NotBoundException e) {
			e.printStackTrace();
		}
	}
}
