package fileTCP;

import java.net.*;
import java.io.*;

@SuppressWarnings("all")
public class Client {
	private final static String HOST = "localhost";
	private final static int PORT = 3000;
	
	/**
	 * Main-method
	 * @param args
	 */
	public static void main(String[] args) {
		new Client().start();
	}
	
	/**
	 * Starts the client
	 */
	public void start() {
		try {
			Socket s = new Socket(HOST, PORT);
			
			BufferedReader networkIn = new BufferedReader(
					new InputStreamReader(s.getInputStream()));
			PrintWriter networkOut = new PrintWriter(s.getOutputStream());
			BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
			
			while(true) {
				String command = userIn.readLine();
				if(command.equals("EXIT)")) break;
				networkOut.println(command);
				networkOut.flush();
				
				System.out.println(networkIn.readLine());
			}
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}