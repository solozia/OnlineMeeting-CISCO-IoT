package org.openiot.lsm.reasoning.websocket;

import javax.websocket.DeploymentException;

import org.glassfish.tyrus.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * @author: Thu-Le Pham
 *
 */
public class ReasoningWebSocketServer implements Runnable{

	final static Logger logger = LoggerFactory.getLogger(ReasoningWebSocketServer.class);

	public ReasoningWebSocketServer() {

	}

	public static void runServer() {

		final int port = 8003;


		final Server rserver = new Server("localhost", port, "/websockets",null, ReasoningServerEndPoint.class);

		try {
			rserver.start();

		} catch (final DeploymentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	@Override
	public void run() {
		runServer();
	}
	public static void main(String[] args) {
		runServer();
	}

}
