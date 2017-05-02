package junit;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

import server.util.message.Payload;
import server.util.message.PayloadBuilder;

public class PayloadBuilderTest {
	
	private PayloadBuilder payloadBuilder = new PayloadBuilder();
	
	@Test
	public void testFromString(){
		
		//aus nem leeren string gibs nix zu parsen
		assertNull(payloadBuilder.getFromString(""));
		
		Payload pl1 = new Payload<String>("<message>", "test", "</message>");
		Payload pb1 = payloadBuilder.getFromString("<message>test</message>");		
		
		assertEquals(pl1.toString(),pb1.toString());
		
		
		//es darf keine leere controlmessage geben
//		Payload plChannel = new Payload<Payload>("<control>",new Payload<String>("<channel>", "", "</channel>"),"</control");
		Payload pbChannel = payloadBuilder.getFromString("<control><channel></channel></control>");
		assertNull(pbChannel);
		
		//subscribe muss eine ID enthalten
		Payload pbSubscribe = payloadBuilder.getFromString("<control><channel><subscribe></subscribe></channel></control>");
		assertNull(pbSubscribe);
		
		//ob der string zu den entsprechenden Payloads gebaut wird und durch toString() genau so wieder gebaut wird
		pbSubscribe = payloadBuilder.getFromString("<control> <channel> <subscribe>13</subscribe> </channel> </control>");
		assertTrue(pbSubscribe.toString().equals("<control><channel><subscribe>13</subscribe></channel></control>"));
		
		
		//unsubscribe muss eine ID enthalten
		Payload pbUnsubscribe = payloadBuilder.getFromString("<control><channel><unsubscribe></unsubscribe></channel></control>");
		assertNull(pbUnsubscribe);
		
		
	}
	
	

}
