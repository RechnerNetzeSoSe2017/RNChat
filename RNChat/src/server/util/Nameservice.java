package server.util;

public class Nameservice {
	
	private Nameservice instance = null;
	private String nsName;
	
	
	
	public Nameservice(String name) {
		// TODO Auto-generated constructor stub
		nsName=name;
		instance=this;
		
	}
	
	public Nameservice getInstance(String name){
		return instance;
	}
	
	/**
	 * fügt einen namen zu einer collection zu einer ID hinzu, falls der Name noch nicht vergeben ist
	 * @param id
	 * @return
	 */
	public synchronized boolean addName(int id){
		return true;
	}

}
