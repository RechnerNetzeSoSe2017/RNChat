package util.tree;

public class Rumspielen {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Payload pl1 = new Payload<>("<message>", "testnachricht", "</message>");
		
		System.out.println(pl1);
		
		Payload id = new Payload<String>("<id>", "ich", "</id>");
		Payload subscribe = new Payload<Payload>("<subscribe>", id, "</subscribe>");
		Payload channel = new Payload<Payload>("<channel>", subscribe, "</channel>");
		Payload control = new Payload<Payload>("<control>", channel, "</control>");
		
		System.out.println(control);
		
		
		System.out.println("channelMSG:"+control.getPayloadList().get(0));
		
		

	}

}
