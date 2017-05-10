package server.protokol;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

import server.server.ServerExecutor;
import server.server.Serverstatus;

/**
 * Wenn gestartet, horcht er auf einem bestimmten Port und öffnet so viele
 * Verbindungen wie es die Konfiguration des Threadpools hergibt.
 * 
 * @author Höling
 *
 */
public class ChatSocketListener extends Thread {

	private int portNr = 33333;
	private int maxClients = 10;
	private ServerExecutor threadPoolModus = ServerExecutor.FixedThredPool;

	// der status ob gelistened wird oder nicht
	private Serverstatus operationStatus = Serverstatus.STOPPED;

	// wird benötigt um den server "sanft" zu beenden, also keine neuen
	// verbindungen aufbauen zu lassen..
	private boolean listen = true;

	private ServerSocket serverSocket;

	// hält eine Übersicht über alle verbundenen clients
	private static ArrayList<HPCServer> clients = new ArrayList<>();

	/**
	 * Erzeugt ein neues Chatptotokoll auf einem port.
	 * 
	 * @param port
	 *            muss von 0 - 655535 sein (default ist 33333)
	 * @param clients
	 *            maximale Anzahl an Clients (abhängig vom Modus) (default ist
	 *            10)
	 * @param modus
	 *            ist für die Erstellung der Verbindungsthreads mit den Clients.
	 *            {@code ServerExecutor.FixedThreadPool} (default) erzeugt einen
	 *            Pool an Threads mit fester Anzahl.
	 *            {@code ServerExecutor.WorkStealingPool} sorgt für maximale
	 *            parallelität. Wird von Java intern verwaltet..
	 */
	public ChatSocketListener(int port, int clients, ServerExecutor modus) {
		portNr = port;
		maxClients = clients;
		threadPoolModus = modus;
	}

	/**
	 * Erzeugt ein neues Chatptotokoll auf einem port.
	 * 
	 * @param port
	 *            muss von 0 - 655535 sein (default ist 33333)
	 * @param clients
	 *            maximale Anzahl an Clients (abhängig vom Modus) (default ist
	 *            10)
	 * 
	 */
	public ChatSocketListener(int port, int clients) {
		portNr = port;
		maxClients = clients;

	}

	@Override
	public void run() {
		// sonst würde es die möglichkeit geben das der server angehalten wird
		// bevor er gestartet wird..
		listen = true;

		try {
			serverSocket = new ServerSocket(portNr);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ExecutorService executorPool = Executors.newFixedThreadPool(maxClients);

		if (threadPoolModus == ServerExecutor.FixedThredPool) {
			// executorPool = Executors.newFixedThreadPool(maxClients);
		} else if (threadPoolModus == ServerExecutor.WorkStealingPool) {
			executorPool = Executors.newWorkStealingPool();
		} else if (threadPoolModus == ServerExecutor.ForkJoinPool) {
			// hier dann irgendwie forkjoin nehmen..
		}

		operationStatus = Serverstatus.RUNNING;
		log("start Listening on Socketaddress " + serverSocket.getInetAddress() + " on Port " + portNr);

		while (listen) {
			Socket client = null;

			try {

				// es wird versucht eine neue verbindung einzugehen
				client = serverSocket.accept();
				if (client != null) {
					HPCServer clientconn = new HPCServer(client);

					clients.add(clientconn);

					// es werden so lange verbindungen angenommen bis der pool
					// erschöpft ist
					executorPool.execute(clientconn);
				}

			} catch (RejectedExecutionException ree) {
				// wenn der executorpool keine clients mehr annehmen kann..
			} catch (SocketException e) {
				// wenn der Socket geschlossen wird.. oder was noch?
			} catch (IOException e) {
				// TODO gescheites fehlerhandling..
				e.printStackTrace();
			}

		}

		operationStatus = Serverstatus.STOPPED;
		executorPool.shutdown();

	}

	private void log(String string) {
		System.out.println("" + string);

	}

	/**
	 * Sorgt dafür das der Server keine weiteren Verbindungen mehr annimmt und
	 * versucht den ServerSocket zu Schliessen
	 */
	public void stopListen() {
		listen = false;

		log("stop listening on port " + portNr);

		if (serverSocket != null) {

			for (HPCServer elem : clients) {
				elem.closeConnection();
			}

			try {
				serverSocket.close();
			} catch (IOException e) {
				// TODO gescheites errorhandling..

				// tritt nur auf wenn beim schliessen des Sockets noch gelesen
				// oder geschrieben wird/werden soll..
				e.printStackTrace();
			}

		}
		interrupt();
	}
	public static void removeClient(HPCServer client){
		
		clients.remove(client);
		
	}

}
