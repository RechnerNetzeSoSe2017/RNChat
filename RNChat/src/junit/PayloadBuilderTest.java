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
		
		Payload pl1 = new Payload<String>("<message>", "test", "</message>");
		Payload pb1 = payloadBuilder.getFromString("<message>test</message>");
		
		
		assertEquals(pl1.toString(),pb1.toString());
		
		
	}
	

}
