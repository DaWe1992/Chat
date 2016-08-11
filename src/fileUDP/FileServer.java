package fileUDP;

import java.net.*;
import java.io.*;

@SuppressWarnings("all")
public class FileServer {
	private final static int PORT = 3000;
	private final static int MAX_SIZE = 100;
	private MyFile[] fileArray = new MyFile[100];

	/**
	 * Main-method.
	 * @param args
	 */
	public static void main(String[] args) {
		new FileServer().start();
	}
	
	/**
	 * Starts the file-server.
	 */
	public void start() {
		DatagramSocket s = null;
		DatagramPacket p = null;
		
		try {
			s = new DatagramSocket(PORT);
			System.out.println("The Server is running on port " + PORT + ".");
		} catch(SocketException e) {e.printStackTrace();}
		
		while(true) {
			try {
				p = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
				s.receive(p);
				
				//Create Worker-Thread
				Worker w = new Worker(s, p, fileArray);
				new Thread(w).start();
			} catch(Exception e) {e.printStackTrace();}
		}
	}
}