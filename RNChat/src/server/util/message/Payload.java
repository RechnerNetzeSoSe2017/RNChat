package server.util.message;

public abstract class Payload {
	
	private String payload="";
	private Payload ladung;
	
	public Payload(String message) {
		if(message!=null){
			payload=message;
		}
	}
	public Payload(Payload inhalt) {
	}	

	public String getType() {
		
		return getPayloadHead();
	}
	

	public String toString() {
		
		return getPayloadHead()+getPayloadMessage()+getPayloadTail();
		
	}
	/**
	 * Liefert die Payload mit oder ohne die message oder control TAGs.
	 * @param tag Ob die TAGs mitgeliefert werden sollen oder nicht
	 * @return 
	 */
	public String getContainer(boolean tag){
		if(tag){
			return toString();
		}else{
			return payload;
		}
	}
	
	/**
	 * liefert die Payload ohne message oder control TAGs
	 * @return
	 */
	public String getContaining(){
		return payload;
	}


	protected abstract String getPayloadTail();


	public abstract String getPayloadMessage();


	protected abstract String getPayloadHead();
	
	public abstract Payload getPayload(PayloadType type, boolean tag);
	
	public abstract Payload getPayload(PayloadType type);
	
	public abstract boolean isType(PayloadType type);
	

}
