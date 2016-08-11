package fileTCP;

@SuppressWarnings("all")
public class FileMonitor {
	private int read_ctr;
	private int write_ctr;
	private boolean activeWriter;
	
	//LESERPRIORITAET
	/*
	public synchronized void startRead() {
		read_ctr++;
		while(activeWriter) {
			try {
				wait();
			} catch (Exception e) {e.printStackTrace();}
		}
	}
	
	public synchronized void endRead() {
		read_ctr--;
		if(read_ctr == 0) {
			notifyAll();
		}
	}
	
	public synchronized void startWrite() {
		while(activeWriter || read_ctr > 0) {
			try {
				wait();
			} catch (Exception e) {e.printStackTrace();}
		}
		activeWriter = true;
	}
	
	public synchronized void endWrite() {
		activeWriter = false;
		notifyAll();
	}
	*/
	//SCHREIBERPRIORITAET
	
	public synchronized void startRead() {
		while(activeWriter || write_ctr > 0) {
			try {
				wait();
			} catch (Exception e) {e.printStackTrace();}
		}
		read_ctr++;
	}
	
	public synchronized void endRead() {
		read_ctr--;
		if(read_ctr == 0) {
			notifyAll();
		}
	}
	
	public synchronized void startWrite() {
		write_ctr++;
		while(activeWriter || read_ctr > 0) {
			try {
				wait();
			} catch (Exception e) {e.printStackTrace();}
		}
		activeWriter = true;
	}
	
	public synchronized void endWrite() {
		if(write_ctr == 0) {
			activeWriter = false;
		}
		notifyAll();
	}
}