package server.util.message;

import java.util.ArrayList;
import java.util.List;

public class Payload <Payloadtype>{
	
	private String praefix;
	private List<Payloadtype> payloadList = new ArrayList<>();
	private String suffix;
	private String value="";
	
	//konstruktor der eine ganze hirachchie parst..
//	public Payload(String praefix, String payload, String suffix) {
//		
//	}
	public Payload(String prae, Payloadtype pay, String suff) {
		praefix=prae;
		suffix=suff;
		
		payloadList.add(pay);
		
	}
	public Payload(String prae, List<Payloadtype> pay, String suff) {
		praefix=prae;
		suffix=suff;
		
		payloadList.addAll(pay);
		
	}
	
	
	public String toString(){
		String temp=praefix;
		
		for(Payloadtype elem : payloadList){
			temp+=elem.toString();
		}
		temp+=suffix;
		
		return temp;
	}
	public void addPayload(Payloadtype pl){
		payloadList.add(pl);
	}
	public String getPrefix(){
		return praefix;
	}
//	public List<Payloadtype> getPayloadList(){
//		if(payloadList.isEmpty()){
//			return null;
//		}
//		
//		return payloadList;
//	}
//	public String toString(){
//		String temp=praefix;
//		for(Payloadtype elem : payloadList){
//			temp+=elem.toString();
//		}
//		return temp+suffix;
//	}

}
