package client.protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.concurrent.LinkedBlockingQueue;

import client.gui.UIController;
import server.protokol.OutputStreamThread;

public class HCPClient extends Thread {
	
	//---------------------------------temporäre hilfsvariablen..-----------
	
	private UIController uiController;
	private LinkedBlockingQueue<String> outputQueue = new LinkedBlockingQueue<>();
	
	//----------------------------------------------------------------------
	private BufferedReader in;
	private PrintWriter out;
	
	private Socket socket;
	private int portNr=33333;
	private String serverAddr = "localhost";
	
	//-----------------TAGS als string----------------------
	private static String befehlsPraefix = "<";
	private static String befehlsSuffix = ">";
	
	private String befehlEOH = befehlsPraefix+"eoh"+befehlsSuffix;
	private String befehlOption = befehlsPraefix+"option"+befehlsSuffix;
	private String befehlClientID = befehlsPraefix+"clientid"+befehlsSuffix;
	private String befehlMessageSize = befehlsPraefix+"messagesize"+befehlsSuffix;
	
	//<MESSAGE>-Format
	//steht in den Methoden

	
	//-----------------------------------------------------
	
	private int messageSize = 8192;
	private int clientID = 0;
//	private LinkedBlockingQueue<E>
	
	private OutputStreamThread outputThread;
	
	
	/**
	 * erstellt eine neue Verbindung Mit dem Server der unter den angegebenen angaben erreichbar sein soll. Verbindung wird erst nach {@code .start()} aufgebaut.
	 * @param host als String {@link Socket }
	 * @param port port zwischen 0 und 65535. Standard ist 33333
	 */
	public HCPClient(String host, int port) {
		if(port>=0 && port < 65536){
			portNr=port;
		}
		serverAddr=host;
		
		uiController = UIController.getInstance();
		
	}
	
	@Override
	public void run() {
		socket=null;
		
		try {
			socket = new Socket(serverAddr, portNr);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		boolean streamSuccess = true;
		
		if(socket!=null && socket.isConnected()){
			
			try {
				in= new BufferedReader(new InputStreamReader(socket.getInputStream()));
			} catch (IOException e) {
				
				streamSuccess=false;
				e.printStackTrace();
			}
			
			try {
				out=new PrintWriter(socket.getOutputStream(), true);
			} catch (IOException e) {
				streamSuccess=false;
				e.printStackTrace();
			}
			
			
			if(streamSuccess){
				
				readServerHeader();
				
				//dem server mitteilen das der client nun den header beendet hat..
				out.println(befehlEOH);
				
				
				//ab hier ist wieder provisorisch...
				outputThread = new OutputStreamThread(out, outputQueue);
				
				
				
				
			}
			
			
			
			
			
		}
		
		
	}

	private void readServerHeader() {
		String antwort="";
		
		try {
			antwort=in.readLine();
			antwort=antwort.trim();
			antwort=antwort.toLowerCase(Locale.GERMANY);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		log(antwort);
		
		while(!antwort.startsWith(befehlEOH)){
			
			
			
			if(antwort.startsWith(befehlClientID)){
				setClientID(antwort);
			}else if(antwort.startsWith(befehlOption)){
//				parseOption(antwort);
			}else if(antwort.startsWith(befehlMessageSize)){
				setMessageSize(antwort);
			}
			
			try {
				antwort=in.readLine();
				antwort=antwort.trim();
				antwort=antwort.toLowerCase(Locale.GERMANY);
				log(antwort);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
		
	}

	/**
	 * wird benutzt um als terminal zu dienen..
	 * @param antwort
	 */
	private void log(String antwort) {
		uiController.message(antwort, 1);
		
	}

	//setMessagesize und setClientID sind doppeltzer code!!
	private void setMessageSize(String antwort) {
		
		
		messageSize=HCPClient.getNumber(antwort);
		
	}
	
	/**
	 * kann - wenn die letzte Information eine Nummer ist, diese extrahieren und zurückgeben. Sucht nach dem letzten vorkommen des
	 * Befehlssuffixes und liest ab dem Index+1 als Zahl..
	 * @param message
	 * @return
	 */
	public static Integer getNumber(String message){
		int index = message.indexOf(befehlsSuffix);
		
		String id = message.substring(index+1);
		
		return Integer.parseInt(id);
		
	}

	private void parseOption(String antwort) {
		int index = antwort.indexOf(befehlsSuffix)+1;
		
		
		
		
	}

	/**
	 * Setzt die von Server vergebene ClientID
	 * @param antwort
	 */
	private void setClientID(String antwort) {
		
		
		clientID=HCPClient.getNumber(antwort);
		
	}
	public void sendMessage(String message, int receiverID){
		outputQueue.add(message);
	}
	/**
	 * liefert den {@code<from>} tag mit der ClientID
	 * @return
	 */
	private String getFromTAG(){
		return befehlsPraefix+"from"+befehlsSuffix+clientID+befehlsPraefix+"/from"+befehlsSuffix;
	}
	
	private String getToTAG(int idReceiver){
		return befehlsPraefix+"to"+befehlsSuffix+idReceiver+befehlsPraefix+"/to"+befehlsSuffix;
	}

}
