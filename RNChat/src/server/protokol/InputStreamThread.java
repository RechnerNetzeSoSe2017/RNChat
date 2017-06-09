package server.protokol;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.LinkedBlockingQueue;

import server.util.message.Message;
import server.util.message.MessageBuilder;
import server.util.message.Payload;
import zgh.PayloadControl;

public class InputStreamThread extends Thread {

	private BufferedReader in = null;
	private LinkedBlockingQueue<Message> input;
	private int clientID;

	private boolean listen = true;

	private Locale lowercaseLocale = Locale.GERMANY;

	private static String befehlsPraefix = "<";
	private static String befehlsSuffix = ">";

	private String tagFrom = befehlsPraefix + "from" + befehlsSuffix;
	private String tagFromClose = befehlsPraefix + "/from" + befehlsSuffix;
	private String tagTo = befehlsPraefix + "to" + befehlsSuffix;
	private String tagToClose = befehlsPraefix + "/to" + befehlsSuffix;
	private String tagMessage = befehlsPraefix + "message" + befehlsSuffix;
	private String tagMessageClose = befehlsPraefix + "/message" + befehlsSuffix;
	private String tagControl = befehlsPraefix + "control" + befehlsSuffix;
	private String tagControlClose = befehlsPraefix + "/control" + befehlsSuffix;

	private MessageBuilder payloadBuilder = new MessageBuilder();
	private HPCServer server = null;

	/**
	 * Erstellt einen neuen Thread, der auf dem InputStream liest und prüft ob
	 * die übermittelte Nachricht dem {@code<message>}-Format entspricht. Wenn
	 * nicht, werden die Nachrichten verworfen..
	 * 
	 * @param inStream
	 * @param queue
	 */
	public InputStreamThread(BufferedReader inStream, LinkedBlockingQueue queue) {

		in = inStream;
		input = queue;
		

	}
	public InputStreamThread(BufferedReader inStream, LinkedBlockingQueue queue,HPCServer hpcServer) {

		in = inStream;
		input = queue;
		server=hpcServer;

	}

	@Override
	public void run() {
		// liest, checkt ob im <message>-format und legt die message in den
		// puffer..

		String clientNachricht = "";

		try {
			clientNachricht = in.readLine();
			System.out.println("Nachricht vom Client> "+clientNachricht);
			

		} catch (IOException e) {
			// wenn aus irgendeinem grund der Stream nicht gelesen werden kann..
			// e.printStackTrace();
			// listen=false;
			clientNachricht=null;
		}

		while (listen) {

			
			if (clientNachricht != null) {
System.out.println("----\ninputThread, run> bevor der string geparst wird:"+clientNachricht);				
				Message<String, String> message = payloadBuilder.getFromString(clientNachricht);
				

				if (message != null) {
					input.add(message);
System.out.println("inputstreamthread, run> die geparste nachricht: "+message.toString());
					clientNachricht=null;
				}

			}
			try {
				
				clientNachricht = in.readLine();
System.out.println("---------------------\ninputStreamThread, run> eingabe des clients:"+clientNachricht);
				
				//in.readline liefert null wenn keine verbindung mehr besteht
				if(clientNachricht==null){
					listen=false;
					
					if(server!=null){
						server.closeConnection();
					}
					
					
				}
			} catch (IOException e) {
				// wenn aus irgendeinem grund der Stream nicht gelesen werden
				// kann..
				// z.b. wenn man ihn schliesst...
				// e.printStackTrace();
				clientNachricht=null;
			}

		}
	}

	// /**
	// * Liefert die ID aus dem {@code<message>...</message>}-TAG oder
	// {@code<coltrol>...</control>}-TAG.
	// * Da bereits schon vorher auf das ensprechende format getestet wurde,
	// kann hier gewiss sein das es eines der beiden tags enthalten ist.
	// * @param clientNachricht
	// * @return Die Payload als Objekt
	// */
	// private Payload getPayload(String clientNachricht) {
	//
	// String temp = clientNachricht.toLowerCase(lowercaseLocale);
	// Payload payload=null;
	//
	// int startidex = 0;
	// int startindexClose = 0;
	//
	// if(temp.contains(tagMessage)){
	//
	// startidex= temp.indexOf(tagMessage)+tagMessage.length();
	// startindexClose = temp.indexOf(tagMessageClose);
	//
	// payload = new Payload(clientNachricht.substring(startidex,
	// startindexClose));
	//
	//
	// }else if(temp.contains(tagControl)){
	// startidex = temp.indexOf(tagControl)+tagControl.length();
	// startindexClose = temp.indexOf(tagControlClose);
	//
	// payload = new PayloadControl(clientNachricht.substring(startidex,
	// startindexClose));
	//
	// }
	//
	//
	//
	//
	//
	//
	//
	// return null;
	// }
	//
	// /**
	// * Liefert die ID aus dem {@code<to>...</to>}-TAG.
	// * @param clientNachricht
	// * @return
	// */
	// private int getToID(String clientNachricht) {
	//
	//
	// String temp = clientNachricht.toLowerCase(lowercaseLocale);
	//
	// int startidex = temp.indexOf(tagTo)+tagTo.length();
	// int startindexClose = temp.indexOf(tagToClose);
	//
	//
	// return Integer.parseInt(clientNachricht.substring(startidex,
	// startindexClose));
	// }
	//
	// /**
	// * Liefert die ID aus dem {@code<from>...</from>}-TAG.
	// * @param clientNachricht
	// * @return
	// */
	// private int getFromID(String clientNachricht) {
	// String temp = clientNachricht.toLowerCase(lowercaseLocale);
	//
	// int startidex = temp.indexOf(tagFrom)+tagFrom.length();
	// int startindexClose = temp.indexOf(tagFromClose);
	//
	//
	// return Integer.parseInt(clientNachricht.substring(startidex,
	// startindexClose));
	// }
	//
	// /**
	// * überprüft ob die Nachricht im {@code <message>}-Format ist
	// * @param clientNachricht
	// * @return true wenn im Format, false falls nicht
	// */
	// private boolean isMessageFormat(String clientNachricht) {
	//
	//
	//
	// if(containsFromTAG(clientNachricht) && containsToTAG(clientNachricht) &&
	// containsPayload(clientNachricht)){
	// return true;
	// }
	//
	// return false;
	// }
	//
	// /**
	// * checkt ob der Übergebene String das {@code <message>}-TAG oder {@code
	// <control>}-TAG enthält
	// * @param clientNachricht
	// * @return
	// */
	// private boolean containsPayload(String clientNachricht) {
	// boolean contain = false;
	//
	// String temp = clientNachricht.toLowerCase().trim();
	//
	// if(temp.contains(tagMessage) && temp.contains(tagMessageClose)){
	// return true;
	// }else if(temp.contains(tagControl) && temp.contains(tagControlClose)){
	// return true;
	// }
	//
	// return false;
	// }
	//
	// /**
	// * checkt ob der Übergebene String das {@code <to>}-TAG enthält
	// * @param clientNachricht
	// * @return
	// */
	// private boolean containsToTAG(String clientNachricht) {
	//
	//
	// String temp = clientNachricht.toLowerCase().trim();
	//
	// if(temp.contains(tagTo) && temp.contains(tagToClose)){
	// return true;
	// }
	//
	// return false;
	// }
	//
	// /**
	// * checkt ob der Übergebene String das {@code <from>}-TAG enthält
	// * @param clientNachricht
	// * @return
	// */
	// private boolean containsFromTAG(String clientNachricht) {
	//
	//
	// String temp = clientNachricht.toLowerCase().trim();
	//
	// if(temp.contains(tagFrom) && temp.contains(tagFromClose)){
	// return true;
	// }
	//
	// return false;
	// }
	//
	/**
	 * Sorgt dafür das der Stream nicht mehr weiter liest und versucht ihn zu
	 * beenden
	 */
	public void stopListen() {
		listen = false;

		interrupt();
	}
	public boolean isStopped(){
		return listen;
	}

}
