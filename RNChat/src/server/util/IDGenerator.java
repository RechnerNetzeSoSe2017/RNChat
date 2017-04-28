package server.util;

import java.util.LinkedList;

/**
 * verwaltet ID's. Jede generierte ID ist einmalig. Die ID's beginnen bei 100, da 0 - 99 reserviert sind.
 * @author Höling
 *
 */
public class IDGenerator {
	
	//ids beginnen bei 100
	
	private static int startID=99;
	private static int id = startID;
	private static LinkedList<Integer> freieIDs = new LinkedList<>();
	
	public synchronized static int getID(){
		
		if(freieIDs.size()>0){
			return freieIDs.poll();
		}
		id++;
		return id;
		
	}
	
	public static synchronized void freeID(int id){
		freieIDs.add(id);
	}
	public static int getStartID(){
		return startID;
	}

}
