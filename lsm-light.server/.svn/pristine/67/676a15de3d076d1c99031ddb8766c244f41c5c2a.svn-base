package org.openiot.lsm.reasoning.websocket;

/*
 * @author Thu-Le Pham
 */

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.openiot.lsm.reasoning.common.ApplicationRequest;
import org.openiot.lsm.reasoning.data.Constants;
import org.openiot.lsm.reasoning.engine.MeetingReasoner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


@ServerEndpoint(value = "/application")
public class ReasoningServerEndPoint {

	final static Logger logger = LoggerFactory.getLogger(ReasoningServerEndPoint.class);

	/*
	 * 1 meeting - 1 MeetingReasoner
	 */
	public static Map<String, MeetingReasoner> meetingReasoner = new HashMap();

	/*
	 * n sessions (users) - 1 MeetingReasoner
	 */
	public static Map<Session, MeetingReasoner> sessionReasoner = new HashMap();

	@OnOpen
    public void onOpen(Session session){
		logger.info(session.getId() + " has opened a connection");
    }


    @OnMessage
    public synchronized String onMessage(String message, Session session) throws IOException {

    	logger.info("Received message: MESSAGEID: " + message + " " + System.currentTimeMillis());

		// parse message to ApplicationRequest
		final ApplicationRequest appRequest = this
				.parseApplicationMessage(message);

		// check if meetingId is null, send data back to Application
		if (appRequest.getMeetingId() == null) {
			session.getAsyncRemote().sendText(message);
		} else {
			// check if userId is null, then send message back to Application
			if (appRequest.getUserId() == null) {
				session.getAsyncRemote().sendText(message);
			} else {
				final String meetingId = appRequest.getMeetingId();
				final String userId = appRequest.getUserId();
				logger.info("Received meetingId = " + meetingId);
				logger.info("ReasonerReceiveUser(T2): USERID: " + userId + " "
						+ System.currentTimeMillis());
				// if new meeting is not registered
				if (!meetingReasoner.keySet().contains(meetingId)) {
					logger.info("New meeting is created: " + meetingId);
					final MeetingReasoner reasoner = new MeetingReasoner();
					// query information of meeting
					reasoner.getMeeting().setMeetingId(meetingId);
					reasoner.queryMeetingInfo(meetingId);
					this.meetingReasoner.put(meetingId, reasoner);
					this.sessionReasoner.put(session, reasoner);
					reasoner.receiveNewUser(userId, session);
				} else {
					this.sessionReasoner.put(session,
							this.meetingReasoner.get(meetingId));
					this.meetingReasoner.get(meetingId).receiveNewUser(userId,
							session);

				}

			}

		}
        return message;
    }

    @OnError
    public void onError(Throwable t) {
        t.printStackTrace();
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
		logger.info("Closed APPLICATIONSESSION =  " + session.getId());
		MeetingReasoner reasoner = this.sessionReasoner.get(session);
		final String meetingId = reasoner.getMeeting().getMeetingId();
		// TODO: try to destroy all related functions
		reasoner.deleteUser(session);
		// check if the meeting has no user
		if (reasoner.getMeeting().getUsers().isEmpty()) {
			logger.info("The meeting " + reasoner.getMeeting().getMeetingId()
					+ " has no user ");
			// delete this meeting/ reasoner
			final Collection<MeetingReasoner> list = this.sessionReasoner
					.values();
			for (final Iterator<MeetingReasoner> itr = list.iterator(); itr
					.hasNext();) {
				if (Collections.frequency(list, itr.next()) > 0) {
					itr.remove();
				}
			}
			logger.info("After delete sesionReasoner = "
					+ this.sessionReasoner.size());
			// delete meetingReasoner
			this.meetingReasoner.remove(meetingId);
			logger.info("After delete meetingReasoner = "
					+ this.meetingReasoner.size());
			reasoner = null;

		}

    }

	/*
	 * parse message to ApplicationRequest
	 */
	public ApplicationRequest parseApplicationMessage(String message) {
		final ApplicationRequest appRequest = new ApplicationRequest();
		final JsonParser parser = new JsonParser();
		final JsonObject jo = (JsonObject) parser.parse(message);
		JsonElement je = jo.get(Constants.MEETING_ID);
		if (je != null) {
			appRequest.setMeetingId(je.getAsString());
			je = jo.get(Constants.USER_ID);
			if (je != null) {
				appRequest.setUserId(je.getAsString());
			}
		}
		return appRequest;
	}

}
