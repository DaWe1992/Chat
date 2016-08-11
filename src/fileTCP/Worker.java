package fileTCP;

import java.io.*;
import java.net.*;

@SuppressWarnings("all")
public class Worker implements Runnable {
	private final static String ROOT = "messages/";
	
	private BufferedReader r;
	private PrintWriter w;
	private FileMonitor m;
	
	/**
	 * Constructor Worker
	 * @param r
	 * @param w
	 * @param m
	 */
	public Worker(BufferedReader r, 
			PrintWriter w, FileMonitor m) {
		this.r = r;
		this.w = w;
		this.m = m;
	}

	@Override
	/**
	 * Run-method
	 */
	public void run() {
		try {
			String command = r.readLine();
			String[] tokens = command.split(",");
			String fileName = tokens[1];
			int lineNo = Integer.parseInt(tokens[2]);
			
			String res = null;
			
			if(command.startsWith("READ")) {				
				m.startRead();
				try {
					Thread.sleep(10000);
				} catch (Exception e) {e.printStackTrace();}
				res = read(fileName, lineNo);
				m.endRead();
				
				w.println(res);
				w.flush();
			} else { //WRITE
				String data = tokens[3];
				
				m.startWrite();
				try {
					Thread.sleep(10000);
				} catch (Exception e) {e.printStackTrace();}
				res = write(fileName, lineNo, data);
				m.endWrite();
				
				w.println("Changed Data: " + res);
				w.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Read file
	 * @param fileName
	 * @param lineNo
	 * @return
	 */
	public String read(String fileName, int lineNo) {
		String s = "";
		String answer = "";
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new FileReader(ROOT + fileName));
			
			for(int i = 0; i < lineNo && s != null; i++)
				s = reader.readLine();
			if(s != null)
				answer = s;
			else throw new IllegalArgumentException("No such linenumber.");				
		} catch(IOException e) {e.printStackTrace();}
		if(reader != null) {
			try {
				reader.close();
			} catch(IOException e) {e.printStackTrace();}
		}
		return s;
	}
	
	/**
	 * Write file
	 * @param fileName
	 * @param lineNo
	 * @param data
	 * @return
	 */
	public String write(String fileName, int lineNo, String data) {
		BufferedReader reader = null;
		PrintWriter writer = null;
		String s = "";
		boolean found = false;
		
		try {
			reader = new BufferedReader(new FileReader(ROOT + fileName));
			writer = new PrintWriter(new FileWriter(ROOT + fileName + ".tmp"));
			
			for(int i = 1; s != null; i++) {
				s = reader.readLine();
				if(i == lineNo) {
					found = true;
					writer.println(data);
				}
				else if(s != null)
					writer.println(s);
			}
		} catch(IOException e) {e.printStackTrace();}
		try {
			if(reader != null) reader.close();
			if(writer != null) writer.close();
		} catch(IOException e) {e.printStackTrace();}
		
		if(found) {
			renameFiles(fileName);
			return data;
		}
		else
			return "ERR: Error occured.";
	}
	
	/**
	 * Rename files
	 * @param fileName
	 */
	private void renameFiles(String fileName) {
		File f1 = new File(ROOT + fileName);
		File f2 = new File(ROOT + fileName + ".tmp");
		File f3 = new File(ROOT + fileName + ".bak");
		f3.delete();
		f1.renameTo(f3);
		f2.renameTo(f1);
	}
}