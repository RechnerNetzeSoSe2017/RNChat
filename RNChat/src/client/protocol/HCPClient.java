package client.protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.concurrent.LinkedBlockingQueue;

import client.gui.UIController;
import javafx.collections.ObservableList;
import javafx.scene.control.TextArea;
import javafx.util.Pair;
import server.protokol.InputStreamThread;
import server.protokol.OutputStreamThread;
import server.util.message.Message;
import server.util.message.MessageBuilder;

public class HCPClient extends Thread {

	// --------------------------------------------

	private UIController uiController;
	private LinkedBlockingQueue<String> outputQueue = new LinkedBlockingQueue<>();

	// ----------------------------------------------------------------------
	private BufferedReader in;
	private PrintWriter out;

	private Socket socket;
	private int portNr = 33333;
	private String serverAddr = "192.168.0.42";

	// -----------------TAGS als string----------------------
	private static String befehlsPraefix = "<";
	private static String befehlsSuffix = ">";

	private String befehlEOH = befehlsPraefix + "eoh" + befehlsSuffix;
	private String befehlOption = befehlsPraefix + "option" + befehlsSuffix;
	private String befehlClientID = befehlsPraefix + "clientid" + befehlsSuffix;
	private String befehlMessageSize = befehlsPraefix + "messagesize" + befehlsSuffix;

	// <MESSAGE>-Format
	// steht in den Methoden

	// -----------------------------------------------------

	private int messageSize = 8192;
	private int clientID = 0;
	// private LinkedBlockingQueue<E>

	private OutputStreamThread<String> outputThread;
	private InputStreamThread inputThread;
	private boolean communicate = true;

	private String nickname = "";
	private ObservableList raumliste;

	private HashMap<String, TextArea> chatraumFenster = new HashMap<>();
	private HashMap<String,ObservableList> nicknameFenster = new HashMap<>(); 

	private MessageBuilder<String, String> messageBuilder = new MessageBuilder<String, String>();

	/**
	 * erstellt eine neue Verbindung Mit dem Server der unter den angegebenen
	 * angaben erreichbar sein soll. Verbindung wird erst nach {@code .start()}
	 * aufgebaut.
	 * 
	 * @param host
	 *            als String {@link Socket }
	 * @param port
	 *            port zwischen 0 und 65535. Standard ist 33333
	 */
	public HCPClient(String host, int port) {
		if (port >= 0 && port < 65536) {
			portNr = port;
		}
		serverAddr = host;

		uiController = UIController.getInstance();

	}

	@Override
	public void run() {
		socket = null;

		try {
			// socket = new Socket(serverAddr, portNr);
			socket = new Socket(Inet4Address.getByName(serverAddr), portNr);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		boolean streamSuccess = true;

		if (socket != null && socket.isConnected()) {

			try {
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			} catch (IOException e) {

				streamSuccess = false;
				e.printStackTrace();
			}

			try {
				out = new PrintWriter(socket.getOutputStream(), true);
			} catch (IOException e) {
				streamSuccess = false;
				e.printStackTrace();
			}

			if (streamSuccess) {

				readServerHeader();

				// dem server mitteilen das der client nun den header beendet
				// hat..
				// out.println(befehlEOH);

				out.println("<nickname>" + nickname);
				String antwort = null;
				try {
					antwort = in.readLine();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					// e1.printStackTrace();
				}

				if (antwort != null && antwort.equals("<nok>")) {
					communicate = false;
					log("nickname war nicht OK");
					out.println("<bye>");
				}

				if (communicate) {

					// ab hier ist wieder provisorisch...
					outputThread = new OutputStreamThread(out, outputQueue);
					outputThread.start();

					// inputThread = new InputStreamThread(in, queue)

					while (!socket.isClosed() && communicate) {
						String clientString = null;
						try {
							
							clientString = in.readLine();
							
							if(clientString!=null){
								Message msg = messageBuilder.getFromString(clientString);
								
								if(msg!=null){
									
									
									
									
								}
								
							}
							
							
						} catch (SocketException se) {
//							communicate = false;
							clientString=null;
						}

						catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

				outputThread.stopSend();

			}

		}

	}

	// private String

	private void readServerHeader() {
		String antwort = "";

		try {
			antwort = in.readLine();
			antwort = antwort.trim();
			antwort = antwort.toLowerCase(Locale.GERMANY);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		log(antwort);

		while (!antwort.startsWith(befehlEOH)) {

			if (antwort.startsWith(befehlClientID)) {
				setClientID(antwort);
			} else if (antwort.startsWith(befehlOption)) {
				// parseOption(antwort);
			} else if (antwort.startsWith(befehlMessageSize)) {
				setMessageSize(antwort);
			}

			try {
				antwort = in.readLine();
				antwort = antwort.trim();
				antwort = antwort.toLowerCase(Locale.GERMANY);
				log(antwort);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	/**
	 * wird benutzt um als terminal zu dienen..
	 * 
	 * @param antwort
	 */
	private void log(String antwort) {
		uiController.message(antwort, 1);

	}

	// setMessagesize und setClientID sind doppeltzer code!!
	private void setMessageSize(String antwort) {

		messageSize = HCPClient.getNumber(antwort);

	}

	/**
	 * kann - wenn die letzte Information eine Nummer ist, diese extrahieren und
	 * zur�ckgeben. Sucht nach dem letzten vorkommen des Befehlssuffixes und
	 * liest ab dem Index+1 als Zahl..
	 * 
	 * @param message
	 * @return
	 */
	public static Integer getNumber(String message) {
		int index = message.indexOf(befehlsSuffix);

		String id = message.substring(index + 1);

		return Integer.parseInt(id);

	}

	private void parseOption(String antwort) {
		int index = antwort.indexOf(befehlsSuffix) + 1;

	}

	/**
	 * Setzt die von Server vergebene ClientID
	 * 
	 * @param antwort
	 */
	private void setClientID(String antwort) {

		clientID = HCPClient.getNumber(antwort);

	}

	public void sendMessage(String message, String receiverID) {
		
		Message msg = messageBuilder.newMessage(nickname, "Server", message);
		
		outputQueue.add(msg.toString());
	}

	/**
	 * liefert den {@code<from>} tag mit der ClientID
	 * 
	 * @return
	 */
	private String getFromTAG() {
		return befehlsPraefix + "from" + befehlsSuffix + clientID + befehlsPraefix + "/from" + befehlsSuffix;
	}

	private String getToTAG(int idReceiver) {
		return befehlsPraefix + "to" + befehlsSuffix + idReceiver + befehlsPraefix + "/to" + befehlsSuffix;
	}

	/**
	 * Schliesst die Verbindung
	 */
	public void closeConnection() {
		if (outputThread != null) {
			outputThread.stopSend();
		}

	}

	/**
	 * setzt den nicknamen f�r die zeit dieser sitzung. Es wird versucht sich
	 * mit diesem Namen mit dem Server zu verbinden
	 * 
	 * @param nickname
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;

	}

	/**
	 * Erwartet die ObservableList eines ListViews wo die chatr�ume eingetragen
	 * werden k�nnen..
	 * 
	 * @param roomlist
	 */
	public void setRoomlist(ObservableList roomlist) {
		raumliste = roomlist;
	}

	/**
	 * f�gt das ziel TextArea hinzu, damit ankommende Nachrichten auf dieser
	 * TextArea angezeigt werden k�nnen.
	 * 
	 * @param raumname
	 * @param area
	 */
	public void setTextareaForRoom(String raumname, TextArea area) {
		chatraumFenster.put(raumname, area);
	}
	/**
	 * setzt die nicklisten damit die bef�llt werden k�nnen
	 * @param raumname
	 * @param listview
	 */
	public void setListViewForNicklist(String raumname,ObservableList listview){
		nicknameFenster.put(raumname, listview);
	}

	public void removeTextArea(String name) {
		chatraumFenster.remove(name);
	}
	

	/**
	 * Teilt dem Server mit das ein bestimmter Raum subscribed werden soll
	 * 
	 * @param name
	 */
	public void subscribe(String name) {

		Message msg = messageBuilder.getSubscribe(nickname, "Server", name);

		outputQueue.add(msg.toString());

	}

	/**
	 * Teilt dem Server mit das ein Bestimmter Raum deabboniert werden soll
	 * 
	 * @param name
	 */
	public void unsubscribe(String name) {
		Message msg = messageBuilder.getUnsubscribe(nickname, "Server", name);
		outputQueue.add(msg.toString());
	}
	
	

}
