package server.util.message;

public class PayloadMessage extends Payload {

	private String payload="";
	
	public PayloadMessage(String message) {

		super(message);
		payload=message;
	}
	
	@Override
	protected String getPayloadTail() {
		
		return "</message>";
	}

	@Override
	protected String getPayloadMessage() {
		
		return payload;
	}

	@Override
	protected String getPayloadHead() {
		
		return "<message>";
	}

}
