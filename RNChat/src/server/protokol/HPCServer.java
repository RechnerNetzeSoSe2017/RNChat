package server.protokol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import server.server.Chatserver;
import server.util.IDGenerator;
import server.util.message.Message;
import server.util.message.PayloadMessage;

/**
 * HPCS - HyperChatProtocolServer Die Serverseite des Chatprotokolls..
 * 
 * Handlet die Verbindung und kommunikation mit dem Client. Hier wird z.B. der
 * Modus (halbduplex, vollduplex,..) entschieden..
 * 
 * @author Höling
 *
 */
public class HPCServer implements Runnable {

	private int id;

	private String protocolVersion = "1.0";

	// 8*1024 -> 8192 byte. Das sind 8192 Zeichen. Die standardgröße des puffers
	// zum lesen. 8kB..
	private int buffersize = 8 * 1024;

	private int socketTimeout = 0;

	private Socket socket;
	private LinkedBlockingQueue<Message> output = new LinkedBlockingQueue<>();
	private LinkedBlockingQueue<Message> input = new LinkedBlockingQueue<>();

	private String verkehrsModusName = "CommunicationMode";
	private CommunicationMode verkehrsModus = CommunicationMode.FullDuplex;

	// eine HashMap für die optionen.
	private HashMap<String, String> optionen = new HashMap<>();
	private static HashMap<String, String> optionsListing = new HashMap<>();

	private BufferedReader in = null;
	private PrintWriter out = null;

	private boolean closeConnection = false;

	private String headerOKMessage = "<OK>";
	private String headerNOKMessage = "<NOK>";
	private String headerErrorMessage = "<ERROR>";
	private int headerErrorCount = 0;
	private int headerErrorMaxCount = 3;

	private OutputStreamThread<Message> outputThread;

	public HPCServer(Socket socket) {

		// wenn der übergebene Socket NULL ist, wird der konstruktor
		// abgebrochen.
		if (socket != null) {
			this.socket = socket;
			id = IDGenerator.getID();
		} else {
			return;
		}
		log("starting communication with client id " + id + " from " + socket.getInetAddress());

		// die optionen die voreingestellt werden
		optionen.put(verkehrsModusName, CommunicationMode.FullDuplex.toString().toLowerCase());

		// die Liste die alle möglichen Optionen beinhaltet
		optionsListing.put(verkehrsModusName, CommunicationMode.FullDuplex.toString());

		// ein Testeintrag
		optionsListing.put("Encryption", "NONE");

	}

	private void log(String string) {
		System.out.println("" + string);

	}

	@Override
	public void run() {
		// TODO informationsaustausch -> übermitteln:
		/*
		 * name des servers protokollversion id des clients optionen EOH - End
		 * Of Header
		 */

		Chatserver cs = Chatserver.getInstance("");

		try {
			socket.setSoTimeout(socketTimeout);
		} catch (SocketException e1) {
			// wenn auf dem darunter liegendem layer ein timeout probleme
			// verursacht..
			e1.printStackTrace();

		}

		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF8"), buffersize);
//			in = new BufferedReader(new InputStreamReader(socket.getInputStream()), buffersize);
			out = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			// TODO hier abbrechen, da hier keine reder oder writer geöffnet
			// werden können. dann weiter zu machen bringt eher nix.

			e.printStackTrace();
		}

		// der Name des Servers
		out.println("<ServerName>" + cs.getName());

		// version des Protokolls
		out.println("<Protokoll>HCP - HyperChatProtokoll " + protocolVersion);

		// die ClientID
		out.println("<ClientID>" + id);

		// Die größe der Nachricht
		out.println("<MessageSize>" + buffersize);

		// optionen
		sendOptions();

		// EOH
		out.println("<EOH>End Of Header");

		// dann wird vom Client gelesen. anhand der antworten wird entschieden
		// wie die kommunikation weiter geht
		readClientHeader();

		if (!closeConnection) {

			// anschliessend sind alle header durch und die kommunikation im
			// <Message>-Format kann beginnen..

			out.println("<OK>start message-format now!");

			if (optionen.get(verkehrsModusName).equals(CommunicationMode.FullDuplex.toString().toLowerCase())) {
				fullDuplexCommunikation();
			}
		}
		
		//ab hier läuft die kommunikation über einen anderen thread und es kann begommen werden die Nachriichten aus der
		// inputqueue zu verarbeiten.
		

	}

	public void closeConnection() {
		closeConnection = true;

		log("closing connection with client " + id + " from " + socket.getInetAddress());

		// die ID muss wieder frei gemacht werden..
		IDGenerator.freeID(id);

		if(outputThread!=null){
		// wenn modus vollduplex, dann die threads beenden
		outputThread.stopSend();
		output.add(new Message(1, id, new PayloadMessage("<bye>")));
		
		}

		try {
			in.close();
			out.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void readClientHeader() {
		// liest den header des clients
		String antwort = "h";

		boolean endOfHeader = false;

		// so lange vom client lesen bis er seinen header beendet.
		while (!closeConnection && !endOfHeader) {
			

			try {
				antwort = in.readLine();
				antwort=antwort.trim();
				antwort=antwort.toLowerCase();
				out.println(antwort);
			} catch (IOException e) {
				//Client hat einfach die verbindung abgebrochen
				e.printStackTrace();
			}

			if (checkHeader(antwort)) {

				headerErrorCount = 0;

				if (antwort.contains("<" + verkehrsModusName.toLowerCase() + ">")) {

					int lastpeak = antwort.lastIndexOf(">");

					String value = antwort.substring(lastpeak + 1);

					if (setTrafficMode(value)) {

						log("client sets " + verkehrsModusName + " to " + value);
						optionen.put(verkehrsModusName, value);
						out.println(headerOKMessage);
					} else {
						log("client trys to set " + verkehrsModusName + " to " + value);
						out.println(headerNOKMessage);
					}

				}

				else if (antwort.toLowerCase().startsWith("<bye>")) {

					closeConnection();

				}
				else if(antwort.startsWith("<eoh>")){
					log("client <eoh>");
					endOfHeader=true;
				}

				else {
					out.println(headerErrorMessage + antwort);
				}

			} else {

				headerErrorCount++;

				// ab hier ist die Nachricht kein header, bzw enspricht nicht
				// dem header format..
				out.println(headerErrorMessage + antwort);

				if (headerErrorCount > headerErrorMaxCount) {
					out.println("Closing connection, too many bad attemps..");
					closeConnection();
				}

			}

		}

	}

	/**
	 * prüft ob der modus zu vollduplex gesetzt werden kann
	 * 
	 * @param value
	 * @return true wenn die einstellung gesetzt wurde, false falls nicht
	 */
	private boolean setTrafficMode(String value) {

		if (value.equals(CommunicationMode.FullDuplex.toString().toLowerCase())) {
			return true;
		}
		return false;
	}

	/**
	 * prüft ob die nachricht vom client dem Format des headers entspricht. Eine
	 * Headernachricht vom client ist entweder {@code<eoh>} oder sie beginnt mit
	 * {@code<option>}
	 * 
	 * @param antwort
	 *            zu prüfender string
	 * @return true wenn ein bestandteil des Headers
	 */
	private boolean checkHeader(String antwort) {
		
		log(antwort);
		
		if (antwort.contains("<option>") || antwort.contains("<bye>") || antwort.contains("<eoh>")) {
			return true;
		}
		return false;

	}

	private String getFirstTag(String antwort) {

		if (antwort.contains(">")) {
			int endeTAG = antwort.indexOf(">");

			String temp = antwort.substring(0, endeTAG);

			return temp;
		}
		return "<error>";

	}

	/**
	 * startet doe fullduplex kommunikation.
	 */
	private void fullDuplexCommunikation() {
		// hier werden die neuen Threads erzeugt die auf beiden streams
		// gleichzeitig arbeiten
		
		outputThread=new OutputStreamThread(out, output);
		outputThread.start();

		// testausgabe
		output.add(new Message(1, id, new PayloadMessage("Welckome to here")));
		

	}

	/**
	 * sendet die Optionen die dieses Protokoll hat an den Client
	 * 
	 * @param out
	 */
	private void sendOptions() {

		// optionen sind
		Iterator it = optionen.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();

			out.println("<Option><" + pair.getKey() + ">" + pair.getValue());

		}

	}

	/**
	 * Legt die Nachricht in den Ausgangspuffer.
	 * 
	 * @param message
	 */
	public void sendMessage(Message message) {

		output.add(message);

	}

	/**
	 * liefert die ID des Clients.
	 * 
	 * @return ID
	 */
	public int getID() {
		return id;
	}

}
