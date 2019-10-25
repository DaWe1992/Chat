package chat;

import java.io.*;
import java.net.*;
import java.util.Vector;

@SuppressWarnings("all")
/**
 * ChatServer
 * @author Daniel Wehner
 *
 */
public class ChatServer {
	private Vector<PrintWriter> sockets = new Vector<>();
	
	public final static int PORT = 5000;
	
	/**
	 * Main-Methode
	 * @param args
	 */
	public static void main(String[] args) {
		new ChatServer().start();
	}
	
	/**
	 * Startet den ChatServer
	 */
	public void start() {
		try {
			ServerSocket serverSocket = new ServerSocket(PORT);
			System.out.println("Server running on port " + PORT);
			while(true) {
				try {
					Socket socket = serverSocket.accept();
					PrintWriter writer = new PrintWriter(socket.getOutputStream()); 
					sockets.add(writer);
					new Thread(new ClientHandler(socket, writer)).start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Leitet eingehende Nachrichten
	 * an alle Clients weiter
	 * @param msg
	 */
	private void broadcast(String msg) {
		for(PrintWriter writer : sockets) {
			writer.println(msg);
			writer.flush();
		}
	}
	
	/**
	 * Die Worker-Klasse ClientHandler
	 * verwaltet die Sockets für die Clients
	 * @author Daniel Wehner
	 *
	 */
	public class ClientHandler implements Runnable {
		private BufferedReader reader;
		private PrintWriter writer;
		private Socket socket;
		
		/**
		 * Konstruktor ClientHandler
		 * @param socket
		 */
		public ClientHandler(Socket socket, PrintWriter writer) {
			this.socket = socket;
			this.writer = writer;
			try {
				reader = new BufferedReader(
					new InputStreamReader(socket.getInputStream())
				);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			String msg;
			
			try {
				while((msg = reader.readLine()) != null) {
					String[] tokens = msg.split(";");
					
					switch(tokens[0]) {
					case "LEAVE":
						sockets.remove(this.writer);
						broadcast("INFO;" + sockets.size());
						broadcast(msg);
						break;
					case "JOIN":
						broadcast("INFO;" + sockets.size());
						broadcast(msg);
						break;
					default: //MSG
						broadcast(msg);						
						break;
					}
				}
			} catch (IOException e) {}
		}
	}
}