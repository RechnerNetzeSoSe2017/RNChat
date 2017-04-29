package server.verteiler;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import server.protokol.HPCServer;
import server.util.IDGenerator;
import server.util.message.Message;
import server.util.message.PayloadMessage;


/**
 * Der Raum auf einem Chatserver.
 * 
 * @version 1.0
 * @author Höling
 *
 */
public class Chatraum extends Thread {
	private String name;
	private int id;
	private LinkedBlockingQueue<Message> nachrichten = new LinkedBlockingQueue<>();
	
	private ArrayList<HPCServer> clientList = new ArrayList<>();
	
	private String welcomeMessage = "Wilkommen im Raum: ";
	
	public Chatraum(String name) {
		if(!name.equals("")){
			this.name=name;
		}
		
		id=IDGenerator.getID();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
	
	}
	
	/**
	 * Fügt den Client zur Subscriptionliste hinzu, wenn er != null ist.
	 * @param client darf NICHT null sein.
	 */
	public void subscibe(HPCServer client){
		if(client!=null){
			clientList.add(client);
			
			// TODO gescheites willkommensnachrichtensystem entwickeln
			
			client.sendMessage(new Message(id, client.getID(), new PayloadMessage(welcomeMessage())));
			
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
	public String getRoomName(){
		return name;
	}
	

}
