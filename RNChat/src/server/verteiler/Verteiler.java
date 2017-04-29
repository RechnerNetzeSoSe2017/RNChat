package server.verteiler;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import javafx.util.Pair;
import server.protokol.HPCServer;
import server.util.message.Message;

/**
 * Der verteiler bekommt Nachrichten, und verteilt sie in die dazugehörigen Räume. Jeder Raum ist wieder ein eigener Thread.
 * @version 1.0
 * @author Höling
 *
 */
public class Verteiler extends Thread {

	private ArrayList<Chatraum> raumListe = new ArrayList<Chatraum>();
	
	public static String welcomeMessage = "Have a nice day!";
	
	private LinkedBlockingQueue<Message> zuVerteilendeNachrichten = new LinkedBlockingQueue<>();
	
	private static Verteiler instanz = null;
	
	
	public Verteiler() {
		// erstmal 3 räume fix anlegen. dynamität später nachrüsten..
		
		instanz=this;
		
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	public static Verteiler getInstance(String name){
		return instanz;
	}
	/**
	 * Fügt das Element der abarbeitungs-warteschlange hinzu, sodass es in die räume verteilt werden kann.. 
	 * @param msg
	 */
	public void addMessage(Message msg){
		if(msg!=null){
			zuVerteilendeNachrichten.add(msg);
		}
	}
	
	/**
	 * Trägt den client in die Liste des Raumes ein, die als id übergeben wird..
	 * @param subscribeToID
	 * @param client
	 */
	public void subscribe(int subscribeToID, HPCServer client){
		
	}
	/**
	 * entfern den client aus dem verteiler für den entsprechenden raum..
	 * @param subscribeToID
	 * @param client
	 */
	public void unsubscribe(int subscribeToID, HPCServer client){
		
	}
	
	/**
	 * Speichert die Ids und die namen in einer Liste aus Key/Value-Paaren..
	 * @return
	 */
	public ArrayList<Pair<Integer, String>> getRoomList(){
		
		ArrayList<Pair<Integer, String>> liste = new ArrayList<Pair<Integer, String>>();
		
		for(Chatraum elem : raumListe){
			
			liste.add(new Pair(elem.getID(), elem.getRoomName()));
			
		} 
		
		return liste;
	} 
	
}
