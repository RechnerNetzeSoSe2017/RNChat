package client.protocolother;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import javafx.collections.ObservableList;
import javafx.scene.control.TextArea;
import server.protokol.InputStreamThread;
import server.protokol.OutputStreamThread;

public class OtherProtocol extends Thread {

	private String host = "127.0.0.1";
	private int port = 3333;

	private String nickname = "";

	private Socket socket;

	private LinkedBlockingQueue<String> outputQueue = new LinkedBlockingQueue<>();
	private BufferedReader in = null;
	private PrintWriter out = null;

	private OutputStreamThread<String> outputThread;
	private InputStreamThread inputThread;
	private boolean communicate = true;

	private ObservableList raumliste;

	private HashMap<String, TextArea> chatraumFenster = new HashMap<>();
	private HashMap<String, ObservableList> nicknameFenster = new HashMap<>();

	private MainGuiController guiController = null;
	
	private boolean whisper=false;
	private boolean changeRoom=false;

	public OtherProtocol(String host) {
		this.host = host;
		guiController = MainGuiController.getInstance();

	}

	@Override
	public void run() {

		socket = null;
		
		boolean communikation=false;

		try {
			socket = new Socket(Inet4Address.getByName(host), port);
			// socket=SSLSocketFactory.getDefault().createSocket(host, 3333);
			socket.setSoTimeout(5000);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {

			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		
//		try {
//			log(in.readLine());
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
		// server sagt nix bis der client seinen usernamen sagt

		out.println(">:THIS>:" + nickname);

		String antwort = null;

		boolean skipToChat = false;

		try {
			// entweder antwortet der server mit "user erfolgreich eongeloggt"
			// oder er antwortet nicht
			antwort = in.readLine();
			
			log(antwort);
			
			if (antwort.contains("successfully")) {
				skipToChat = true;
				communikation=true;
			}
		} catch (IOException e) {

			e.printStackTrace();
		}

		log(antwort);

		if (!skipToChat) {
			// ggf nochmal den usernamen senden
			out.println(nickname);
			try {
				// hier sollte "you're trying to connect with an ununsual
				// client"
				antwort=in.readLine();
				log(antwort+"| skiptoChat");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//username nochmal senden
//			out.println(nickname);
//			guiController.log("client>"+nickname, 1);
			
			//der server müsste sagen das man sich entweder erfolgreich eingeloggt hat oder nicht
			
			
			
			
//			//timeout wieder auf 0 setzen
			try {
				socket.setSoTimeout(0);
			} catch (SocketException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			

			
		}
		
		
		
		
			
			outputThread=new OutputStreamThread<>(out, outputQueue);
			outputThread.start();
			
			//ab hier ist man erfolgreich eingeloggt
			boolean beenden=false;
			
			antwort=null;
			
			
			//hier die Raumliste holen und dann channellist(String) mit jedem namen aufrufen
			out.println("SHOWROOMS:>");
			
			
			try {
				//raumliste verarbeiten
				antwort=in.readLine();
				
				log(antwort);
				setRaeume(antwort);
				
				
				sendeAnRaum("USERS:>", "");
				
				antwort=in.readLine();
				log(antwort);
				
				setUsers(antwort);
				
				
				
				
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			while(!beenden){
				//lesen bis der client beendet
				
				try {
					antwort=in.readLine();
					log(antwort);
				} catch (IOException e) {
					
					e.printStackTrace();
					antwort=null;
				}
				
				if(antwort!=null){
					parseNachricht(antwort);
				}
				
			}
			
			
		
		
		
		
	}
	private void setRaeume(String raeume){
		String[] tmp = raeume.split(" \\| ");
		
		if(tmp.length>1){
		for(String elem : tmp){
			if(!elem.equals("")){
				guiController.addToChannellist(elem);
			}
		}
		}
	}
	private void setUsers(String usernamen){
		String[] tmp = usernamen.split(" \\| ");
		
		if(tmp.length>1){
		for(String elem : tmp){
			if(!elem.equals("")){
				
				guiController.addNickToNicklist("lobby", elem);
			}
		}
		}
	}
	/**
	 * parst die ankommende Nachricht
	 * @param input
	 */
	private void parseNachricht(String input){
		
		if(!changeRoom){
			guiController.messageToChat("", "", input);
		}else{
			if(input.contains("\\|")){
				setUsers(input);
			}
		}
		
		
	}
	/**
	 * fügt einen namen zur kanalliste hinzu
	 * @param name
	 */
	private void channellist(String name){
		guiController.addToChannellist(name);
	}
	
	
	private void sendeChatnachricht(String chatnachricht){
		
		if(out!=null){
			out.println(chatnachricht);
		}
		
	}

	/**
	 * Wechselt den in den Raum
	 * 
	 * @param raumname
	 */
	public void wechsleRaum(String raumname) {

		out.println("JOIN:>"+raumname);
//		outputQueue.add("JOIN:>"+raumname);
		changeRoom=true;
		out.println("USERS:>");
		
	}
	


	/**
	 * Flüstert den entsprechenden user an
	 * 
	 * @param name
	 */
	public void anfluestern(String name) {

		out.println("WHISPER:>"+name);
		
	}

	/**
	 * beendet den flüstermodus
	 */
	public void unwhisper() {

	}

	private void log(String log) {
		guiController.log("Server>" + log, 1);
	}

	public void setUsername(String name) {
		nickname = name;
	}

	public void closeConnection() {

		outputThread.stopSend();
	}
	public void sendeAnRaum(String nachricht,String raum){
		
		if(nachricht.contains("/w")){
			
			String[] tmp = nachricht.split(" ");
			
			if(tmp.length>1){
				out.println("WHISPER:>"+tmp[1]);
			}else{
				out.println("UNWHISPER:>");
			}
			
		}
		
		out.println(nachricht);
//		outputQueue.add(nachricht);
	}

}
