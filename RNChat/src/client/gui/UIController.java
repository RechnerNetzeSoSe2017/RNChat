package client.gui;

import java.net.URL;
import java.util.ResourceBundle;

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

public class UIController implements Initializable{

	
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
		
		
		
		instance=this;
		
//		hcpClient=new HCPClient("127.0.0.1", 33333);
//		
//		hcpClient.start();
//		
		
		sendButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				String temp = inputArea.getText();
				
				hcpClient.sendMessage(temp, "Server");
				messageArea.appendText("\n"+temp);
				
				inputArea.setText("");
				
			}
		});
		
		
		
		Menu menuConnect = new Menu("Connect");
		MenuItem connToServer = new MenuItem("connect to Server..");
		
		menuConnect.getItems().add(connToServer);
		
		menubar.getMenus().add(menuConnect);
		
		//eventlistener für den menüpunkt "connect To Server.."
		connToServer.setOnAction(actionEvent -> {});
		
		disconnectB.setDisable(true);
		
		connectB.setOnAction(actionEvent ->{connectToServer();});
		disconnectB.setOnAction(actionEvent ->{disconnect();});
		joinB.setOnAction(actionEvent -> {abboniereChannel();});
		leaveB.setOnAction(actionEvent ->{deabboniereChannel();});
		
		raum1Senden.setOnAction(actionEvent ->{
			
			if(raum1Besetzt){
				String nachricht = raum1TF.getText();
				
				raum1TF.setText("");
				
				hcpClient.sendMessage(nachricht, raum1Name);
				
				
			}
			
		});
		raum2Senden.setOnAction(actionEvent ->{
			
			if(raum2Besetzt){
			String nachricht = raum2TF.getText();
			
			raum2TF.setText("");
			
			hcpClient.sendMessage(nachricht, raum2Name);
			
			}
			
		});
		
		
		
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
				hcpClient.setListViewForNicklist(name, raum1LV.getItems());
				raum1Name=name;
			}else if(!raum2Besetzt){
				raum2TA.clear();
				hcpClient.setTextareaForRoom(name, raum2TA);
				raum2LV.getItems().clear();
				hcpClient.setListViewForNicklist(name, raum2LV.getItems());
				raum2Name=name;
			}
			
			if(raum1Besetzt && raum2Besetzt){
				joinB.setDisable(true);
			}
			
		}
		
		
	}
	public static UIController getInstance(){
		return instance;
	}
	public void message(String message, int id){
		
		messageArea.appendText(message+"\n");
		
	}
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
	private void disconnect(){
		hcpClient.closeConnection();
		disconnectB.setDisable(true);
		connectB.setDisable(false);
	}

}
