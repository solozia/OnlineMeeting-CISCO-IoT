package org.openiot.lsm.websocket.server;

//import javax.websocket.server.ServerEndpoint;
//import org.deri.cqels.websocket.server.PublisherServerEndpoint;
import org.glassfish.tyrus.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


//30/01/2015
public class webSocketServer implements Runnable {
	final static Logger logger = LoggerFactory.getLogger(webSocketServer.class);

	public webSocketServer() {

	}

	public static void runServer() {
		// server information
		// String hostIP = "127.0.0.1";// args[0];
		final int port = 8002;// Integer.parstyreInt(args[1]);
		//int port = 8003;// Integer.parstyreInt(args[1]);
		//int port = 8002;// Integer.parstyreInt(args[1]);

		// initialize cqels engine

		// Server server = new Server("localhost", port, "/websockets", null,
		// SubscriberServerEndpoint.class, queryEndpoint.class);
		final Server server = new Server("localhost", port, "/websockets",null, QueryServerEndPoint.class, SubscriberServerEndpoint.class);
		//Server server = new Server("localhost", 8880, "/websockets", QueryServerEndPoint.class);

		try {
			server.start();
			 //BufferedReader reader = new BufferedReader(new
			// InputStreamReader(System.in));
			logger.info("ZIA WEBSOCKET.===========================-----------------------------------------------------------");
			// Constant.Inputquery = reader.readLine();
			// (new Thread(new ReasoningWebSocketServer())).start();

		} catch (final Exception e) {
			throw new RuntimeException(e);
		}  //finally {
			// server.stop();
			// }
	}

	@Override
	public void run() {
		runServer();
	}
	public static void main(String[] args) {
		runServer();
	}
}
