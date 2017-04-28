package server.gui;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import server.server.Chatserver;
import server.server.Serverstatus;

public class Main extends Application {

	private Stage stage;

	private Chatserver chatserver;
	
	private java.awt.SystemTray tray;
	
	private PopupMenu iconPopup = new PopupMenu();
	private TrayIcon trayIcon;
	
	private String icontext = "RNChat";

	@Override
	public void start(Stage primaryStage) {

		stage = primaryStage;

		if (SystemTray.isSupported()) {

			Platform.setImplicitExit(false);
			tray = java.awt.SystemTray.getSystemTray();
			chatserver = new Chatserver("MeinServer", 33333);



			MenuItem item = new MenuItem("Beenden");
			MenuItem showWindow = new MenuItem("Config");
			MenuItem startServer = new MenuItem("Start Server");
			MenuItem stopServer = new MenuItem("Stoppe Server");
			stopServer.setEnabled(false);

			iconPopup.add(showWindow);
			iconPopup.addSeparator();
			iconPopup.add(startServer);
			iconPopup.add(stopServer);
			iconPopup.addSeparator();
			iconPopup.add(item);



			// bei klicl auf exit wird das programm beendet
			ActionListener listener = new ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent arg0) {

					chatserver.stopServer();
					
					System.exit(0);
				}
			};

			// bei doppelklick verschwindet das fenster
			// ActionListener listenerTray = new ActionListener() {
			// @Override
			// public void actionPerformed(java.awt.event.ActionEvent arg0)
			// {
			//
			// primaryStage.hide();
			// }
			// };

			// trayIcon.addActionListener(listenerTray);

			item.addActionListener(listener);

			// avtionlistener der dafür sorgt das das hauptfenseter
			// angezeigt wird
			showWindow.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							primaryStage.show();
						}
					});
				}
			});

			startServer.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					chatserver.startServer();
					setIcon(Serverstatus.RUNNING, icontext);
					startServer.setEnabled(false);
					stopServer.setEnabled(true);
				}
			});
			
			stopServer.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					chatserver.stopServer();
					setIcon(Serverstatus.STOPPED, icontext);
					stopServer.setEnabled(false);
					startServer.setEnabled(true);
				}
			});

			trayIcon = new TrayIcon(Toolkit.getDefaultToolkit()
					.getImage(".\\lib\\icon\\systray_rot.jpg"), icontext, iconPopup);
			
			
			try {
				
				tray.add(trayIcon);
				
			} catch (AWTException e1) {
				//wenn das Icon dem tray nicht hinzugefügt werden kann..
				e1.printStackTrace();
			}

			
			setIcon(Serverstatus.STOPPED,icontext);

		}

		try {
			AnchorPane root = (AnchorPane) FXMLLoader.load(getClass().getResource("MainServer.fxml"));
			Scene scene = new Scene(root, 400, 400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
//			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	private void showStage() {
		if (stage != null) {
			stage.show();
			stage.toFront();
		}

	}
	
	/**
	 * Ändert das Trayicon abhängig vom Status des Servers
	 * @param status
	 */
	private void setIcon(Serverstatus status, String icontext){
	
		Image image;
		
		
		
		if(status == Serverstatus.STOPPED){
			image = Toolkit.getDefaultToolkit()
					.getImage(".\\lib\\icon\\systray_rot.jpg");
		}else if(status == Serverstatus.RUNNING){
			image = Toolkit.getDefaultToolkit()
					.getImage(".\\lib\\icon\\systray_gruen.jpg");
		}else{
			image = Toolkit.getDefaultToolkit()
					.getImage(".\\lib\\icon\\systray_rot.jpg");
		}
		
		
		trayIcon.setImage(image);
//		trayIcon.setPopupMenu(iconPopup);
//		trayIcon.set
		
				
		
	}
}
