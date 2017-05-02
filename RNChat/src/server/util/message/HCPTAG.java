package server.util.message;

import java.util.HashMap;
import java.util.HashSet;

public class HCPTAG {

	private static HashMap<String, String> openZUcloseTAG = new HashMap<>();

	static {
		openZUcloseTAG.put("<message>", "</message>");
		
		openZUcloseTAG.put("<nameservice>", "</nameservice>");
		openZUcloseTAG.put("<changenick>", "</changenick>");
		openZUcloseTAG.put("<nickid>", "</nickid>");
		
		openZUcloseTAG.put("<control>", "</control>");
		
	}

	/**
	 * 
	 * @param tag
	 * @return closeTAG zu tag, null wenn es keinen SchlieﬂTAG gibt (also auch wenn es den OpenTAG nicht gibt)
	 */
	public static String getCloseTAG(String tag) {

		return openZUcloseTAG.get(tag);
		
	}

}
