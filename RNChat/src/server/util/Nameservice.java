package server.util;

import java.util.HashSet;

public class Nameservice {
	
	private static Nameservice instance = null;
	private String nsName;
	
	private HashSet<String> nameSet = new HashSet<String>();
	
	
	
	public Nameservice(String name) {
		// TODO Auto-generated constructor stub
		nsName=name;
		instance=this;
		
		
		nameSet.add("Admin");
		nameSet.add("admin");
		nameSet.add("Server");
		nameSet.add("server");
		
		
		
	}
	
	public static Nameservice getInstance(String name){
		if(instance==null){
			instance=new Nameservice(name);
		}
		return instance;
	}
	
	/**
	 * fügt einen namen zu dem Verzeichnis hinzu, falls der Name noch nicht vergeben ist
	 * @param id
	 * @return
	 */
	public synchronized boolean addName(String nick){
		
		if(nick!=null && !nameSet.contains(nick)){
			nameSet.add(nick);
			return true;
		}
		
		
		return false;
	}
	/**
	 * entfernt den Namen aus dem Verzeichnis
	 * @param name
	 */
	public synchronized void removeName(String name){
		if(name!=null){
		nameSet.remove(name);
		}
	}

}
