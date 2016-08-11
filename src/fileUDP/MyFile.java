package fileUDP;

import java.io.*;

@SuppressWarnings("all")
public class MyFile {
	private final static String ROOT = "messages/";
	private String fileName;
	
	private int readCtr = 0;
	private int writeCtr = 0;
	private boolean activeWriter = false;
	
	/**
	 * Konstruktor.
	 * @param fileName
	 */
	public MyFile(String fileName) {
		this.fileName = fileName;
	}
	
	/**
	 * Returns a file-handle for the file specified.
	 * @param fileArray
	 * @param fileName
	 * @return Returns a file-handle for fileName.
	 * @throws Exception
	 */
	public static synchronized MyFile getHandle(MyFile[] fileArray, String fileName) 
			throws Exception {
		MyFile f = null;
		
		for(int i = 0; i < fileArray.length; i++) {
			if(i == fileArray.length)
				throw new Exception("ERR: Array-Overflow");
			if(fileArray[i] == null)
				fileArray[i] = new MyFile(fileName);
			if(fileArray[i].fileName.equals(fileName)) {
				f = fileArray[i];
				break;
			}
		}
		return f;
	}
	
	/**
	 * Thread starts to read the file.
	 */
	public synchronized void startRead() {
		while(activeWriter || writeCtr > 0)
			try {
				wait();
			} catch(Exception e) {e.printStackTrace();}
		readCtr++;
	}
	
	/**
	 * Thread is finished reading the file.
	 */
	public synchronized void endRead() {
		readCtr--;
		if(readCtr == 0)
			notifyAll();
	}
	
	/**
	 * Thread starts to write a file.
	 */
	public synchronized void startWrite() {
		writeCtr++; //register for writing
		while(activeWriter || readCtr > 0)
			try {
				wait();
			} catch(Exception e) {e.printStackTrace();}
		activeWriter = true;
	}
	
	/**
	 * Thread is finished writing the file.
	 */
	public synchronized void endWrite() {
		writeCtr--;
		activeWriter = false;
		notifyAll();
	}
	
	/**
	 * Returns the string at line lineNo.
	 * @param lineNo
	 * @return Returns the string at line lineNo.
	 */
	public String read(int lineNo) {
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
	 * Writes data into file at line lineNo.
	 * @param lineNo
	 * @param data
	 * @returns Returns updated line.
	 */
	public String write(int lineNo, String data) {
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
			renameFiles();
			return data;
		}
		else
			return "ERR: Error occured.";
	}
	
	/**
	 * Renames the files.
	 */
	private void renameFiles() {
		File f1 = new File(ROOT + fileName);
		File f2 = new File(ROOT + fileName + ".tmp");
		File f3 = new File(ROOT + fileName + ".bak");
		f3.delete();
		f1.renameTo(f3);
		f2.renameTo(f1);
	}
	
	/**
	 * Returns the file name for the file.
	 * @return Returns the file name for the file.
	 */
	public String getFileName() {
		return this.fileName;
	}
}