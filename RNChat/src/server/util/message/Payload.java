package server.util.message;

public abstract class Payload {
	
	private String payload="";
	
	public Payload(String message) {
		if(message!=null){
			payload=message;
		}
	}
	

	public String getType() {
		
		return getPayloadHead();
	}
	

	public String toString() {
		
		return getPayloadHead()+getPayloadMessage()+getPayloadTail();
		
	}


	protected abstract String getPayloadTail();


	protected abstract String getPayloadMessage();


	protected abstract String getPayloadHead();
	

}
