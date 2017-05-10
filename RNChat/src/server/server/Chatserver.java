package server.server;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashSet;

import server.protokol.ChatSocketListener;
import server.verteiler.Verteiler;

/**
 * 
 * @version 1.0
 * @author Höling
 *
 */
public class Chatserver {

	private double version = 1.0;

	private int portNr = 33333;
	private String servername;// = "MyChatServer";

	private Serverstatus serverstatus = Serverstatus.STOPPED;
	private ServerSocket serverSocket;

	private ServerExecutor serverExecutor = ServerExecutor.FixedThredPool;
	private int maxClientThreads = 10;

	private ChatSocketListener protokoll;

	private HashSet<String> nicknames;

	private static ArrayList<Chatserver> chatserverList = new ArrayList<>();
	
	private Verteiler<String,String,String> verteiler;// = new Verteiler<String,String,String>();

	public Chatserver(String name, int port) {
		if (!name.equals("")) {
			servername = name;
		} else {
			servername = "MyChatServer";
		}

		portNr = port;

		chatserverList.add(this);

	}

	/**
	 * Startet den server, also horcht auf dem port und wartet auf
	 * verbindungsaufbau. Startet zudem den Verteiler in einem extra thread.
	 */
	public void startServer() {
		/*
		 * Startet den neuen Thread, ändert den status auf running
		 * 
		 */
		if (protokoll == null) {

			protokoll = new ChatSocketListener(portNr, maxClientThreads);
			log("Starting Server..");
			protokoll.start();

		}
		//den verteiler starten
		if(verteiler == null){
			verteiler = new Verteiler<String,String,String>();
			log("starte Verteiler..");
			verteiler.start();
		}

	}

	public void stopServer() {

		log("stopping Server "+servername);
		
		if (protokoll != null) {

			protokoll.stopListen();
			protokoll.interrupt();

		}
		if(verteiler != null){
			verteiler.stopVerteilen();
			
		}

	}

	/**
	 * Startet den Server und erstellt maximal so viele Threads für Clients wie
	 * angegeben
	 * 
	 * @param anzahlThreads
	 *            wie viele Clients maximal gleichzeitig bedient werden können.
	 */
	public void startServer(int anzahlThreads) {
		if (anzahlThreads > 0) {

			maxClientThreads = anzahlThreads;

		}
		startServer();

	}

	public void setExecutor(ServerExecutor exec) {
		if (exec != null) {
			serverExecutor = exec;
		}
	}

	private void log(String message) {
		System.out.println(""+message);
	}

	/**
	 * returnt die instanz eines nach dem namengesuchten servers
	 * 
	 * @param name
	 *            der Name des Servers der gesucht wird
	 * @return null wenn der Server nicht gefunden wurde
	 */
	public static Chatserver getInstance(String name) {
		// for (Chatserver elem : chatserverList) {
		// if(elem.getName().equals(name)){
		// return elem;
		// }
		// }
		// return null;
		return chatserverList.get(0);
	}

	public String getName() {
		return servername;
	}

}
