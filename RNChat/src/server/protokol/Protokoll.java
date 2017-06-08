package server.protokol;

import javafx.collections.ObservableList;
import javafx.scene.control.TextArea;

public interface Protokoll{
	
public void closeConnection();
public void setNickname(String name);
public void setRoomlist(ObservableList roomlist);
public void setTextareaForRoom(String raumname, TextArea area);
public void setListViewForNicklist(String raumname, ObservableList listview);
public void removeTextArea(String name);
public void subscribe(String name);
public void unsubscribe(String name);
public String getNick();
public void run();
}
