package server.util.message;

public class PayloadControl extends Payload {

private String payload="";
	
	public PayloadControl(String message) {

		super(message);
		payload=message;
	}
	
	@Override
	protected String getPayloadTail() {
		
		return "</control>";
	}

	@Override
	protected String getPayloadMessage() {
		
		return payload;
	}

	@Override
	protected String getPayloadHead() {
		
		return "<control>";
	}


}
