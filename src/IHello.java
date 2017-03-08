import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface IHello.
 * @author Daniel
 *
 */
public interface IHello extends Remote {
	public String sayHello() throws RemoteException;
}
