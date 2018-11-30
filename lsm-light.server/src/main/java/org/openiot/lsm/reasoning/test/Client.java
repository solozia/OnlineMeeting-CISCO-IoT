package org.openiot.lsm.reasoning.test;

import java.net.URI;
import java.net.URISyntaxException;

import org.openiot.lsm.reasoning.data.Constants;
import org.openiot.lsm.reasoning.websocket.ApplicationClientEndPoint;

import com.google.gson.JsonObject;

public class Client implements Runnable{
	
	String meetingId, userId; 
	

	public Client(String meetingId, String userId) {
		super();
		this.meetingId = meetingId;
		this.userId = userId;
	}


	@Override
	public void run() {
		//Mock up User of Open Meeting
				try {
		            // open websocket
					URI uri =  new URI("ws://localhost:8003/websockets/application");
		            final ApplicationClientEndPoint clientEndPoint = new ApplicationClientEndPoint(uri);
		            // add listener
		            clientEndPoint.addMessageHandler(new ApplicationClientEndPoint.MessageHandler() {
		                public void handleMessage(String message) {
		                  ApplicationClientEndPoint.logger.info("Aplication received message: " + message);
		                }
		            });
		                     
		            JsonObject jo = new JsonObject();
		    		jo.addProperty(Constants.MEETING_ID, meetingId);
		    		jo.addProperty(Constants.USER_ID, userId);
		            clientEndPoint.sendMessage(jo.toString());
		            
		            clientEndPoint.getLatch().await();
		    		
		        } catch (URISyntaxException ex) {
		            System.err.println("URISyntaxException exception: " + ex.getMessage());
		        } catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
	}
	
}
