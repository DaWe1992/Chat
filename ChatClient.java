package chat;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.text.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.*;

/**
 * ChatClient
 * @author Daniel Wehner
 *
 */
public class ChatClient {
	public final static String HOST = "localhost";
	public final static int PORT = 5000;
	
	private JTextPane msgsIn;
	private JScrollPane scroller;
	private JTextField msgOut;
	private JTextField name;
	private JLabel lblParticipantsCount;
	
	private SimpleAttributeSet boldText;
	private SimpleAttributeSet joinText;
	private SimpleAttributeSet leaveText;
	
	private BufferedReader reader;
	private PrintWriter writer;
	private Socket socket;
	
	/**
	 * Main-Methode
	 * @param args
	 */
	public static void main(String[] args) {
		new ChatClient().start();
	}
	
	/**
	 * Initialisiert das GUI und
	 * startet den Client
	 */
	public void start() {
		initializeGUI();
		setUpNetwork();
		new Thread(new InputReader()).start();
	}
	
	/**
	 * Initialisiert die
	 * grafische Benutzeroberfläche
	 */
	private void initializeGUI() {
		try {
			UIManager.setLookAndFeel(
				"com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//JFrame erzeugen und formatieren
		JFrame frame = new JFrame("Chat Client");
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setBounds(100, 100, 560, 450);
		
		boldText = new SimpleAttributeSet();
		StyleConstants.setBold(boldText, true);
		joinText = new SimpleAttributeSet();
		StyleConstants.setItalic(joinText, true);
		StyleConstants.setForeground(joinText, Color.BLUE);
		leaveText = new SimpleAttributeSet();
		StyleConstants.setItalic(leaveText, true);
		StyleConstants.setForeground(leaveText, Color.RED);
		
		//JPanel erzeugen
		JPanel panel = new JPanel();
		panel.setLayout(null);
		
		//JLabels erzeugen
		JLabel lblName = new JLabel("Please insert your name:");
		lblName.setBounds(300, 45, 153, 14);
		JLabel lblMsg = new JLabel("Insert your message here:");
		lblMsg.setBounds(300, 129, 153, 14);
		lblParticipantsCount = new JLabel();
		lblParticipantsCount.setBounds(300, 10, 153, 14);
		
		//JTextPane erzeugen
		msgsIn = new JTextPane();
		msgsIn.setPreferredSize(new Dimension(255, 390));
		msgsIn.setEditable(false);
		
		//JScrollPane erzeugen und mit JTextArea verbinden
		scroller = new JScrollPane(msgsIn);
		scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroller.setBounds(25, 10, 255, 390);
		
		//JTextFields erzeugen
		name = new JTextField(20);
		name.setBounds(300, 67, 160, 30);
		
		msgOut = new JTextField(20);
		msgOut.setBounds(300, 149, 160, 30);
		msgOut.addKeyListener(new EnterKeyListener());
		
		//JButtons erzeugen
		JButton send = new JButton("Send");
		send.setBounds(300, 180, 90, 30);
		send.addActionListener(new SendButtonListener());
		send.setToolTipText("Send your message. Make sure you inserted a message.");
		
		JButton clear = new JButton("Clear");
		clear.setBounds(300, 241, 90, 30);
		clear.addActionListener(new ClearButtonListener());
		clear.setToolTipText("Click this button if you want to clear the chat history.");
		
		JButton exit = new JButton("Exit");
		exit.setBounds(445, 370, 90, 30);
		exit.addActionListener(new ExitButtonListener());
		exit.setToolTipText("Leave the chat.");
		
		//Elemente dem JPanel hinzufügen
		panel.add(scroller);
		panel.add(lblParticipantsCount);
		panel.add(lblName);
		panel.add(name);
		panel.add(lblMsg);
		panel.add(msgOut);
		panel.add(send);
		panel.add(clear);
		panel.add(exit);
		
		//JPanel dem JFrame hinzufügen
		frame.getContentPane().add(panel);
		
		//Frame sichtbar machen
		frame.setVisible(true);
		name.requestFocus();
	}
	
	/**
	 * Aufbau des Netzwerks
	 */
	private void setUpNetwork() {
		try {
			socket = new Socket(HOST, PORT);
			reader = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream());
			
			//Nachricht senden, dass ein neuer Teilnehmer da ist
			writer.println("JOIN;~~ Someone joined the conversation.");
			writer.flush();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ActionListener für den
	 * send Button
	 * @author Daniel Wehner
	 *
	 */
	public class SendButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			send();
		}	
		
		public void send() {
			String userName = name.getText();
			String msg = msgOut.getText();
		
			//Prüfen, ob ein Name eingegeben wurde
			if(userName == null || userName.equals("")) {
				JOptionPane.showMessageDialog(null,
						"Please insert a name");
			}
			
			//Prüfen, ob eine Nachricht eingegeben wurde
			else if(msg == null || msg.equals("")) {
				JOptionPane.showMessageDialog(null,
						"You haven't entered a message.");
			} else {
				String now = new SimpleDateFormat("HH.mm").format(new Date());
				writer.println("MSG;" + now + " " + userName + ": " + msg);
				writer.flush();
				
				msgOut.setText("");
				msgOut.requestFocus();		
			}
		}
	}
	
	/**
	 * ActionListener für den<
	 * clear Button
	 * @author Daniel Wehner
	 *
	 */
	public class ClearButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			msgsIn.setText("");
		}
	}
	
	/**
	 * ActionListener für den
	 * exit Button
	 * @author Daniel Wehner
	 *
	 */
	public class ExitButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			writer.println("LEAVE;~~ " + name.getText() + " has left the conversation.");
			writer.flush();
			System.exit(0);
		}	
	}
	
	/**
	 * Wird aufgerufen, wenn
	 * eine Taste gedrückt wurde
	 * @author Daniel Wehner
	 *
	 */
	public class EnterKeyListener implements KeyListener {
		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_ENTER) {
				new SendButtonListener().send();
			}
		}

		@Override public void keyReleased(KeyEvent e) {}
		@Override public void keyTyped(KeyEvent e) {}
	}

	/**
	 * Die Klasse InputReader kümmert sich um
	 * das Anzeigen der erhaltenen Nachrichten
	 * @author Daniel Wehner
	 *
	 */
	public class InputReader implements Runnable {
		@Override
		public void run() {
			String msg;
			
			try {
				while((msg = reader.readLine()) != null) {
					String[] tokens = msg.split(";");
					StyledDocument doc = msgsIn.getStyledDocument();
					
					/*
					 * Unterschiedliche Handhabung bei
					 * verschiedenen Nachrichtenarten
					 */
					switch(tokens[0]) {
					case "INFO":
						lblParticipantsCount.setText("Number of participants: " + 
								tokens[1]);
						break;
					case "JOIN":
						try {
							doc.insertString(doc.getLength(), tokens[1] + "\n", joinText);
						} catch (BadLocationException e1) {
							e1.printStackTrace();
						}
						break;
					case "LEAVE":
						try {
							doc.insertString(doc.getLength(), tokens[1] + "\n", leaveText);
						} catch (BadLocationException e1) {
							e1.printStackTrace();
						}
						break;
					default: //MSG
						try {
							tokens = tokens[1].split(":", 2);
							doc.insertString(doc.getLength(), tokens[0] + ":", boldText);
							doc.insertString(doc.getLength(), tokens[1] + "\n", null);
						} catch (BadLocationException e) {
							e.printStackTrace();
						}
						
						JScrollBar vScroll = scroller.getVerticalScrollBar();
						vScroll.setValue(vScroll.getMaximum());
						break;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}