package client.gui;

import java.net.URL;
import java.util.ResourceBundle;

import client.protocol.HCPClient;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;

public class UIController implements Initializable{

	
	@FXML
	TabPane tabpane = new TabPane();
	
	@FXML
	TextArea messageArea = new TextArea();
	
	@FXML
	TextArea inputArea = new TextArea();
	
	@FXML
	Button sendButton = new Button("send");
	
	private HCPClient hcpClient;
	
	private static UIController instance=null;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		instance=this;
		
		hcpClient=new HCPClient("127.0.0.1", 33333);
		
		hcpClient.start();
		
		
		sendButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				String temp = inputArea.getText();
				
				hcpClient.sendMessage(temp, 0);
				messageArea.appendText(temp);
				
				inputArea.setText("");
				
			}
		});
		
		
		
	}
	public static UIController getInstance(){
		return instance;
	}
	public void message(String message, int id){
		
		messageArea.appendText(message+"\n");
		
	}

}
