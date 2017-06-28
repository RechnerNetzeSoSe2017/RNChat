package client.protocolother;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import client.gui.UIController;
import client.protocol.HCPClient;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class MainGuiController implements Initializable{
	@FXML
	TabPane tabpane = new TabPane();
	
	@FXML
	TextArea messageArea = new TextArea();
	
	@FXML
	TextArea inputArea = new TextArea();
	
	@FXML
	Button sendButton = new Button("send");
	
	@FXML
	MenuBar menubar = new MenuBar();
	
	//----------------------main tab-----------------
	@FXML
	TextField serverIPTF = new TextField();
	
	@FXML
	TextField nicknameTF = new TextField();
	
	@FXML
	Button connectB = new Button();
	
	@FXML
	Button disconnectB = new Button();
	
	//---------
	
	@FXML
	ListView<String> raumlisteLV = new ListView<String>();
	
	@FXML
	Button joinB = new Button();
	
	@FXML
	Button leaveB = new Button();
	
	//---------------------------------------------------
	//--------------fenster raum1------------------------
	@FXML
	TextArea raum1TA = new TextArea();
	@FXML
	ListView raum1LV = new ListView<String>();
	@FXML
	TextField raum1TF = new TextField();
	@FXML
	Button raum1Senden = new Button();
	
	//--------------fenster raum2------------------------
		@FXML
		TextArea raum2TA = new TextArea();
		@FXML
		ListView raum2LV = new ListView<String>();
		@FXML
		TextField raum2TF = new TextField();
		@FXML
		Button raum2Senden = new Button();
	
	private boolean raum1Besetzt=false;
	private String raum1Name="";
	private boolean raum2Besetzt=false;
	private String raum2Name="";
	
	
	
	
	private HCPClient hcpClient;
	
	private static UIController instance=null;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		
		
//		instance=this;
		
//		hcpClient=new HCPClient("127.0.0.1", 33333);
//		
//		hcpClient.start();
//		
		
		sendButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				String temp = inputArea.getText();
				
//				int beginIndex = temp.indexOf("<>");
				String[] blah = temp.split("<>");
				
				hcpClient.sendMessage(blah[1], blah[0]);
				messageArea.appendText(temp);
				
				inputArea.setText("");
				
			}
		});
		
		
		
		Menu menuConnect = new Menu("Datei");
		MenuItem connToServer = new MenuItem("beenden");
		
		menuConnect.getItems().add(connToServer);
		
		menubar.getMenus().add(menuConnect);
		
		//eventlistener für den menüpunkt "connect To Server.."
		connToServer.setOnAction(actionEvent -> {beenden();});
		
		disconnectB.setDisable(true);
		
		connectB.setOnAction(actionEvent ->{connectToServer();});
		disconnectB.setOnAction(actionEvent ->{disconnect();});
		joinB.setOnAction(actionEvent -> {abboniereChannel();});
		leaveB.setOnAction(actionEvent ->{deabboniereChannel();});
		
		raum1Senden.setOnAction(actionEvent ->{raum1Senden();});
		
		raum2Senden.setOnAction(actionEvent ->{raum2Senden();});
		
//		messageArea.add
		
		
		
	}
	private void beenden() {
		disconnect();
		System.exit(0);
		
	}
	private void raum1Senden(){
		if(raum1Besetzt){
			String nachricht = raum1TF.getText();
			
			raum1TF.setText("");
			
			hcpClient.sendMessage(nachricht, raum1Name);
			
			
		}
	}
	private void raum2Senden(){
		if(raum2Besetzt){
			String nachricht = raum2TF.getText();
			
			
			raum2TF.setText("");
			
			hcpClient.sendMessage(nachricht, raum2Name);
			
			}
	}
	
	private void deabboniereChannel() {
		String name = raumlisteLV.getSelectionModel().getSelectedItem();
		
		if(name!=null){
			
			if(name.equals(raum1Name)){
				
				raum1Besetzt=false;
				
			}else if(name.equals(raum2Name)){
				raum2Besetzt=false;
				
			}
			hcpClient.unsubscribe(name);
			
			if(!raum1Besetzt || !raum2Besetzt){
				joinB.setDisable(false);
			}
			
		}
		
	}
	private void abboniereChannel() {
		
		
		String name = raumlisteLV.getSelectionModel().getSelectedItem();
		if(name!=null){
			hcpClient.subscribe(name);
			
			if(!raum1Besetzt){
				raum1TA.clear();
				hcpClient.setTextareaForRoom(name, raum1TA);
				raum1LV.getItems().clear();
//				raum1LV.getItems().add(hcpClient.getNick());
				hcpClient.setListViewForNicklist(name, raum1LV.getItems());
				raum1Name=name;
				raum1Besetzt=true;
			}else if(!raum2Besetzt){
				raum2TA.clear();
				hcpClient.setTextareaForRoom(name, raum2TA);
				raum2LV.getItems().clear();
//				raum2LV.getItems().add(hcpClient.getNick());
				hcpClient.setListViewForNicklist(name, raum2LV.getItems());
				raum2Name=name;
				raum2Besetzt=true;
			}
			
			if(raum1Besetzt && raum2Besetzt){
				joinB.setDisable(true);
			}
			
		}
		
		
	}
	
	/**
	 * Gibt diese Instanz des Controllers zurück --> Singelton-Pattern
	 * @return
	 */
	public static UIController getInstance(){
		return instance;
	}
	/**
	 * fügt einfach nur den text zur konsole hinzu
	 * @param message
	 * @param id
	 */
	public void message(String message, int id){
		
		messageArea.appendText(message+"\n");
		
	}
	
	/**
	 * Bekommt eine Nachricht und fügt sie an das richtige tabfenser an.
	 * @param tabname
	 * @param nachricht
	 */
	public void messageToChat(String tabname, String absender, String nachricht){
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		String uhrzeit = sdf.format(new Date());
		
		
		if(tabname.equals(raum1Name)){
			raum1TA.appendText(uhrzeit+" - "+absender+">"+nachricht+"\n");
			
		}
		else if(tabname.equals(raum2Name)){
			raum2TA.appendText(uhrzeit+" - "+absender+">"+nachricht+"\n");
		}
		
		
	}
	
	/**
	 * Fügt einem Namen der Channelliste hinzu
	 * @param channel
	 */
	public void addToChannellist(String channel){
		if(channel!=null){
			raumlisteLV.getItems().add(channel);
		}
	}
	
	private void connectToServer(){
		String ip = serverIPTF.getText();
		String nickname = nicknameTF.getText();
		
		if(!ip.equals("") && !nickname.equals("")){
			hcpClient=new HCPClient(ip, 33333);
			hcpClient.setNickname(nickname);
			raumlisteLV.getItems().clear();
			hcpClient.setRoomlist(raumlisteLV.getItems());
			
			hcpClient.start();
			disconnectB.setDisable(false);
			connectB.setDisable(true);
		}
		
	}
	/**
	 * Fügt der nick-übersicht einen namen hinzu
	 * @param raumname
	 * @param nickname
	 */
	public void addNickToNicklist(String raumname, String nickname){
		
		if(raumname != null && nickname != null){
			
			if(raumname.equals(raum1Name)){
				
				raum1LV.getItems().add(nickname);
				
				
			}else if(raumname.equals(raum2Name)){
				
				raum2LV.getItems().add(nickname);
			}
			
		}
		
		
	}
	/**
	 * entfernt einen Nickname aus der Nickübersicht eines Raumes
	 * @param raumname
	 * @param nickname
	 */
	public void removeNickfromNicklist(String raumname, String nickname){
		
		if(raumname != null && nickname != null){
			
			if(raumname.equals(raum1Name)){
				
//				ObservableList<String> users = raum1LV.getItems();
//				users.remove(nickname);
//				raum1LV.getItems().clear();
//				raum1LV.getItems().addAll(users);
				raum1LV.getItems().remove(nickname);
				
				
				
			}else if(raumname.equals(raum2Name)){
				
				raum2LV.getItems().remove(nickname);
			}
			
		}
		
		
	}
	private void disconnect(){
		
		hcpClient.closeConnection();
		disconnectB.setDisable(true);
		connectB.setDisable(false);
	}

}
