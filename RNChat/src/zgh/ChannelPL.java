package zgh;

import zgh.PayloadControl;

public class ChannelPL extends PayloadControl {
	private Payload payload;
	private String nachricht="";
	private static PayloadType payloadTyp = PayloadType.control;
	private static final String messageHead = "<control>";
	private static final String messageTail = "</control>";
	
	public ChannelPL(Payload inhalt, String alsString) {
		super(inhalt,alsString);
		typListe.add(payloadTyp);
		
		payload=inhalt;
		nachricht=alsString;
		
	}
	
	public ChannelPL(String string) {
	super();
		nachricht=string;
		typListe.add(payloadTyp);
	}
	
	
	@Override
	public String getPayloadTail() {

		
		return messageTail;
	}

	@Override
	public String getPayloadMessage() {
		
		return nachricht;
	}

	@Override
	public
	String getPayloadHead() {
		
		return messageHead;
	}

	@Override
	public String getPayload(PayloadType type, boolean tag) {
		if(payloadTyp.toString().toLowerCase().equals(type.toString().toLowerCase())){
			if(tag){
				return messageHead+nachricht+messageTail;
			}else{
				return nachricht;
			}
		}
		return null;
	}

	@Override
	public Payload getPayload(PayloadType type) {
		if(payloadTyp.toString().toLowerCase().equals(type.toString().toLowerCase())){
			
				return payload;
			
		}
		return null;
	}

	@Override
	public boolean isType(PayloadType type) {
		
		return payloadTyp.toString().toLowerCase().equals(type.toString().toLowerCase());
	}
}
