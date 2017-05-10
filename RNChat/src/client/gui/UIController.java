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
	
	//---------------------------------------------------
	
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
				
				hcpClient.sendMessage(temp, 0);
				messageArea.appendText(temp);
				
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
		
		
		
	}
	public static UIController getInstance(){
		return instance;
	}
	public void message(String message, int id){
		
		messageArea.appendText(message+"\n");
		
	}
	
	private void connectToServer(){
		String ip = serverIPTF.getText();
		String nickname = nicknameTF.getText();
		
		if(!ip.equals("") && !nickname.equals("")){
			hcpClient=new HCPClient(ip, 33333);
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
