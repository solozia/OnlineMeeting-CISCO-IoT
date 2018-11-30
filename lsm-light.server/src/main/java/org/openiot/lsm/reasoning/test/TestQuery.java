package org.openiot.lsm.reasoning.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.websocket.DeploymentException;

import org.glassfish.tyrus.server.Server;
import org.openiot.lsm.reasoning.engine.MeetingReasoner;


public class TestQuery {

	public static void main(String[] args) {
		MeetingReasoner reasoner = new MeetingReasoner();
		reasoner.queryMeetingInfo("http://www.insight-centre.org/MeetingEvent_9");

	}

}
