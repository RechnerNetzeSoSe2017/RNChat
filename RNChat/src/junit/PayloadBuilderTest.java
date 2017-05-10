package junit;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

import server.util.message.Payload;
import server.util.message.Message;
import server.util.message.MessageBuilder;

public class PayloadBuilderTest {
	
	private MessageBuilder payloadBuilder = new MessageBuilder();
	
	
	private String channellistCommand="<from>g</from><to>g</to><control><channellist></channellist></control>";
	private String subscribeCommand="<from>g</from><to>g</to><control><subscribe>hallo Test</subscribe></control>";
	private String unsubscribeCommand="<from>g</from><to>g</to><control><subscribe>hallo Test</subscribe></control>";
	private String logoutCommand="<from>g</from><to>g</to><control><logout></logout></control>";
	
	private String testnachricht="<from>g</from><to>g</to><message>Testnachricht</message>";
	
	
	@Test
	public void testFromString(){
		
		//aus nem leeren string gibs nix zu parsen
		assertNull(payloadBuilder.getFromString(""));
		
		Payload pl1 = new Payload<String>("<message>", "test", "</message>");
		
		Message<String,String> mes1 = payloadBuilder.getFromString("<from>g</from><to>gg</to><message>test</message>");
		assertNotNull(mes1);
		
		Payload pb1 = payloadBuilder.getFromString("<from>g</from><to>gg</to><message>test</message>").getPayload();		

		assertEquals(pl1.toString(),pb1.toString());
		
		
		//es darf keine leere controlmessage geben
//		Payload plChannel = new Payload<Payload>("<control>",new Payload<String>("<channel>", "", "</channel>"),"</control");
		Message pbChannel = payloadBuilder.getFromString("<from>g</from><to>g</to><control><channel></channel></control>");
		assertNull(pbChannel);
		
		//subscribe muss eine ID enthalten
		Message pbSubscribe = payloadBuilder.getFromString("<from>g</from><to>g</to><control><channel><subscribe></subscribe></channel></control>");
		assertNull(pbSubscribe);
		
		//ob der string zu den entsprechenden Payloads gebaut wird und durch toString() genau so wieder gebaut wird
		pbSubscribe = payloadBuilder.getFromString("<from>g</from><to>g</to><control>  <subscribe>13</subscribe>  </control>");
		
		assertTrue(pbSubscribe.toString().equals("<from>g</from><to>g</to><control><subscribe>13</subscribe></control>"));
		
		
		Message msg2 = payloadBuilder.getFromString(channellistCommand);
		assertNotNull(msg2);
		System.out.println(msg2);
		assertTrue(channellistCommand.equals(msg2.toString()));
		
		Message msg3 = payloadBuilder.getFromString(subscribeCommand);
		assertNotNull(msg3);
		System.out.println(msg3);
		assertTrue(subscribeCommand.equals(msg3.toString()));
		
		Message msg4 = payloadBuilder.getFromString(unsubscribeCommand);
		assertNotNull(msg4);
		System.out.println(msg4);
		assertTrue(unsubscribeCommand.equals(msg4.toString()));
		
		Message msg5 = payloadBuilder.getFromString(logoutCommand);
		assertNotNull(msg5);
		System.out.println(msg5);
		assertTrue(logoutCommand.equals(msg5.toString()));
//		
//		//unsubscribe muss eine ID enthalten
//		Payload pbUnsubscribe = payloadBuilder.getFromString("<control><channel><unsubscribe></unsubscribe></channel></control>").getPayload();
//		assertNull(pbUnsubscribe);
//		
		
	}
	
	

}
