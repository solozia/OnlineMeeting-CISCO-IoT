package org.openiot.lsm.reasoning.engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.websocket.Session;

import org.openiot.lsm.reasoning.data.AgendaItem;
import org.openiot.lsm.reasoning.data.Constants;
import org.openiot.lsm.reasoning.data.Coordinate;
import org.openiot.lsm.reasoning.data.Meeting;
import org.openiot.lsm.reasoning.data.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 *
 */
public class MeetingReasoner {

	final static Logger logger = LoggerFactory.getLogger(MeetingReasoner.class);

	public static Map<Session, UserReasoner> sessionUserReasoner = new HashMap(); //1 userSession - 1 UserReasoner
	public static boolean start = false;
	Meeting meeting = new Meeting();

	public static String notificationRulesFile =String.format("%sNotificationRules.lp", Constants.aspURI);
	public static String notificationRules;




	public MeetingReasoner()  {
		super();
		//read Rules
		notificationRules = readAspRules(notificationRulesFile);

	}


	public Meeting getMeeting() {
		return meeting;
	}

	public void setMeeting(Meeting meeting) {
		this.meeting = meeting;
	}


	/*
	 * Read asp files: NotificationRules.lp
	 */
	public String readAspRules(String ruleFile){
		final StringBuilder aspRules = new StringBuilder();
		String line = "";
		try {
			final BufferedReader br = new BufferedReader(new FileReader(
					ruleFile));
			while ((line = br.readLine()) != null) {
				aspRules.append(line).append("\n");
			}
		} catch (final IOException e) {
			aspRules.append("");
			e.printStackTrace();
		}
		return aspRules.toString();
	}


	/*
	 * query meeting information
	 */
	public void queryMeetingInfo(String meetingId){

		//--- MOCK UP DATA OF MEETING
		this.meeting.setMeetingStartTime(System.currentTimeMillis());
		this.meeting.setMeetingEndTime(System.currentTimeMillis() + 3600*1000);
		this.meeting.setMeetingLocation("RoomB");
		this.meeting.setMeetingCoordinate(new Coordinate(53.28991301691684,-9.074204787611961));

		this.meeting.setMeetingOrganizerId("pltanhthu13");

		final List<String> attendees = new ArrayList<String>();
		attendees.add("naomi18");
		this.meeting.setAttendees(attendees);

		final List<AgendaItem> items = new ArrayList<AgendaItem>();
		AgendaItem item = new AgendaItem("item1");
		item.setAgendaItemOrder(1);
		item.setAgendaItemTime(20*60*1000); // 20 minutes
		item.setAgendaItemTitle("Internet of Thing");
		items.add(item);
		this.meeting.getAgendaItemUser().put(item.getAgendaItemId(), "pltanhthu13");

		item = new AgendaItem("item2");
		item.setAgendaItemOrder(2);
		item.setAgendaItemTime(30*60*1000); // 20 minutes
		item.setAgendaItemTitle("Reasoning");
		items.add(item);
		this.meeting.getAgendaItemUser().put(item.getAgendaItemId(), "naomi18");

		this.meeting.setAgendaItems(items);

		logger.info("MeetingId: = " + meetingId);
		logger.info("MeetingStartTime: = " + this.meeting.getMeetingStartTime());
		logger.info("MeetingEndTime:= " + this.meeting.getMeetingEndTime());
		logger.info("MeetingLocation:= " + this.meeting.getMeetingLocation());

		logger.info("MeetingOrganizer: = " + this.meeting.getMeetingOrganizerId());
		logger.info("MeetingAttendees:= ");
		for(final String s: this.meeting.getAttendees()){
			logger.info(s);
		}
		logger.info("MeetingAgendaItems: = " + this.meeting.getAgendaItems());
		for(final AgendaItem s: this.meeting.getAgendaItems()){
			logger.info(s.toString());
		}
		logger.info("MeetingAgendaItemUser: = " + this.meeting.getAgendaItemUser());



		///----------->


//		StringBuilder prefix = new StringBuilder();
//		prefix.append("PREFIX ncal: <http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#>\n")
//		.append("PREFIX mms: <http://www.insight-centre.org/ontologies/2015/02/06/mms_ioe#>\n")
//		.append("PREFIX pimo: <http://www.semanticdesktop.org/ontologies/2007/11/01/pimo#>\n")
//		.append("PREFIX agenda-ont: <http://www.daml.org/2001/10/agenda/agenda-ont#>\n")
//		.append("\n");
//
//		StringBuilder queryStr = new StringBuilder();
//		queryStr.append(prefix)
//		.append("SELECT ?meetingStartTime ?meetingEndTime ?meetingLocation ?meetingOrganizer\n")
//		.append("WHERE\n{")
//		.append("<").append(meetingId).append("> ncal:DtStart ?startDate.\n")
//		.append("?startDate").append(" ncal:DateTime ?meetingStartTime.\n")
//
//		.append("<").append(meetingId).append("> ncal:DtEnd ?endDate.\n")
//		.append("?endDate").append(" ncal:DateTime ?meetingEndTime.\n")
//
//		.append("<").append(meetingId).append("> pimo:HasLocation ?location.\n")
//		.append("?location").append(" pimo:LocatedWithin ?meetingLocation.\n")
//
//		.append("<").append(meetingId).append("> ncal:HasOrganizer ?meetingOrganizer.\n")
//		.append("?organizer").append(" agenda-ont:userId ?meetingOrganizer.}\n");
//
//		Query query = QueryFactory.create(queryStr.toString());
//		QueryExecution qexec = QueryExecutionFactory.sparqlService("http://srvgal90.deri.ie:8890/sparql", query, "http://lsm.deri.ie/OpenIoT/eventcalender#");
//
//		// Set the specific timeout.
//		((QueryEngineHTTP) qexec).addParam("timeout", "10000");
//		// Execute.
//		ResultSet rst = qexec.execSelect();
////		ResultSetFormatter.out(System.out,  rst, query); //output
//
//		for (; rst.hasNext();) {
//			QuerySolution qr = rst.next();
//			String result = qr.get("meetingStartTime").toString();
//			this.meeting.setMeetingStartTime(Long.parseLong(result.substring(0,result.indexOf("^"))));
//			logger.info("Meeting Start time = " + result);
//
//			result = qr.get("meetingEndTime").toString();
//			this.meeting.setMeetingEndTime(Long.parseLong(result.substring(0,result.indexOf("^"))));
//			logger.info("Meeting End time = " + result);
//
//			result = qr.get("meetingLocation").toString();
//			this.meeting.setMeetingLocation(result.substring(0,result.indexOf("^")));
//			logger.info("Meeting Location = " + result);
//
//			//mock up coordinate of meeting Location
//			this.meeting.setMeetingCoordinate(new Coordinate(56.128890576859355,10.161577951049312));
//
//			//TODO: get Id of organizer
//			result = qr.get("meetingOrganizer").toString();
////			this.meeting.setMeetingOrganizerId(new User(result.substring(0,result.indexOf("^"))));
//
//			logger.info("Meeting Organizer = " + result);

//		}


		//query Attendees
//		StringBuilder queryStr = new StringBuilder();
//
//
//		.append("<").append(meetingId).append("> ncal:HasAttendee ?meetingAttendee.\n")
//		.append("?attendee").append(" agenda-ont:userId ?meetingAttendee.}\n");
//
//		.append("<").append(meetingId).append("> mms:hasAgenda ?meetingAgenda.\n")
//		.append("?meetingAgenda").append(" agenda-ont:order ?order.}\n");
//		.append("?item").append("> agenda-ont:userId ?meetingAgenda.}\n");

//		qexec.close();
	}





//	public Map<String, Session> getUserSession() {
//		return userSession;
//	}
//
//
//	public void setUserSession(Map<String, Session> userSession) {
//		this.userSession = userSession;
//	}

	/*
	 * This function create a Client for a User to connect with Query Component
	 */
//	public void startQueryClient(URI queryServerURI, User user){
//
//        QueryClientEndPoint clientEndPoint = new QueryClientEndPoint(queryServerURI);
//        // add listener
//        clientEndPoint.addMessageHandler(new QueryClientEndPoint.MessageHandler() {
//            public void handleMessage(String message) {
//               QueryClientEndPoint.logger.info("Received message: " + message);
//               Reasoner.this.receiveFromQuery(message);// received from Query Component
//            }
//        });
////        this.setQueSession(clientEndPoint.getUserSession());
//        this.sessionMap.put(Constants.QUERYSESSION, clientEndPoint.getUserSession());
//
//	}

	public void receiveNewUser(String userId, Session userSession){
		//check if the user is in meeting
		if(this.getMeeting().indexOf(userId) != null){
			logger.info("The user " + userId + " has been in the meeting!");
		}
		else{
			logger.info("The meeting " +  this.getMeeting().getMeetingId() + " receives new user " +  userId);
			// create the User
			final User user = new User(userId);
			//add User to the Meeting
			this.getMeeting().getUsers().add(user);

			//create new UserReasoner
			final UserReasoner userReasoner = new UserReasoner(this.meeting,
					user, userSession);

			this.sessionUserReasoner.put(userSession, userReasoner);

			userReasoner.startSubscribeEvent();

		}
	}
	/*
	 * manage when user logout
	 */
	public void deleteUser(Session userSession){
		 try {
		 UserReasoner userReasoner =
		 this.sessionUserReasoner.get(userSession);
		 //remove user from meeting
		 this.meeting.getUsers().remove(userReasoner.getUser());
		 logger.info("Deleted user " +
		 this.sessionUserReasoner.get(userSession).getUser().getUserId() +
		 " from the meeting " + this.meeting.getMeetingId());
		 //close the connection with QueryComponent
		 logger.info("Closed QUERRYSESSION = " +
		 userReasoner.getQuerySession().getId());
		 userReasoner.getQuerySession().close();
		 // logger.info("Closed the Query session " + userSession.getId());
		 //remove UserReasoner from Map
		 this.sessionUserReasoner.remove(userSession);
		 userReasoner = null;


		 } catch (final IOException e) {
		 // TODO Auto-generated catch block
		 e.printStackTrace();
		}


	}

}
