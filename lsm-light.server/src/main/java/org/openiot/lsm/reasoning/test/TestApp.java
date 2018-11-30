package org.openiot.lsm.reasoning.test;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.websocket.DeploymentException;

import org.openiot.lsm.reasoning.data.Constants;
import org.openiot.lsm.reasoning.websocket.ApplicationClientEndPoint;



//import org.openiot.lsm.websocket.server.QueryServerEndPoint;



import com.google.gson.JsonObject;

public class TestApp {

	public static void main(String[] args) throws DeploymentException, InterruptedException, IOException {
		int[] N = {25, 125, 250, 1250, 2500};
		int n; // number of user
		
		for(int i = 0; i < N.length; i++){
			n = N[i]; 
			String meetingId = "http://www.insight-centre.org/MeetingEvent_1";
			for(int u = 1; u <= n; u ++){
				String userId = String.format("user%d", u);
				Client user1 = new Client(meetingId, userId);
				(new Thread(user1)).start();
				Thread.sleep(500);
				
			}
		}
	}

}
