package server.protokol;

import java.io.BufferedReader;
import java.util.concurrent.LinkedBlockingQueue;

import server.util.message.Message;

public class InputStreamThread extends Thread {
	
	private BufferedReader in = null;
	private LinkedBlockingQueue<Message> input;
	
	public InputStreamThread(BufferedReader inStream, LinkedBlockingQueue queue) {

		in=inStream;
		input=queue;
		
	}
	
	
	@Override
	public void run() {
		//liest, checkt ob im <message>-format und legt die message in den puffer..
	}

}
