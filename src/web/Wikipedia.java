package web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

@SuppressWarnings("all")
/**
 * Wikipedia-Spiel. Gibt den Pfad von einem Wikipediaartikel
 * zu einem anderen Wikipediaartikel aus
 * @author Daniel Wehner
 *
 */
public class Wikipedia {
	public final static String ROOT = "https://de.wikipedia.org";
	private final Vector<Anchor> workList = new Vector<>();
	private final Vector<Anchor> finishedList = new Vector<>();
	private String start;
	private String end;
	
	/**
	 * Main-Methode
	 * @param args
	 */
	public static void main(String[] args) {
		//Übergebene Argumente auslesen
		if(args.length != 2) {
			System.err.println("Bitte geben Sie Start- und Zielseite an!");
		} else {
			String start = args[0];
			String end = args[1];
			new Wikipedia(start, end).start();
		}
	}
	
	/**
	 * Konstruktor Wikipedia
	 * @param start
	 * @param end
	 */
	public Wikipedia(String start, String end) {
		this.start = ROOT + "/wiki/" + start;
		this.end = ROOT + "/wiki/" + end;
	}
	
	/**
	 * Start des Spiels
	 */
	public void start() {
		Anchor a = new Anchor(null, start);
		workList.add(a);
		
		/*
		 * Durchlaufen der workList, solange
		 * die Zielseite noch nicht gefunden wurde
		 */
		while((a = workListContainsEndpage()) == null) {			
			getAnchors(workList.get(0));
			finishedList.add(workList.remove(0));
		}
		
		//Die Seite wurde gefunden
		System.out.println(getPath(a));
	}
	
	/**
	 * Gibt alle Anchors der Angebenen Seite zurück
	 * @param anchor
	 */
	private void getAnchors(final Anchor anchor) {
		
		try {
			URL url = new URL(anchor.getName());
			URLConnection con = url.openConnection();
			
			/*
			 * Definition eines Callbacks, der ausgeführt wird,
			 * wenn ein HTML-Tag gefunden wurde
			 */
			HTMLEditorKit.ParserCallback callback = new HTMLEditorKit.ParserCallback() {
				
				@Override
				public void handleStartTag(HTML.Tag t, 
							MutableAttributeSet a, int pos) {
					
					//Es handelt sich um ein Anchor-Tag
					if(t == HTML.Tag.A) {
						String href = (String) a.getAttribute(HTML.Attribute.HREF);
						
						//bestimmte Links herausfiltern
						if(href != null && href.length() >= 5 && 
								href.substring(1, 5).equals("wiki") && !href.contains(":")) {
							Anchor nextAnchor = new Anchor(anchor, ROOT + href);
							workList.add(nextAnchor);
						}
					}
				}
			};
			
			BufferedReader in = new BufferedReader(
					new InputStreamReader(con.getInputStream()));
			new ParserDelegator().parse(in, callback, false);
			in.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Prüft, ob die workList die Endpage beinhaltet
	 * @return
	 */
	private Anchor workListContainsEndpage() {
		for(Anchor a : workList) {
			if(a.getName().equals(end))
				return a;
		}
		return null;
	}
	
	/**
	 * Liefert den Pfad von der Startseite zur Zielseite zurück
	 * @param end
	 * @return
	 */
	private String getPath(Anchor end) {
		Anchor a = end;
		String result = "> " + end.getName() + "\n";
		
		while((a = a.getPreviousAnchor()) != null) {
			result += "> " + a.getName() + "\n";
		}
		
		return result;
	}
	
	/**
	 * Diese innere Klasse repräsentiert ein Link-Objekt, welches eine
	 * Verknüpfung zum aufrufenden Link besitzt
	 * @author Daniel Wehner
	 *
	 */
	private class Anchor {
		private Anchor previousAnchor;
		private String name;
		
		/**
		 * Konstruktor Anchor
		 * @param previousAnchor
		 * @param name
		 */
		public Anchor(Anchor previousAnchor,
				String name) {
			this.previousAnchor = previousAnchor;
			this.name = name;
		}
		
		/**
		 * Gibt den Anchor zurück, von dem aus dieser Anchor
		 * aufgerufen wurde
		 * @return
		 */
		public Anchor getPreviousAnchor() {
			return this.previousAnchor;
		}
		
		/**
		 * Gibt den Namen des aktuellen Anchors zurück
		 * @return
		 */
		public String getName() {
			return this.name;
		}

		private Wikipedia getOuterType() {
			return Wikipedia.this;
		}
	}
}