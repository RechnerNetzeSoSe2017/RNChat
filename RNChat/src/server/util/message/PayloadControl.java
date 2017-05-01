package server.util.message;

public class PayloadControl extends Payload {

private String payload="";
private Payload ladung;
private static String openTAG = "<control>";
private static String closeTAG = "</control>";
private static PayloadType payloadType = PayloadType.control;
	
	public PayloadControl(String message) {

		super(openTAG+message+closeTAG);
		payload=message;
	}
	
	@Override
	protected String getPayloadTail() {
		
		return closeTAG;
	}

	@Override
	public String getPayloadMessage() {
		
		return payload;
	}

	@Override
	protected String getPayloadHead() {
		
		return openTAG;
	}

	@Override
	public Payload getPayload(PayloadType type, boolean tag) {
		if(type.toString().toLowerCase().equals(payloadType.toString().toLowerCase())){
			
				return payload;
			
		}
		return null;
	}

	@Override
	public Payload getPayload(PayloadType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isType(PayloadType type) {
		// TODO Auto-generated method stub
		return false;
	}


}
