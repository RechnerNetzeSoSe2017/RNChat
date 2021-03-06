package server.verteiler;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

import server.protokol.HPCServer;
import server.util.IDGenerator;
import server.util.message.Message;
import server.util.message.MessageBuilder;
import server.util.message.Payload;



/**
 * Der Raum auf einem Chatserver.
 * 
 * @version 1.0
 * @author H�ling
 *
 */
public class Chatraum extends Thread {
	private String name;
	private int id;
	private LinkedBlockingDeque<Message> nachrichten = new LinkedBlockingDeque<Message>();
	private Semaphore semaphore = new Semaphore(1, true);
	
	private ArrayList<HPCServer> clientList = new ArrayList<>();
	
	private String welcomeMessage = "Wilkommen im Raum: ";
	private boolean arbeiten=true;
	private String messageTAG = "<message>";
	private String controlTAG = "<control>";
	private String subscribeTAG ="<subscribe>";
	private String unsubscribeTAG="<unsubscribe>";
	
	MessageBuilder messageBuilder = new MessageBuilder<>();
	
	public Chatraum(String name) {
		if(!name.equals("")){
			this.name=name;
		}
		
		id=IDGenerator.getID();
	}
	
	@Override
	public void run() {
		// 
		
		Message msg = null;
		
		while(arbeiten){
			
			try {
				msg=nachrichten.take();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
				msg=null;
			}
			
			if(msg!=null){
				
				//tue dinge
				
				Payload<Payload> pl = msg.getPayload();
				
				
				if(pl.getPrefix().equals(messageTAG)){
System.out.println("chatraum, run> nachricht wird versand: "+msg );					
					sendToClient(null, msg);					
					
				}else if(pl.getPrefix().equals(subscribeTAG)){
					
				}
				
				
				
			}
			
			
		msg=null;	
		}
		
		
	
	}
	
	/**
	 * Sendet eine Nachricht an einen oder alle clients dieses verteilers.
	 * @param recipient wenn null, dann wird an alle gesendet, ansonsten wird versucht nur an den client mit dem namen zu senden
	 * @param msg
	 */
	private void sendToClient(String recipient, Message msg){
		
		try {
			semaphore.acquire();
			
			for(HPCServer clients : clientList){
				
				if(recipient == null){
					clients.sendMessage(msg);
				}else if(clients.getClientName().equals(recipient)){
					clients.sendMessage(msg);
					break;
				}
			}
			
			
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			
			//wenn DIESER Thread beim warten interrupted wurde, wird die nicht abgearbeitete Nachricht wieder an die 1. stelle gesetzt
			nachrichten.addFirst(msg);
			
		}finally{
			semaphore.release();
			
		}
		
	}
	
	/**
	 * F�gt den Client zur Subscriptionliste hinzu, wenn er != null ist.
	 * @param client darf NICHT null sein.
	 */
	public boolean subscibe(HPCServer client){
		if(client!=null){
			
			Message msg = messageBuilder.getNickadd(name, name, client.getClientName());
System.out.println("chatraum, subscribe> die nachricht die hinzugefuegt wird: "+msg);


			for(HPCServer clients : clientList){
				clients.sendMessage(msg);
			}
//			nachrichten.add(msg);
			
			clientList.add(client);
			
			// TODO gescheites willkommensnachrichtensystem entwickeln
			
//			client.sendMessage(new Message(id, client.getID(), new Payload(welcomeMessage())));
			
		}
		
		
		
		
		
		return true;
	}
	
	public synchronized void unsubscribe(HPCServer client){
	
		try {
			semaphore.acquire();
			
			
			if(client != null){
				 clientList.remove(client);
				
			}
			
			
			
			
		}
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}finally{
			semaphore.release();
		}
		Message msg = messageBuilder.getNickLeave(name, name, client.getClientName());
		
		System.out.println("chatraum, subscribe> die nachricht die hinzugefuegt wird: "+msg);


					for(HPCServer clients : clientList){
						clients.sendMessage(msg);
					}
		
		
	}
	
	private String welcomeMessage(){
		return "---------"+welcomeMessage+name+"--------\n"+Verteiler.welcomeMessage;
	}
	
	/**
	 * returnt die ID des raumes
	 * @return
	 */
	public int getID(){
		return id;
	}
	
//	public String getIDName(){
//		return name;
//	}
	/**
	 * Liefert den Namen des Chatraums
	 * @return
	 */
	public String getRoomName(){
		return name;
	}
	/**
	 * F�gt eine Nachricht zum verteiler des raumes hinzu
	 * @param msg
	 */
	public void addMessage(Message msg){
		nachrichten.add(msg);
	}

	public void stopWorking() {
		arbeiten=false;
		interrupt();
		
	}

	/**
	 * Liefert eine Liste mit allen Usernamen die Momentan im Raum sind
	 * @return null wenn keiner im Raum ist
	 */
	public ArrayList<String> getNicklist() {

		ArrayList<String> list = new ArrayList<>();
		
		for(HPCServer elem : clientList){
			list.add(elem.getClientName());
		}
		if(list.isEmpty()){
			return null;
		}
		return list;
	}
	

}
