package server.protokol;

import java.io.PrintWriter;
import java.util.concurrent.LinkedBlockingQueue;

import server.util.message.Message;

public class OutputStreamThread extends Thread {

	private LinkedBlockingQueue<Message> output = new LinkedBlockingQueue<>();
	private PrintWriter out = null;
	private boolean shutdown = false;
	
	public OutputStreamThread(PrintWriter stream, LinkedBlockingQueue queue) {

		output=queue;
		out=stream;
		
	}
	
	
	@Override
	public void run() {
		//liest aus der liste aus und sendet dann alles
		
		Message msg=null;
		
		while(!shutdown){
			
			try {
				msg = output.take();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				//hier vielleicht die verbindnug beenden?
				e.printStackTrace();
			}
			
			out.println(msg);
			
			
		}
		
	}
	
	public void stopSend(){
		shutdown=true;
	}
}
