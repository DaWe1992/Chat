package lookup;

import java.io.*;
import java.net.*;

@SuppressWarnings("all")
public class LookupClient {
	private final String HOST = "localhost";
	private final int PORT = 7777;
	
	public void verbinden() {
		try {
			System.out.println("Bitte 'exit' eingeben, um Programm zu beenden!");
			while(true) {
				//Benutzereingabe abfragen
				BufferedReader userIn = new BufferedReader(
						new InputStreamReader(System.in));
				
				System.out.println("Bitte Suchbegriff eingeben:");
				String s = userIn.readLine();
				
				//Bei Eingabe von "exit" das Programm beenden
				if(s.equals("exit")) {
					break;
				}
				
				Socket socket = new Socket(HOST, PORT);
				BufferedReader networkIn = new BufferedReader(
						new InputStreamReader(socket.getInputStream()));
				PrintWriter networkOut = new PrintWriter(socket.getOutputStream());
				
				//Anfrage an Server stellen
				networkOut.println(s);
				networkOut.flush();
				
				//Ergebnis ausgeben
				while(true) {
					System.out.println(networkIn.readLine());
					if(!networkIn.ready()) break;
				}
			}
		} catch (IOException e) {
			System.out.println("Es konnte keine Verbindung hergestellt werden.");
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new LookupClient().verbinden();
	}
}
