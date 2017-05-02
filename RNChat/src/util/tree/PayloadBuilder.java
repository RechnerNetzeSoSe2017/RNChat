package util.tree;

public class PayloadBuilder {
	
	private String closeTAG = ">";
	HCPTAG tagLibary = new HCPTAG();
	
	public Payload getFromString(String string){
		
		String arbeitsString = string.toLowerCase().trim();
		int firsTAGIndex = arbeitsString.indexOf(closeTAG);
		String praefix = arbeitsString.substring(0, firsTAGIndex);
		
		
		
		return null;
	}
	private String getBetweenTAGs(String startTAG, String endTAG){
		return null;
	}
	
	public Payload newMessage(String nachricht){
		
		
		Payload pl = new Payload<String>("<message>", nachricht, "</message>");
		
		return pl;
	}
	/**
	 * erstellt eine neue Payload, die alle hirarchien control/channel/subscribe/id[..] erstellt..
	 * @param id
	 * @return
	 */
	public Payload newSubscribe(int id){
		//zuerst neue ID
		//dann neue subscribe
		//dann neue channel
		//dann neue control
		//dann fertig
		
		return null;
	}

}
