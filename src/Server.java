import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Class Server.
 * @author Daniel
 *
 */
public class Server implements IHello {

	/**
	 * Constructor.
	 */
	public Server() {}
	
	/**
	 * Implementation of remote method.
	 */
	@Override
	public String sayHello() throws RemoteException {
		return "Hello World!";
	}
	
	/**
	 * Main method.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Server server = new Server();
			
			//export remote object
			IHello stub = (IHello) UnicastRemoteObject.exportObject(server, 0);
			
			//bind to registry
			Registry registry = LocateRegistry.createRegistry(1099);
			registry.bind("Hello", stub);
			
			System.out.println("Server ready");
		} catch(RemoteException e) {
			e.printStackTrace();
		} catch(AlreadyBoundException e) {
			e.printStackTrace();
		}
	}
}
