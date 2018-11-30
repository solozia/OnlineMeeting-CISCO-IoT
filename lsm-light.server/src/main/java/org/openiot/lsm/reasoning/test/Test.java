package org.openiot.lsm.reasoning.test;


import java.io.IOException;

import javax.websocket.DeploymentException;

import org.openiot.lsm.reasoning.engine.UserReasoner;


public class Test {

	public static void main(String[] args) throws DeploymentException, InterruptedException, IOException {

		// final String meetingId =
		// "http://www.insight-centre.org/MeetingEvent_1";
		// final String userId = "user2";
		// final Client user1 = new Client(meetingId, userId);
		// (new Thread(user1)).start();
		// Thread.sleep(500);
		//
		UserReasoner reasoner = new UserReasoner(null);
		System.out.println(reasoner.generateQueryRequest("http://www.insight-centre.org/MeetingEvent_90"));

	}

}
