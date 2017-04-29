package server.util.message;

public class Message {

	private int from;
	private int recipient;
	private Payload payload;
	private long timestamp;

	public Message(int from, int to, Payload payload) {
		this.from = from;
		recipient = to;
		this.payload = payload;
		timestamp=System.currentTimeMillis();
	}
	
	public int getFromID(){
		return from;
	}
	
	public int getToId(){
		return recipient;
	}
	public Payload getPayload(){
		return payload;
	}
	public void timestamp(long time){
		timestamp=time;
	}
	public long getTime(){
		return timestamp;
	}
	
	public String toString(){
	return "<from>"+from+"</from><to>"+recipient+"</to>"+payload.toString();	
	}
	public void setFromID(int id){
		from=id;
	}

}
