package web;

import java.io.*;
import java.net.*;

public class Webclient {
	private static final String DOMAIN = "www.google.de";
	private static final int PORT = 80;
	
	public static void main(String[] args) {
		try {
			Socket socket = new Socket(DOMAIN, PORT);
			BufferedReader networkIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter networkOut = new PrintWriter(socket.getOutputStream());
			
			//Localport
			System.out.println("Local Port: " + socket.getLocalPort());
			
			//Befehl an Server senden
			networkOut.println("GET / HTTP/1.1");
			networkOut.println("");
			
			//WICHTIG! Nachricht abschicken
			networkOut.flush();
			
			//Antwort lesen
			String s = "";
			while((s = networkIn.readLine()) != null)
				System.out.println(s);
			socket.close();
		}
		catch(UnknownHostException e) {
			e.printStackTrace();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
}
