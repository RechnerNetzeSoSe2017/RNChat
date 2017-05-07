package server.util.message;

public class Message<From, To> {

	private From from;
	private To recipient;
	private Payload payload;
	private long timestamp;

	public Message(From from, To to, Payload payload) {
		this.from = from;
		recipient = to;
		this.payload = payload;
		timestamp=System.currentTimeMillis();
	}
	
	public From getFrom(){
		return from;
	}
	
	public To getTo(){
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
	public void setFrom(From id){
		from=id;
	}
	public void setTo(To id){
		recipient=id;
	}

}
