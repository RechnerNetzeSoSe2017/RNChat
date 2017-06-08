package server.protokol;

import java.io.PrintWriter;
import java.util.concurrent.LinkedBlockingQueue;

import server.util.message.Message;

public class OutputStreamThread<E> extends Thread {

	private LinkedBlockingQueue<E> output = new LinkedBlockingQueue<>();
	private PrintWriter out = null;
	private boolean shutdown = false;
	
	public OutputStreamThread(PrintWriter stream, LinkedBlockingQueue queue) {

		output=queue;
		out=stream;
		
	}
	
	
	@Override
	public void run() {
		//liest aus der liste aus und sendet dann alles
		
		E msg=null;
		
		while(!shutdown){
			
			try {
				msg = output.take();
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				//hier vielleicht die verbindnug beenden?
//				e.printStackTrace();
//				shutdown=true;
				msg=null;
				continue;
			}
			
			if(!shutdown && msg != null){
				out.println(msg);
				msg=null;
			}
			
		}
		
	}
	/**
	 * Sorgt dafür das dieser Thread aufhört zu senden und er ordnungsgemäß "herunter fährt"
	 */
	public void stopSend(){
		shutdown=true;
//		output.notifyAll();
		interrupt();
	}
	

}
