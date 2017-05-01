package util.tree;

import java.util.ArrayList;
import java.util.List;

public class Payload <Payloadtype>{
	
	private String praefix;
	private List<Payloadtype> payloadList = new ArrayList<>();
	private String suffix;
	
	//konstruktor der eine ganze hirachchie parst..
	public Payload(String praefix, String payload, String suffix) {
		
	}
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
		
	}
	public List<Payloadtype> getPayloadList(){
		return payloadList;
	}

}
