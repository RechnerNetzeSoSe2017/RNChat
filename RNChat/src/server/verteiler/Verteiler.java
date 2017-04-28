package server.verteiler;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

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
	
	public Verteiler() {
		// erstmal 3 räume fix anlegen. dynamität später nachrüsten..
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
}
