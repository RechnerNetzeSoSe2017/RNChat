package zgh;

import java.util.LinkedList;

public abstract class Payload {
	
	LinkedList<PayloadType> typListe = new LinkedList<>();
	
	public Payload(Payload inhalt, String alsString) {
	}	
	public Payload() {
		// TODO Auto-generated constructor stub
	}

	public String getType() {
		
		return getPayloadHead();
	}
	

	public String toString() {
		
		return getPayloadHead()+getPayloadMessage()+getPayloadTail();
		
	}

	abstract String getPayloadTail();


	abstract String getPayloadMessage();


	abstract String getPayloadHead();
	
	public abstract String getPayload(PayloadType type, boolean tag);
	
	public abstract Payload getPayload(PayloadType type);
	
	public abstract boolean isType(PayloadType type);
	
	public boolean lastTypeIs(PayloadType typ){
		PayloadType last = typListe.getLast();
		
		return typ.toString().toLowerCase().equals(last.toString().toLowerCase());
		
	}
	public PayloadType getLastType(){
		return typListe.getLast();
	}

	
	
	

}
