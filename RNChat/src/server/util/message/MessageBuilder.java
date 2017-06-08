package server.util.message;

import java.util.Locale;

public class MessageBuilder<FromType, ToType> {

	private String closeTAG = ">";
	// HCPTAG tagLibary = new HCPTAG();
	private Locale locale = Locale.GERMAN;

	private String headerOKMessage = "<OK>";
	private String headerNOKMessage = "<NOK>";
	private String headerErrorMessage = "<ERROR>";
	private String fromTAG = "<from>";
	private String fromTAGClose = "</from>";
	private String toTAG = "<to>";
	private String toTAGClose = "</to>";
	public final String messageTAG = "<message>";
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
	private String nickaddTAG = "<nickadd>";
	private String nickaddTAGClose = "</nickadd>";
	private String nickleaveTAG = "<nickleave>";
	private String nickleaveTAGClose = "</nickleave>";
	private String nameserviceTAG = "<nameservice>";
	private String nameserviceTAGClose = "</nameservice>";
	private String nickTAG = "<nick>";
	private String nickTAGClose = "</nick>";
	private String channellistTAG = "<channellist>";
	private String channellistTAGClose = "</channellist>";
	private String logoutTAG = "<logout>";
	private String logoutTAGClose = "</logout>";
	private String okTAG = "<OK>";
	private String okTAGClose = "</OK>";
	private String nokTAG = "<NOK>";
	private String errorTAG = "<ERROR>";

	/**
	 * Parst anhand eines Strings ein Message-Objekt. Wenn die Syntax nicht
	 * korrekt ist (z.B. fehlt ein schliessender TAG), dann wird null zurück
	 * geliefert)
	 * 
	 * @param string
	 * @return null wenn parsen nicht erfolgreich, ein Payload-Objekt wenn
	 *         erfolgreich.
	 */
	public synchronized Message<String, String> getFromString(String string) {
		if (string != null) {

			String arbeitsString = string.toLowerCase(locale);

			String from = null;
			String to = null;

			if (arbeitsString.contains(fromTAG) && arbeitsString.contains(fromTAGClose)) {
				from = getInBetweenTAGs(fromTAG, fromTAGClose, string);
			
			}

			if (arbeitsString.contains(toTAG) && arbeitsString.contains(toTAGClose)) {
				to = getInBetweenTAGs(toTAG, toTAGClose, string);
			
			}
			
			
//			int start =arbeitsString.indexOf(toTAGClose)+toTAGClose.length();
			
			Payload payload = getPayloadFromString(string);

			if (payload != null && from != null && to != null) {
				return new Message<String, String>(from, to, payload);
			}

		}
		return null;
	}

	private Payload getPayloadFromString(String string) {
		if (string != null) {

			String arbeitsString = string.toLowerCase(locale).trim();

			if (arbeitsString.contains(messageTAG)) {
//				int begin = string.indexOf(">") + 1;
//				int ende = string.lastIndexOf("<");
System.out.println("messagebuilder, getpayloadfromString> ist message: "+arbeitsString);
				
					// String nachricht = string.substring(begin, ende);
					String nachricht = getInBetweenTAGs(messageTAG, messageTAGClose, string);

					if(nachricht != null){
					Payload pl = new Payload<String>(messageTAG, nachricht, messageTAGClose);
					

					return pl;
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

	public Message newMessage(String from, String to, String nachricht) {

		Payload<String> pl = new Payload<String>(messageTAG, nachricht, messageTAGClose);

		return new Message<String, String>(from, to, pl);
	}

	public Message toClientChannelAdd(String from, String to, String name) {

		if (name != null) {

			Payload<String> add = new Payload<>(addTAG, name, addTAGClose);
			Payload channel = new Payload<>(channelTAG, add, channelCloseTAG);
			Payload control = new Payload<>(controlTAG, channel, controlTAGClose);

			return new Message<String, String>(from, to, control);

		}
		return null;
	}

//	/**
//	 * erstellt eine neue Payload, die alle hirarchien
//	 * control/channel/subscribe/id[..] erstellt..
//	 * 
//	 * @param id
//	 * @return
//	 */
//	public Payload newSubscribe(int id) {
//		// zuerst neue ID
//		// dann neue subscribe
//		// dann neue channel
//		// dann neue control
//		// dann fertig
//
//		Payload idPL = new Payload<String>("<id>", "" + id, "</id>");
//		Payload subscribePL = new Payload<Payload>("<subscribe>", idPL, "</subscribe>");
//		Payload channelPL = new Payload<Payload>("<channel>", subscribePL, "</channel>");
//		Payload controlPL = new Payload<Payload>("<control>", channelPL, "</control>");
//
//		return controlPL;
//	}

	/**
	 * Baut ein Message-Objekt das die Nachricht enthält, ob die Subscribtion
	 * erfolgreich war oder nicht
	 * 
	 * @param from
	 * @param to
	 * @param type
	 *            "ok" für ok, "nok" für nicht ok
	 * @return
	 */
	public Message<FromType, ToType> tcSubscribeResponse(FromType from, ToType to, String type) {

		Payload okpl = new Payload<String>("", okTAG, "");

		if (type.equals("ok")) {
			okpl = new Payload<String>("", okTAG, "");
		} else if (type.equals("nok")) {
			okpl = new Payload<String>("", nokTAG, "");
		}

		Payload subpl = new Payload<>(subscribeTAG, okpl, subscribeTAGClose);
		Payload control = new Payload<>(controlTAG, subpl, controlTAGClose);
		Message<FromType, ToType> msg = new Message<FromType, ToType>(from, to, control);

		return msg;
	}

	/**
	 * Baut ein Message-Objekt das die Nachricht enthält, ob das Unsubscriben
	 * erfolgreich war oder nicht
	 * 
	 * @param from
	 * @param to
	 * @param type
	 *            "ok" für ok, "nok" für nicht ok
	 * @return
	 */
	public Message<FromType, ToType> tcUnsubscribeResponse(FromType from, ToType to, String type) {

		Payload okpl = new Payload<String>("", okTAG, "");

		if (type.equals("ok")) {
			okpl = new Payload<String>("", okTAG, "");
		} else if (type.equals("nok")) {
			okpl = new Payload<String>("", nokTAG, "");
		}

		Payload subpl = new Payload<>(unsubscribeTAG, okpl, unsubscribeTAGClose);
		Payload control = new Payload<>(controlTAG, subpl, controlTAGClose);
		Message<FromType, ToType> msg = new Message<FromType, ToType>(from, to, control);

		return msg;
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
	
	/**
	 * generiert eine nick-add Nachricht
	 * @param from
	 * @param to
	 * @param name
	 * @return
	 */
	public Message getNickadd(FromType from, ToType to,String name){
		
		
		Payload<String> nickadd = new Payload<>(nickaddTAG, name, nickaddTAGClose);
		Payload<Payload> control = new Payload<>(controlTAG, nickadd, controlTAGClose);
		
		Message msg = new Message<FromType, ToType>(from, to, control);
		
		return msg;
	}

	/**
	 * generiert eine nick-leave nachricht
	 * @param from
	 * @param to
	 * @param name
	 * @return
	 */
	public Message getNickLeave(FromType from, ToType to,String name){
		
		
		Payload<String> nickadd = new Payload<>(nickleaveTAG, name, nickleaveTAGClose);
		Payload<Payload> control = new Payload<>(controlTAG, nickadd, controlTAGClose);
		
		Message msg = new Message<FromType, ToType>(from, to, control);
		
		return msg;
	}
	/**
	 * Erstellt eine subscribe-Message
	 * @param from
	 * @param to
	 * @param name
	 * @return
	 */
	public Message getSubscribe(FromType from, ToType to,String name){
		
		Payload<String> subscribe = new Payload<>(subscribeTAG, name, subscribeTAGClose);
		Payload<Payload> control = new Payload<>(controlTAG, subscribe, controlTAGClose);
		
		Message msg = new Message<FromType, ToType>(from, to, control);
		
		return msg;
	}
	
	/**
	 * Erstellt eine unsubscribe-Message
	 * @param from
	 * @param to
	 * @param name
	 * @return
	 */
	public Message getUnsubscribe(FromType from, ToType to,String name){
		
		Payload<String> subscribe = new Payload<>(unsubscribeTAG, name, unsubscribeTAGClose);
		Payload<Payload> control = new Payload<>(controlTAG, subscribe, controlTAGClose);
		
		Message msg = new Message<FromType, ToType>(from, to, control);
		
		return msg;
	}
	public Message getchannellist(FromType from, ToType to){
		
		Payload channellist = new Payload<>(channellistTAG, "", channellistTAGClose);
		Payload control = new Payload<>(controlTAG, channellist, controlTAGClose);
		
		Message msg = new Message<FromType, ToType>(from, to, control);
		
		return msg;
		
		
	}
	public Message getLogout(String from, String to){
		
		Payload logout = new Payload<>(logoutTAG, "", logoutTAGClose);
		Payload control = new Payload<>(controlTAG, logout, controlTAGClose);
		
		return new Message<String, String>(from, to, control);
		
	}
	
	private Payload getControlBody(String restString) {
		// <control>[hier ist restString]</control>
		if (restString != null) {

			String workString = restString.toLowerCase(locale);

			// if (workString.contains(channelTAG)) {
			// // <channel>...</channel>
			// String temp = getInBetweenTAGs(channelTAG, channelCloseTAG,
			// restString);
			//
			// if (temp != null) {
			// Payload channel = getChannelBody(temp);
			//
			// if (channel != null) {
			// return new Payload<Payload>(channelTAG, channel,
			// channelCloseTAG);
			// } else {
			// return null;
			// }
			// }
			//
			// }
			if (workString.contains(channellistTAG)) {
				// <channellist></channellist>

				return new Payload<String>(channellistTAG, "", channellistTAGClose);

			} else if (workString.contains(subscribeTAG)) {
				// <subscribe>[ID]<subscribe>
				String temp = getInBetweenTAGs(subscribeTAG, subscribeTAGClose, restString);

				if (temp != null) {

					return new Payload<String>(subscribeTAG, temp, subscribeTAGClose);

				}

			} else if (workString.contains(unsubscribeTAG)) {
				// <unsubscribe>[...]</unsubscribe>

				String temp = getInBetweenTAGs(unsubscribeTAG, unsubscribeTAGClose, restString);

				if (temp != null) {

					return new Payload<String>(unsubscribeTAG, temp, unsubscribeTAGClose);

				}

			} else if (workString.contains(logoutTAG)) {
				return new Payload<String>(logoutTAG, "", logoutTAGClose);
			}

			else if (workString.contains(nickaddTAG)) {
				// <subscribe>[ID]<subscribe>
				String temp = getInBetweenTAGs(nickaddTAG, nickaddTAGClose, restString);

				if (temp != null) {

					return new Payload<String>(nickaddTAG, temp, nickaddTAGClose);

				}

			}
			
			 else if (workString.contains(nickleaveTAG)) {
					// <subscribe>[ID]<subscribe>
					String temp = getInBetweenTAGs(nickleaveTAG, nickleaveTAGClose, restString);

					if (temp != null) {

						return new Payload<String>(nickleaveTAG, temp, nickleaveTAGClose);

					}

				}
			 else if(workString.contains(channelTAG)){
				 //<channel>.....</channel>
				 //kann <add>..</add> beinhalten
				 
				 String temp = getInBetweenTAGs(channelTAG, channelCloseTAG, restString);
				 //temp ist nun <add>[]</add>
				 
				 if(temp != null && temp.contains(addTAG)){
					 
					 String name = getInBetweenTAGs(addTAG, addTAGClose, restString);
					 
					 return new Payload<Payload>(channelTAG,new Payload<Payload>(addTAG,new Payload<String>("",name,""),addTAGClose),channelCloseTAG);
					 
				 }
				 
			 }

		}
		return null;

	}

	private Payload getChannelIDBody(String restString) {
		if (restString != null) {
			// <nickadd><id></id><name></name></nickadd>
			// ODER
			// <nickleave><id></id><name></name></nickleave>
			String workString = restString.toLowerCase(locale);

			if (workString.contains(nickaddTAG)) {
				// <nickadd>...</nickadd>
				String temp = getInBetweenTAGs(nickaddTAG, nickaddTAGClose, restString);

				// ab hier ist temp = "<id>[id]</id><name>[name]</name>"

				if (temp != null) {
					String id = null;

					if (temp.contains(idTAG) && temp.contains(idTAGClose)) {
						id = getInBetweenTAGs(idTAG, idTAGClose, temp);
					}

					String name = null;
					if (temp.contains(nameTAG) && temp.contains(nameTAGClose)) {
						name = getInBetweenTAGs(nameTAG, nameTAGClose, temp);
					}

					if (id != null && name != null) {
						Payload idPL = new Payload<String>(idTAG, id, idTAGClose);
						Payload namePL = new Payload<String>(nameTAG, name, nameTAGClose);
						Payload nickaddPL = new Payload<Payload>(nickaddTAG, idPL, nickaddTAGClose);
						nickaddPL.addPayload(namePL);
						return nickaddPL;
					}

				}

			} else if (workString.contains(nickleaveTAG)) {
				// <nickleave><id>[]</id><name></name>

				String temp = getInBetweenTAGs(nickleaveTAG, nickleaveTAGClose, restString);
				// ab hier: "<id>..</id><name></name>

				if (temp != null) {
					String id = null;

					if (temp.contains(idTAG) && temp.contains(idTAGClose)) {
						id = getInBetweenTAGs(idTAG, idTAGClose, temp);
					}

					String name = null;
					if (temp.contains(nameTAG) && temp.contains(nameTAGClose)) {
						name = getInBetweenTAGs(nameTAG, nameTAGClose, temp);
					}

					if (id != null) {
						Payload idPL = new Payload<String>(idTAG, id, idTAGClose);
						Payload nickleavePL = new Payload<Payload>(nickleaveTAG, idPL, nickleaveTAGClose);

						if (name != null) {
							Payload namePL = new Payload<String>(nameTAG, name, nameTAGClose);
							nickleavePL.addPayload(namePL);
						}
						return nickleavePL;
					}
				}

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
