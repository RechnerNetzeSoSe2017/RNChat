package util.tree;
//import server.util.message.Payload;
import server.util.message.*;

public class Rumspielen {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
//		Payload pl1 = new Payload<>("<message>", "testnachricht", "</message>");
//		
//		System.out.println(pl1);
//		
//		Payload id = new Payload<String>("<id>", "ich", "</id>");
//		Payload subscribe = new Payload<Payload>("<subscribe>", id, "</subscribe>");
//		Payload channel = new Payload<Payload>("<channel>", subscribe, "</channel>");
//		Payload control = new Payload<Payload>("<control>", channel, "</control>");
//		
//		System.out.println(control);
//		
//		
//		System.out.println("channelMSG:"+control.getPayloadList().get(0));
	
//		
//		server.util.message.PayloadBuilder pb = new PayloadBuilder();
//		
//		server.util.message.Payload pl = pb.getFromString("<mEssage>\t   blahBlah</messagE>");
//		
//		System.out.println(pl);
//		
		
		
		String tag = Rumspielen.getInBetweenTAGs("<control>", "</control>", "<CoNtRoL>bla h h h blahhh</control>");
		System.out.println(tag);

	}
private static String getInBetweenTAGs(String tagBegin,String tagEnd, String getFrom){
		
		String temp = getFrom.toLowerCase();
		
		int startidex = temp.indexOf(tagBegin) + tagBegin.length();
		int startindexClose = temp.indexOf(tagEnd);

		if (startidex < startindexClose) {
			return getFrom.substring(startidex, startindexClose);
		}
		return null;
	}

}
