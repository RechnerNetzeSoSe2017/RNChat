package client.protocolother;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.Socket;
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

public class OtherProtocol extends Thread{

	private String host="127.0.0.1";
	private int port=3333;
	
	private String nickname="";
	
	private Socket socket;
	
	private LinkedBlockingQueue<String> outputQueue = new LinkedBlockingQueue<>();
	private BufferedReader in=null;
	private PrintWriter out=null;
	
	private OutputStreamThread<String> outputThread;
	private InputStreamThread inputThread;
	private boolean communicate = true;
	
	private ObservableList raumliste;

	private HashMap<String, TextArea> chatraumFenster = new HashMap<>();
	private HashMap<String, ObservableList> nicknameFenster = new HashMap<>();


	private MainGuiController guiController=null;
	
	public OtherProtocol(String host) {
		this.host=host;
		guiController=MainGuiController.getInstance();
		
	}
	
	@Override
	public void run() {


		socket=null;
		
		try {
//			socket= new Socket(Inet4Address.getByName(host), port);
			socket=SSLSocketFactory.getDefault().createSocket(host, 3333);
			socket.setSoTimeout(5000);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		try {
			
			in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//wenn bis hier alles gut, dann kann die kommunikation beginnen..
	
		out.println(">:THIS>:"+nickname);
		
		String antwort=null;
		
		try {
			antwort=in.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		log(antwort);
		
		super.run();
	}
	
	/**
	 * Wechselt den in den Raum
	 * @param raumname
	 */
	public void wechsleRaum(String raumname){
		
	}
	
	/**
	 * Flüstert den entsprechenden user an
	 * @param name
	 */
	public void anfluestern(String name){
		
	}
	
	/**
	 * beendet den flüstermodus
	 */
	public void unwhisper(){
		
	} 
	private void log(String log){
		guiController.log("Server>"+log, 1);
	}
	public void setUsername(String name){
		nickname=name;
	}
	public void closeConnection(){
		
	}
	
	
}
