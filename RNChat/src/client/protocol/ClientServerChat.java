package client.protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.Socket;

import javafx.collections.ObservableList;
import javafx.scene.control.TextArea;
import server.protokol.Protokoll;

public class ClientServerChat implements Protokoll {

	private String hostName ="";
	private int portNr=3333;
	private Socket socket=null;
	private BufferedReader in;
	private PrintWriter out;
	private String userName="";
	private ObservableList roomlist=null;
	
	
	public ClientServerChat(String host, int port) {
		hostName=host;
		portNr=port;
		
		try {
			socket = new Socket(Inet4Address.getByName(host), portNr);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(socket != null && socket.isConnected()){
			try {
				in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	@Override
	public void closeConnection() {
		// TODO Auto-generated method stub
		if(socket != null){
			try {
				in.close();
				out.close();
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

	}

	@Override
	public void setNickname(String name) {
		userName=name;

	}

	@Override
	public void setRoomlist(ObservableList roomlist) {
		this.roomlist=roomlist;

	}

	@Override
	public void setTextareaForRoom(String raumname, TextArea area) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setListViewForNicklist(String raumname, ObservableList listview) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeTextArea(String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public void subscribe(String name) {
		// wechselt in den raum der mit <name> angegeben ist

	}

	@Override
	public void unsubscribe(String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getNick() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

}
