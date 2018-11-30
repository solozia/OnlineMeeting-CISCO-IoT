package org.openiot.lsm.reasoning.engine;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.websocket.Session;

import org.openiot.lsm.reasoning.aspjavamanager.data.Formula;
import org.openiot.lsm.reasoning.aspjavamanager.data.Formulas;
import org.openiot.lsm.reasoning.aspjavamanager.manager.AspManager;
import org.openiot.lsm.reasoning.data.Constants;
import org.openiot.lsm.reasoning.data.Meeting;
import org.openiot.lsm.reasoning.data.User;
import org.openiot.lsm.reasoning.websocket.QueryClientEndPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

/*
 * @author: Thu-Le Pham
 *
 * This class performs reasoning tasks of a user in a meeting.
 *
 */
public class UserReasoner extends Thread{
	final static Logger logger = LoggerFactory.getLogger(UserReasoner.class);

	Meeting meeting;
	User user;
	String proStr;// asp program
	List<Listener> Listeners = new ArrayList<Listener>();
	Session applicationSession;
	Session querySession;

	// Map<String, Session> sessionMap = new HashMap();



	public UserReasoner(User user) {
		super();
		this.user = user;
		// add listener to UserReasoner
		this.addListener(new QueryListener(this));
	}

	public UserReasoner(Meeting meeting, User user, Session applicationSession) {
		super();
		this.meeting = meeting;
		this.user = user;
		this.applicationSession = applicationSession;
		// add listener to UserReasoner
		this.addListener(new QueryListener(this));
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}


	// public Map<String, Session> getSessionMap() {
	// return sessionMap;
	// }
	//
	// public void setSessionMap(Map<String, Session> sessionMap) {
	// this.sessionMap = sessionMap;
	// }

	public Session getApplicationSession() {
		return applicationSession;
	}

	public void setApplicationSession(Session applicationSession) {
		this.applicationSession = applicationSession;
	}

	public Session getQuerySession() {
		return querySession;
	}

	public void setQuerySession(Session querySession) {
		this.querySession = querySession;
	}

	/*
	 * Register a listener
	 */
	public void addListener(Listener listener) {
		if (!this.Listeners.contains(listener)) {
			this.Listeners.add(listener);
		}
	}

	public Meeting getMeeting() {
		return meeting;
	}

	public void setMeeting(Meeting meeting) {
		this.meeting = meeting;
	}

	/*
	 * receive JsonObject from Query
	 */
	public void receiveFromQuery(String s){
		fireEvents(s,Constants.QUERYLISTENER);
	}

	/*
	 *
	 * call this method whenever you want to notify
	 * the event listeners of the particular event
	 */
	private void fireEvents(String e, String type) {

	    final Event event = new Event(this, e);
	    final Iterator i = this.Listeners.iterator();
	    while(i.hasNext())  {
	    	final Listener listener = (Listener) i.next();
	    	if(type.equals(Constants.QUERYLISTENER)){
	    		if(listener.getClass() == QueryListener.class) {
					listener.update(event);
				}
	    	}

	    }
	}

	/*
	 * parse User's information to asp
	 */
	public String parseAsp(){
		final StringBuilder asp = new StringBuilder();
		//--> value(userId,feature,featurevalue)
		final String userId = this.user.getUserId();
		String temp = String.format("user(%s).", userId);
		asp.append(temp).append("\n");
		for(final String s: this.user.getFeatures().keySet()){
			final String feature = this.user.getFeatures().get(s).toLowerCase();
			temp = String.format("value(%s,%s,%s).", userId,s.toLowerCase(), feature);
			asp.append(temp).append("\n");
		}
		asp.append(String.format("value(%s,%s,%s).", userId,Constants.USER_PLACE.toLowerCase(), user.getUserPlace()));
		asp.append(String.format("value(%s,%s,%s).", userId,Constants.USER_ACTIVITY.toLowerCase(), user.getUserActivity()));
		logger.info("asp string = " + asp.toString());
		return asp.toString();
	}


	/*
	 * 2015-03-20
	 * This function compute the capabilities of a user
	 */
	public void notifyCapabilities() throws UnsupportedOperationException, IOException, CloneNotSupportedException{
		//parse asp program
		this.proStr =parseAsp().concat( MeetingReasoner.notificationRules);
		//call clingo
		final AspManager aspManager = new AspManager();
		aspManager.setClingoURI(Constants.aspURI);
		aspManager.callClingo(proStr);
		logger.info("Asp answer set = \n" + aspManager.getResult());
		//get answer set
		if(!aspManager.getResult().isEmpty()){
			final Formulas answerSets = aspManager.getResult().get(0);
			for(final Formula f: answerSets.getFormulas()){
				final String s = f.getF();// on(thu,share); off(thu,talk)
				final String result = s.substring(0,s.indexOf('('));
				final String capability = s.substring(s.indexOf(',')+1, s.indexOf(')'));
				this.user.getCapabilities().put(capability.toUpperCase(),result.toUpperCase());
			}

			//send result to Application
			 final JsonObject request = new JsonObject();
		     request.addProperty(Constants.MEETING_ID, this.meeting.getMeetingId());
		     request.addProperty(Constants.USER_ID, user.getUserId());
		     final Map<String,String> CAPABILITIES = new HashMap();
			 for(final String sc: user.getCapabilities().keySet()){
				if(user.getCapabilities().get(sc).equals(Constants.ON)){
						CAPABILITIES.put(sc, "true");
				}
				if(user.getCapabilities().get(sc).equals(Constants.OFF)){
						CAPABILITIES.put(sc, "false");
				}

			 }

		        final JsonArray capabilities = new JsonArray();

		        JsonObject capability = new JsonObject();


		        capability.addProperty(Constants.SHARE,CAPABILITIES.get(Constants.SHARE));
		        capabilities.add(capability);

		        capability = new JsonObject();
		        capability.addProperty(Constants.LISTEN,CAPABILITIES.get(Constants.LISTEN));
		        capabilities.add(capability);



		        capability = new JsonObject();
		        capability.addProperty(Constants.READ,CAPABILITIES.get(Constants.READ));
		        capabilities.add(capability);

		        capability = new JsonObject();
		        capability.addProperty(Constants.TALK,CAPABILITIES.get(Constants.TALK));
		        capabilities.add(capability);


		        capability = new JsonObject();
		        capability.addProperty(Constants.TYPE,CAPABILITIES.get(Constants.TYPE));
		        capabilities.add(capability);


		        request.add(Constants.CAPABILITIES, capabilities);

			final Gson gson = new Gson();
			final String jo = gson.toJson(request);
			logger.info("Using session" + this.applicationSession.getId()
					+ " of User = " + this.user.getUserId()
					+ " to Send to Application = " + jo);
			//send results to Application
			this.applicationSession.getAsyncRemote().sendText(jo);
		}
	}

	@Override
	public void run() {
		System.out.println("UserReasoner of" + user.getUserId() + " is running.......");
		try {
			notifyCapabilities();

		} catch (final UnsupportedOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}



	/*
	 * 2015-03-20
	 * This function generates a request(for 1 user) for Query Component
	 */
	public String generateQueryRequest(String meetingId){

		 //create Json object to QueryComponent
        final JsonObject request = new JsonObject();
        request.addProperty(Constants.MEETING_ID, meetingId);

        final JsonArray users = new JsonArray();
        final JsonObject user = new JsonObject();
		user.addProperty(Constants.USER_ID, "thu");
        users.add(user);
        request.add(Constants.USER, users);

        final JsonArray capabilities = new JsonArray();

        JsonObject capability = new JsonObject();
        capability.addProperty(Constants.NOISE,Constants.Noise);
        capabilities.add(capability);

        capability = new JsonObject();
        capability.addProperty(Constants.LIGHT, Constants.Light);
        capabilities.add(capability);

        //2015/04/22 comment out for testing
//        if(!this.user.getUserDevice().equals(Constants.LAPTOP)){
//        	logger.info("User " + this.user.getUserId() + " do not use Laptop" );
        	 capability = new JsonObject();
             capability.addProperty(Constants.PROXIMITY,Constants.Proximity);
             capabilities.add(capability);
//        }

        capability = new JsonObject();
        capability.addProperty(Constants.LOCATION,Constants.Location);
        capabilities.add(capability);

        //2015/04/22 Comment out for testing
//        capability = new JsonObject();
//        capability.addProperty(Constants.USER_PLACE,Constants.User_Place);
//        capabilities.add(capability);
//
//        capability = new JsonObject();
//        capability.addProperty(Constants.USER_ACTIVITY,Constants.User_Activity);
//        capabilities.add(capability);

        // added NFCTag on 2015-09-11
        capability = new JsonObject();
        capability.addProperty("NFC_Tag","NFC_Tag");
        capabilities.add(capability);

        
        request.add(Constants.CAPABILITY, capabilities);
        return request.toString();
	}


	public void queryUserDevice(){

		logger.info("Start query device of User = " + user.getUserId());
		final StringBuilder queryStr = new StringBuilder();

		queryStr
		.append("SELECT ?deviceType \n")
		.append("WHERE\n{")
		.append("<http://www.semanticdesktop.org/ontologies/2007/11/01/pimo/").append(this.user.getUserId()).append("> <http://www.semanticdesktop.org/ontologies/2011/10/05/ddo#owns> ?deviceId.\n")
		.append("?deviceId").append(" <http://insight-centre.org/CiscoIoE/deviceType> ?deviceType.}\n");


		final Query query = QueryFactory.create(queryStr.toString());
		final QueryExecution qexec = QueryExecutionFactory.sparqlService(Constants.virtuosoURI, query, "http://lsm.deri.ie/OpenIoT/sensormeta#");

		// Set the specific timeout.
		((QueryEngineHTTP) qexec).addParam("timeout", "10000");

		// Execute.
		final ResultSet rst = qexec.execSelect();
//		ResultSetFormatter.out(System.out,  rst, query); //output


		while (rst.hasNext()) {
			final QuerySolution qr = rst.next();
			final String result = qr.get("deviceType").toString();
			logger.info("User" + user.getUserId() + " has Device = " + result);
			user.setUserDevice(result);
		}
		qexec.close();
	}

	/*
	 *
	 * This function create a Client to connect with Query Component
	 */
	public void startQueryClient(URI queryServerURI){
		//create QueryClient for the user to communicate with QueryComponent
		QueryClientEndPoint clientEndPoint;
		try {
			clientEndPoint = new QueryClientEndPoint(new URI(Constants.queryServerURI));
			// add listener
	        clientEndPoint.addMessageHandler(new QueryClientEndPoint.MessageHandler() {
	            @Override
				public void handleMessage(String message) {
	               QueryClientEndPoint.logger.info("Received message: " + message);
	               UserReasoner.this.receiveFromQuery(message);
	            }
	        });
	        //add QuerySession to UserReasoner
			this.querySession = clientEndPoint.getUserSession();
		} catch (final URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public void notifyLocation(){
//		boolean inside = insideMeetingRoom(this.meeting.getMeetingCoordinates(), this.user.getUserCoordinate());
		final double distance = this.meeting.getMeetingCoordinate().distance(
				this.user.getUserCoordinate());
//		double distance = distance(new Coordinate(53.2899978, -9.0742487), this.user.getUserCoordinate());
//		logger.info("Coordinate of meeting room = 53.2899978, -9.0742487");
		logger.info("user location = "
				+ this.user.getUserCoordinate().getLongitude() + ", "
				+ this.user.getUserCoordinate().getLatitude());
		logger.info("distance = " + distance);
		final JsonObject request = new JsonObject();
	    request.addProperty(Constants.MEETING_ID, this.meeting.getMeetingId());
	    request.addProperty(Constants.USER_ID, user.getUserId());
	    request.addProperty("DISTANCE", String.format("%s", distance));
//	    if(inside){
//	    	request.addProperty("USER_LOCATION","INSIDE");
//	    }
//	    else{
//	    	request.addProperty("USER_LOCATION", "OUTSIDE");
//	    }

	    final Gson gson = new Gson();
		final String jo = gson.toJson(request);
		logger.info("Using session" + this.applicationSession.getId()
				+ " of User = " + this.user.getUserId()
				+ " to Send to Application = " + jo);
		//send results to Application
		this.applicationSession.getAsyncRemote().sendText(jo);

	}

	public void notifyNFCTagData(){

		final JsonObject request = new JsonObject();
	    request.addProperty(Constants.MEETING_ID, this.meeting.getMeetingId());
	    request.addProperty(Constants.USER_ID, user.getUserId());
	    
	    final JsonArray capabilities = new JsonArray();

        JsonObject capability = new JsonObject();
        capability.addProperty("ID",this.user.getNfcTagData().getNfcId());
        capabilities.add(capability);

        capability = new JsonObject();
        capability.addProperty("ROOM_NAME",this.user.getNfcTagData().getRoomName());
        capabilities.add(capability);

        capability = new JsonObject();
        capability.addProperty("COORDINATE",this.user.getNfcTagData().getCoorRoom().toString());
        capabilities.add(capability);

        request.add("NFC_TAG", capabilities);
        
	    final Gson gson = new Gson();
		final String jo = gson.toJson(request);
		logger.info("Sending from Reasoner to Application: " + jo);
		//send results to Application
		this.applicationSession.getAsyncRemote().sendText(jo);

	}
	public void startSubscribeEvent() {
		// query User's Device, in order to generate Request to QueryComponent
		this.queryUserDevice();

		// start a Client
		try {
			this.startQueryClient(new URI(Constants.queryServerURI));
		} catch (final URISyntaxException e) {
			e.printStackTrace();
		}

		// UserReasoner generates Request and send to QueryComponent
		final String request = this.generateQueryRequest(this
				.getMeeting().getMeetingId());

		this.querySession.getAsyncRemote().sendText(request);
		logger.info("ReasonerSendUser(T3): USERID: " + this.user.getUserId()
				+ " " + System.currentTimeMillis());
	}
}
