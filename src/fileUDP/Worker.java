package fileUDP;

import java.net.*;
import java.io.*;

@SuppressWarnings("all")
public class Worker implements Runnable {
	private DatagramSocket s;
	private DatagramPacket p;
	private MyFile[] fileArray;

	/**
	 * Konstruktor.
	 * @param s
	 * @param p
	 * @param fileArray
	 */
	public Worker(DatagramSocket s, DatagramPacket p, MyFile[] fileArray) {
		this.s = s;
		this.p = p;
		this.fileArray = fileArray;
	}
	
	@Override
	/**
	 * Run-method for the threads.
	 */
	public void run() {
		String data = new String(p.getData(), 0, p.getLength());
		String answer = "";
		
		if(data.startsWith("READ")) {
			try {
				String[] tokens = data.split(" ", 2);
				tokens = tokens[1].split(",");
				
				String fileName = tokens[0];
				int lineNo = Integer.parseInt(tokens[1]);
				
				MyFile f = MyFile.getHandle(fileArray, fileName);
				
				f.startRead();
				answer = f.read(lineNo);
				f.endRead();
			} catch(Exception e) {e.printStackTrace();}
		} else { //data.startsWith("WRITE")
			try {
				String[] tokens = data.split(" ", 2);
				tokens = tokens[1].split(",");
				
				String fileName = tokens[0];
				int lineNo = Integer.parseInt(tokens[1]);
				String newData = tokens[2];
				
				MyFile f = MyFile.getHandle(fileArray, fileName);
				
				f.startWrite();
				answer = f.write(lineNo, newData);
				f.endWrite();
			} catch(Exception e) {e.printStackTrace();}
		}
		sendAnswer(answer);
	}
	
	/**
	 * Send answer back to client.
	 * @param answer
	 */
	private void sendAnswer(String answer) {
		try {
			DatagramPacket p2 = new DatagramPacket(answer.getBytes(), answer.length(),
				p.getAddress(), p.getPort());
			s.send(p2);
		} catch(IOException e) {e.printStackTrace();}
	}
}