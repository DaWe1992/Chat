package lookup;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class LookupServer {
	private int port;
	private ServerSocket serverSocket;
	private final ArrayList<String> begriffe = new ArrayList<>();
	
	/**
	 * Konstruktor
	 * @param port
	 */
	public LookupServer(int port) {
		try {
			this.port = port;
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Server ready and listending on port " + 
				port);
	}
	
	public int getServerPort() {
		return this.port;
	}
	
	/**
	 * Startet den Webserver und baut
	 * Sockets zu anfragenden Clients auf.
	 */
	public void start() {
		begriffeLaden();
		while(true) {
			try {
				Socket socket = serverSocket.accept();
				
				BufferedReader networkIn = new BufferedReader(
						new InputStreamReader(socket.getInputStream()));
				PrintWriter networkOut = new PrintWriter(socket.getOutputStream());
				
				//Auf Suchanfragen antworten
				String begriff = networkIn.readLine();
				System.out.println(new Date() + " || Folgender Begriff wurde gesucht: " + begriff);
				
				Random r = new Random();
				
				int anzahl = r.nextInt(5) + 1;
				networkOut.println("Ihr Suchergebnis:");
				
				for(int i = 0; i < anzahl; i++) {
					networkOut.println(begriffe.get(
							r.nextInt(begriffe.size())));
				}
				networkOut.println();
				networkOut.flush();
				
				//Streams und Socket schließen
				networkIn.close();
				networkOut.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Lädt die Begriffe aus der Datei
	 * begriffe.txt und speichert sie
	 * in der ArrayList begriffe
	 */
	private void begriffeLaden() {
		try {
			BufferedReader datei = new BufferedReader(new FileReader("begriffe.txt"));
			
			//Begriffe aus Datei lesen und
			//in der ArrayList begriffe speichern
			String begriff;
			while((begriff = datei.readLine()) != null) {
				begriffe.add(begriff);
			}
			
			datei.close();
		} catch(IOException e) {
			System.out.println("Die Datei begriffe.txt konnte nicht gelesen werden.");
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new LookupServer(7777).start();
	}
}