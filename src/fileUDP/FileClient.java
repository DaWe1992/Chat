package fileUDP;

import java.net.*;
import java.io.*;

@SuppressWarnings("all")
public class FileClient {
	private final static String HOST = "127.0.0.1";
	private final static int PORT = 3000;
	private final static int MAX_SIZE = 100;
	
	/**
	 * Main-method.
	 * @param args
	 */
	public static void main(String[] args) {
		new FileClient().start();
	}
	
	/**
	 * Starts the file-client.
	 */
	public void start() {
		DatagramSocket s = null;
		DatagramPacket p = null;
		BufferedReader userIn = null;
		
		try {
			s = new DatagramSocket();
			userIn = new BufferedReader(new InputStreamReader(System.in));
			
			while(true) {
				System.out.println("Please enter command:");
				String sInput = userIn.readLine();
				if(sInput.equals("EXIT")) break;
				byte[] input = sInput.getBytes();
				p = new DatagramPacket(input, input.length, 
						InetAddress.getByName(HOST), PORT);
				s.send(p);
				
				//wait for response
				DatagramPacket p2 = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
				s.receive(p2);
				String res = new String(p2.getData(), 0, p2.getLength());
				System.out.println(res);
			}
		} catch(SocketException e) {
			e.printStackTrace();
		} catch(UnknownHostException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}