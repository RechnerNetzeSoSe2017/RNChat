package server.util.message;

import java.util.Locale;

public class PayloadBuilder {

	private String closeTAG = ">";
	// HCPTAG tagLibary = new HCPTAG();
	private Locale locale = Locale.GERMAN;

	private String headerOKMessage = "<OK>";
	private String headerNOKMessage = "<NOK>";
	private String headerErrorMessage = "<ERROR>";
	private String messageTAG = "<message>";
	private String messageTAGClose = "</message>";
	private String controlTAG = "<control>";
	private String controlTAGClose = "</control>";
	private String channelTAG = "<channel>";
	private String channelCloseTAG = "</channel>";
	private String subscribeTAG = "<subscribe>";
	private String subscribeTAGClose = "</subscribe>";
	private String unsubscribeTAG = "<unsubscribe>";
	private String unsubscribeTAGClose = "</unsubscribe>";
	private String listTAG = "<list>";
	private String listTAGClose = "</list>";
	private String addTAG = "<add>";
	private String addTAGClose = "</add>";
	private String idTAG = "<id>";
	private String idTAGClose = "</id>";
	private String nameTAG = "<name>";
	private String nameTAGClose = "</name>";
	private String channelidTAG = "<channelid>";
	private String channelidTAGClose = "</channelid>";
	private String nameserviceTAG = "<nameservice>";
	private String nameserviceTAGClose = "</nameservice>";
	private String nickTAG = "<nick>";
	private String nickTAGClose = "</nick>";

	/**
	 * Parst anhand eines Strings ein Message-Objekt. Wenn die Syntax nicht
	 * korrekt ist (z.B. fehlt ein schliessender TAG), dann wird null zurück
	 * geliefert)
	 * 
	 * @param string
	 * @return null wenn parsen nicht erfolgreich, ein Payload-Objekt wenn
	 *         erfolgreich.
	 */
	public synchronized Payload getFromString(String string) {
		if (string != null) {

			String arbeitsString = string.toLowerCase(locale).trim();

			if (arbeitsString.contains(messageTAG)) {
				int begin = string.indexOf(">") + 1;
				int ende = string.lastIndexOf("<");

				if (begin < ende) {
					// String nachricht = string.substring(begin, ende);
					String nachricht = getInBetweenTAGs(messageTAG, messageTAGClose, string);

					Payload pl = new Payload<String>(messageTAG, nachricht, messageTAGClose);

					return pl;

				} else {
					return null;
				}

			} else if (arbeitsString.contains(controlTAG)) {
				// ab hier ist klar das es sich um ein control tag handelt..
				String ohneControl = getInBetweenTAGs(controlTAG, controlTAGClose, string);

				if (ohneControl != null) {
					Payload control = getControlBody(ohneControl);

					if (control != null) {
						return new Payload<Payload>(controlTAG, control, controlTAGClose);
					}
				}

			}
		}
		return null;
	}

	public Payload newMessage(String nachricht) {

		Payload pl = new Payload<String>(messageTAG, nachricht, messageTAGClose);

		return pl;
	}

	/**
	 * erstellt eine neue Payload, die alle hirarchien
	 * control/channel/subscribe/id[..] erstellt..
	 * 
	 * @param id
	 * @return
	 */
	public Payload newSubscribe(int id) {
		// zuerst neue ID
		// dann neue subscribe
		// dann neue channel
		// dann neue control
		// dann fertig

		Payload idPL = new Payload<String>("<id>", "" + id, "</id>");
		Payload subscribePL = new Payload<Payload>("<subscribe>", idPL, "</subscribe>");
		Payload channelPL = new Payload<Payload>("<channel>", subscribePL, "</channel>");
		Payload controlPL = new Payload<Payload>("<control>", channelPL, "</control>");

		return controlPL;
	}

	private String getInBetweenTAGs(String tagBegin, String tagEnd, String getFrom) {
		if (getFrom != null) {
			String temp = getFrom.toLowerCase(locale);

			int startidex = temp.indexOf(tagBegin) + tagBegin.length();
			int startindexClose = temp.indexOf(tagEnd);

			if (startidex < startindexClose) {
				return getFrom.substring(startidex, startindexClose);
			}
		}
		return null;
	}

	private Payload getControlBody(String restString) {
		if (restString != null) {

			String workString = restString.toLowerCase(locale);

			if (workString.contains(channelTAG)) {
				String temp = getInBetweenTAGs(channelTAG, channelCloseTAG, restString);

				if (temp != null) {
					Payload channel = getChannelBody(temp);

					if (channel != null) {
						return new Payload<Payload>(channelTAG, channel, channelCloseTAG);
					} else {
						return null;
					}
				}

			}else if(workString.contains(channelidTAG)){
//-------------------------				
			}
		}
		return null;
	}

	private Payload getChannelBody(String restString) {
		if (restString != null) {
			String workString = restString.toLowerCase(locale);

			if (workString.contains(subscribeTAG)) {
				// <subscribe>[ID]</subscribe>
				String temp = getInBetweenTAGs(subscribeTAG, subscribeTAGClose, restString);

				Integer id;
				try {
					id = Integer.parseInt(temp);
				} catch (NumberFormatException e) {
					id = null;
				}

				if (id != null) {
					return new Payload<Integer>(subscribeTAG, id, subscribeTAGClose);
				}
			} else if (workString.contains(unsubscribeTAG)) {
				// <unsubscribe>[ID]</unsubscribe>

				String temp = getInBetweenTAGs(unsubscribeTAG, unsubscribeTAGClose, restString);

				if (temp != null) {
					return new Payload<String>(unsubscribeTAG, temp, unsubscribeTAGClose);
				}
			} else if (workString.contains(listTAG)) {
				// <list></list>
				return new Payload<String>(listTAG, "", listTAGClose);
			} else if (workString.contains(addTAG)) {
				// <add><id>[ID]</id><name>[NAme]</name></add>

				String temp = getInBetweenTAGs(addTAG, addTAGClose, restString);
				String tempWorkString = workString.toLowerCase(locale);
				// <id>[ID]</id><name>[NAme]</name>

				String idString = null;
				if (tempWorkString.contains(idTAG) && tempWorkString.contains(idTAGClose)) {
					idString = getInBetweenTAGs(idTAG, idTAGClose, temp);
				}
				String nameString = null;
				if (tempWorkString.contains(nameTAG) && tempWorkString.contains(nameTAGClose)) {
					nameString = getInBetweenTAGs(nameTAG, nameTAGClose, temp);
				}

				// wenn es eine ID UND einen Namen gibt, DANN kann die nachricht
				// gebaut werden.
				if (idString != null && nameString != null) {
					Payload idPL = new Payload<String>(idTAG, idString, idTAGClose);
					Payload namePL = new Payload<String>(nameTAG, nameString, nameTAGClose);

					Payload addPL = new Payload<Payload>(addTAG, idPL, addTAGClose);
					addPL.addPayload(namePL);

					return addPL;
				}

				// wenn add fehlerhaft ist, wird null returnt..
				return null;

			}
		}

		return null;
	}

}
