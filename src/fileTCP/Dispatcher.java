package fileTCP;

import java.io.*;
import java.net.*;

@SuppressWarnings("all")
public class Dispatcher {
	private final static int DEF_PORT = 3000;
	private final FileMonitor mon = new FileMonitor();
	
	/**
	 * Main-method
	 * @param args
	 */
	public static void main(String[] args) {
		new Dispatcher().start();
	}
	
	/**
	 * Starts the server
	 */
	public void start() {
		try {
			ServerSocket serverSocket = new ServerSocket(DEF_PORT);
			System.out.println("Server running on port " + DEF_PORT);
			
			while(true) {
				try {
					Socket s = serverSocket.accept();
					BufferedReader networkIn = new BufferedReader(
					new InputStreamReader(s.getInputStream()));
					PrintWriter networkOut = new PrintWriter(s.getOutputStream());
					Worker w = new Worker(networkIn, networkOut, mon);
					new Thread(w).start();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
