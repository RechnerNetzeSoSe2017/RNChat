package server.verteiler;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import javafx.util.Pair;
import server.protokol.HPCServer;
import server.util.message.Message;

/**
 * Der verteiler bekommt Nachrichten, und verteilt sie in die dazugeh�rigen R�ume. Jeder Raum ist wieder ein eigener Thread.
 * @version 1.0
 * @author H�ling
 *
 */
public class Verteiler<ID,MessageFrom,MessageTo> extends Thread {

	private ArrayList<Chatraum> raumListe = new ArrayList<Chatraum>();
	
	public static String welcomeMessage = "Have a nice day!";
	
	private LinkedBlockingQueue<Message> zuVerteilendeNachrichten = new LinkedBlockingQueue<>();
	
	private static Verteiler instanz = null;
	
	private String adresseAlleRaeume="";
	
	private boolean arbeiten = true;
	
	public Verteiler() {
		// erstmal 3 r�ume fix anlegen. dynamit�t sp�ter nachr�sten..
		
		instanz=this;
		
		raumListe.add(new Chatraum("Raum 1"));
		raumListe.add(new Chatraum("Raum 2"));
		
	}
	
	@Override
	public void run() {
		// 
		
		Message<MessageFrom,MessageTo> msg=null;
		while(arbeiten){
			
			try {
				msg=zuVerteilendeNachrichten.take();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
				msg=null;
			}
			if(msg != null){
				MessageTo recipient = msg.getTo();
				
				for(Chatraum raum : raumListe){
					if(recipient.equals(raum.getRoomName())){
						raum.addMessage(msg);
						break;
					}
				}
				
			}
			
		}
		
		
	}
	public static Verteiler getInstance(String name){
		return instanz;
	}
	/**
	 * F�gt das Element der abarbeitungs-warteschlange hinzu, sodass es in die r�ume verteilt werden kann.. 
	 * @param msg
	 */
	public void addMessage(Message msg){
		if(msg!=null){
			zuVerteilendeNachrichten.add(msg);
		}
	}
	
	/**
	 * Tr�gt den client in die Liste des Raumes ein, die als id �bergeben wird..
	 * @param subscribeToID
	 * @param client
	 */
	public boolean subscribe(ID subscribeToID, HPCServer client){
		
		boolean temp=false;
		
		for(Chatraum raum : raumListe){
			if(raum.getRoomName().equals(subscribeToID)){
				temp=raum.subscibe(client);
				break;
			}
		}
		return temp;
	}
	/**
	 * entfern den client aus dem verteiler f�r den entsprechenden raum..
	 * @param subscribeToID wenn null, dann wird bei ALLEN channels unsubscribed, ansonsten wird nach dem raum gesucht
	 * @param client
	 */
	public void unsubscribe(ID subscribeToID, HPCServer client){
		
			for(Chatraum raum : raumListe){
				
				if(subscribeToID == null){
					raum.unsubscribe(client);
				}else if(raum.getRoomName().equals(subscribeToID)){
					raum.unsubscribe(client);
					break;
				}
			}
		
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

	public void stopVerteilen() {
		arbeiten=false;
		
		for(Chatraum raum : raumListe){
			raum.stopWorking();
		}
		zuVerteilendeNachrichten.notifyAll();
		interrupt();
		
		
	} 
	@Override
	public synchronized void start() {
		// TODO Auto-generated method stub
		super.start();
		
		for(Chatraum raum: raumListe){
			raum.start();
		}
		
	}
	
}
