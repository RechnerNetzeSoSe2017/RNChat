package server.protokol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javafx.util.Pair;
import server.server.Chatserver;
import server.util.IDGenerator;
import server.util.Nameservice;
import server.util.message.Message;
import server.util.message.MessageBuilder;
import server.util.message.Payload;


import server.verteiler.Verteiler;

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

	private static final String headerOKMessage = "<OK>";
	private static final String headerNOKMessage = "<NOK>";
	private static final String headerErrorMessage = "<ERROR>";
	private int clientID;
	private String clientName="";
	private int serverID = 1;
	private String serverName =""+serverID;

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
	
	//hält eine referenz auf abbonierte raeume..
	private ArrayList<String> abbonierteRaeume = new ArrayList<>();

	private BufferedReader in = null;
	private PrintWriter out = null;

	private boolean closeConnection = false;

	private String channellistTAG ="<channellist>";
	private String subscribeTAG ="<subscribe>";
	private String unsubscribeTAG="<unsubscribe>";
	private String logoutTAG ="<logout>";

	private OutputStreamThread<Message> outputThread;
	private InputStreamThread inputThread;

	private Verteiler verteiler = Verteiler.getInstance("");

	// erlaubt es dem client Nachrichten die er an sich selbst addressiert
	// wieder zu empfangen.. nur für testzwecke
	private static boolean loopback = true;
	
	private Locale lowercaseLocale = Locale.GERMANY;
	private MessageBuilder messageBuilder = new MessageBuilder();
	private int headerErrorCount=0;
	private int headerErrorMaxCount=5;
	private String controlTAG = "<control>";
	private String messageTAG = "<message>";
	
	private Nameservice nslookup=Nameservice.getInstance("Chatsserver");
	
	private int timeoutattemps = 0;
	private int maxtimeoutAttemps=3;

	public HPCServer(Socket socket) {

		// wenn der übergebene Socket NULL ist, wird der konstruktor
		// abgebrochen.
		if (socket != null) {
			this.socket = socket;
			clientID = IDGenerator.getID();
		} else {
			return;
		}
		log("starting communication with client id " + clientID + " from " + socket.getInetAddress());

		// die optionen die voreingestellt werden
		optionen.put(verkehrsModusName, CommunicationMode.FullDuplex.toString().toLowerCase(lowercaseLocale));

		// die Liste die alle möglichen Optionen beinhaltet
		optionsListing.put(verkehrsModusName, CommunicationMode.FullDuplex.toString());

		// ein Testeintrag
		optionsListing.put("Encryption", "NONE");
		
		try {
			this.socket.setKeepAlive(true);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}

	}

	public static void setLoopback(boolean value) {
		loopback = value;
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
		} 
		catch (SocketException e1) {
			// wenn auf dem darunter liegendem layer ein timeout probleme
			// verursacht..
//			e1.printStackTrace();

		}

		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF8"), buffersize);
			// in = new BufferedReader(new
			// InputStreamReader(socket.getInputStream()), buffersize);
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
		out.println("<ClientID>" + clientID);

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

			if (optionen.get(verkehrsModusName).equals(CommunicationMode.FullDuplex.toString().toLowerCase(lowercaseLocale))) {
				fullDuplexCommunikation();
			}
		}

		// ab hier läuft die kommunikation über einen anderen thread und es kann
		// begommen werden die Nachriichten aus der
		// inputqueue zu verarbeiten.
		
		ChatSocketListener.removeClient(this);

	}

	public void closeConnection() {
		closeConnection = true;

		log("closing connection with client " + clientID + " from " + socket.getInetAddress());

		// die ID muss wieder frei gemacht werden..
		IDGenerator.freeID(clientID);

		if (outputThread != null) {
			// wenn modus vollduplex, dann die threads beenden
			outputThread.stopSend();
			output.add(new Message<String,String>(serverName, clientName, new Payload<String>("","<bye>","")));

		}

		if (inputThread != null) {
			inputThread.stopListen();
		}

		try {
			in.close();
			out.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}

		
		
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		

	}

	private void readClientHeader() {
		// liest den header des clients
		String antwort = "h";

		boolean endOfHeader = false;
		boolean setNickname = false;
		
		int fehler = 0;

		// so lange vom client lesen bis er seinen header beendet.
		while (!closeConnection && !endOfHeader) {

			try {
				antwort = in.readLine();
				antwort = antwort.trim();
				antwort = antwort.toLowerCase(lowercaseLocale);
				out.println(antwort);
			}catch(SocketException se){
				closeConnection();
				
			} 
			
			catch (IOException e) {
				// Client hat einfach die verbindung abgebrochen
				e.printStackTrace();
			}

			if (!closeConnection && checkHeader(antwort)) {

				headerErrorCount = 0;

				if (antwort.contains("<" + verkehrsModusName.toLowerCase(lowercaseLocale) + ">")) {

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

				else if (antwort.toLowerCase(lowercaseLocale).startsWith("<bye>")) {

					closeConnection();

				}
				
				
				else if (antwort.startsWith("<eoh>")) {
					log("client <eoh>");
					if(setNickname){
						endOfHeader = true;
					}else{
						out.println("<nok>Nickname not set");
					}
				}
				
				
				else if(antwort.contains("<nickname>")){
					int lastpeak = antwort.lastIndexOf(">");

					String value = antwort.substring(lastpeak + 1);
					
					if(value!=null && value.length()>0){
						if(nslookup.addName(value.trim())){
							out.println("<ok>");
							clientName=value;
							setNickname=true;
							
						}else{
							out.println("<nok>Nickname nicht verfuegbar");
						}
					}
					
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
	private String readfromClient(){
		
		String temp=null;
		
		try {
			temp=in.readLine();
		}catch(SocketTimeoutException ste){
			timeoutattemps++;
			if(timeoutattemps>=maxtimeoutAttemps){
				closeConnection=true;
				closeConnection();
			}
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return temp;

	}

	/**
	 * prüft ob der modus zu vollduplex gesetzt werden kann
	 * 
	 * @param value
	 * @return true wenn die einstellung gesetzt wurde, false falls nicht
	 */
	private boolean setTrafficMode(String value) {

		if (value.equals(CommunicationMode.FullDuplex.toString().toLowerCase(lowercaseLocale))) {
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

		if (antwort.contains("<option>") || antwort.contains("<bye>") || antwort.contains("<eoh>") || antwort.contains("<nickname>")) {
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

		outputThread = new OutputStreamThread(out, output);
		outputThread.start();

		inputThread = new InputStreamThread(in, input);
		inputThread.start();

		// testausgabe
//		output.add(new Message(serverName, clientName, new Payload("","Welckome to here","")));

		Message<String,String> msg = null;

		while (!closeConnection) {

			if (msg != null) {
//				log("clientmessage>\t"+msg.toString());
				
				msg.setFrom(clientName);
				
				
				List<Payload> plist = msg.getPayload().getPayloadList();
//				
//				System.out.println("präfix:"+msg.getPayload().getPrefix());
//				System.out.println("ganzes payload: "+msg.getPayload());
//				System.out.println("unter control:"+plist.get(0).getPrefix());

				// wenn die Nachricht einen < message > - tag enthält, wird
				// dieser nachricht noch die clientID zugewiesen (der client
				// kann ja betrügen..)
				// und gibt sie dann an den verteiler weiter der dann an die
				// entsprechenden räume verteilt..
				if (loopback) {
					output.add(msg);
				}
				if(msg.getPayload().getPrefix().contains(messageTAG )){
					verteiler.addMessage(msg);
				}
				//Wenn der server empfänger ist UND es ein controltag ist (Server hat keine message nachrichten)...
				else if(msg.getPayload().getPrefix().contains(controlTAG)){
					
					//wenn die raumliste angefragt wurde..
					
					Payload control = plist.get(0);
					
//					log("client message>\t"+control.toString());
					
					if(control.getPrefix().contains(channellistTAG)){
						ArrayList<Pair<Integer, String>> li = verteiler.getRoomList();
						
						for(Pair<Integer,String> p : li){
							output.add(messageBuilder.toClientChannelAdd(serverName, clientName, p.getValue()));
						}
						
					}else if(control.getPrefix().contains(subscribeTAG)){
					//subscribe
						String name = control.getPayloadList().get(0).toString();
						
						boolean erfolg = verteiler.subscribe(name, this);
						Message mess = messageBuilder.tcSubscribeResponse(name, clientName, "ok");
						output.add(mess);
						
					}else if(control.getPrefix().contains(unsubscribeTAG)){
					//subscribe
						
						verteiler.unsubscribe(null, this);
						Message mess = messageBuilder.tcUnsubscribeResponse(serverName, clientName, "ok");
						output.add(mess);
						
					}
					
				
					
					//und wenn logout übermittelt wurde..
					else if(control.getPrefix().equals(logoutTAG)){
						verteiler.unsubscribe(null, this);
						closeConnection();
					}
					
					
				}else{
					verteiler.addMessage(msg);
				}
				
				
				msg=null;

			}

			try {
				msg = input.poll(3, TimeUnit.SECONDS);//take();
			} catch (InterruptedException e) {
				// wenn interrupted beim warten auf elemente
				e.printStackTrace();
			}

		}

	}

//	/**
//	 * wenn die message für den server ist und ein {@code<control>}-TAG hat,
//	 * muss der server aktiv werden..
//	 * 
//	 * @param msg
//	 */
//	private void serverControl(Message msg) {
//
//		String temp = msg.getPayload().getContaining().toLowerCase(lowercaseLocale);
//		String message = msg.getPayload().getContaining();
//
//		if (temp.startsWith(payloadControlChannelTAG)) {
//			temp = temp.substring(payloadControlChannelTAG.length());
//
//			serverChannel(message);
//
//		}else if(temp.startsWith(nameserviceTAG)){
//			temp = temp.substring(nameserviceTAG.length());
//
//			serverNameService(message);
//		}
//
//	}

	/**
	 * wickelt alles ab was mit dem nameservice zu tun hat..
	 * @param message
	 */
	private void serverNameService(String message) {

		String temp = message.toLowerCase(lowercaseLocale).trim(); 
		
//		if(temp.startsWith(nickTAG)){
//			//feststellen ob der Nick verfügbar ist 
//		}
		
	}

//	/**
//	 * handlet alles was {@code <channel>}-TAGs hat..
//	 * 
//	 * @param temp
//	 */
//	private void serverChannel(String payload) {
//		
//		String temp = payload.toLowerCase(lowercaseLocale).trim();
//
//		// <subscribe> .... </subscribe>
//		if (temp.startsWith(subscribeTAG)) {
//
//			//subscribet einen raum
//			String id = getBetweenTAGs(subscribeTAG, subscribeTAGClose, payload);
//
//			if (id != null) {
//				int subscribeID = Integer.parseInt(id);
//
//				verteiler.subscribe(subscribeID, this);
//			}
//
//		} else if (temp.startsWith(unsubscribeTAG)) {
//			// <unsubscribe>...</unsubscribe>
//			//unsubscribet einen Raum
//
//			String id = getBetweenTAGs(unsubscribeTAG, unsubscribeTAGClose, payload);
//
//			if (id != null) {
//				int subscribeID = Integer.parseInt(id);
//
//				verteiler.unsubscribe(subscribeID, this);
//			}
//
//		} else if (temp.startsWith(listTAG)) {
//			// <list>
//			//sendet eine liste von Chaträumen an den Client
//
//			ArrayList<Pair<Integer, String>> liste = verteiler.getRoomList();
//			
//			for(Pair<Integer, String> elem : liste){
//				
//				PayloadControl newPayload = new PayloadControl(idTAG+elem.getKey()+idTAGClose+nameTAG+elem.getValue()+nameTAGClose);
//				Message toClient = new Message(serverID, clientID, newPayload);
//				
//				output.add(toClient);
//				
//				
//			}
//			
//		}
//
//	}

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
	 * Untersucht einen string nach den beiden TAGs und liefert was zwischen
	 * ihnen steht..
	 * 
	 * @param tagBegin
	 * @param tagEnd
	 * @param getFrom
	 * @return {@code null} wenn nichts gefunden werden kann
	 */
	private String getBetweenTAGs(String tagBegin, String tagEnd, String getFrom) {

		int startidex = getFrom.indexOf(tagBegin) + tagBegin.length();
		int startindexClose = getFrom.indexOf(tagEnd);

		if (startidex < startindexClose) {
			return getFrom.substring(startidex, startindexClose);
		}
		return null;
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
		return clientID;
	}
	public String getClientName(){
		return clientName;
	}

}
