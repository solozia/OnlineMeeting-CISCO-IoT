package org.openiot.lsm.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.websocket.Session;

import org.deri.cqels.data.Mapping;
import org.deri.cqels.engine.ContinuousListener;
//import org.apache.log4j.Logger;
import org.deri.cqels.engine.ExecContext;
import org.openiot.lsm.cqels.CQELSStream;
import org.openiot.lsm.reasoning.data.Constants;
import org.openiot.lsm.websocket.server.Constant;
//import org.openiot.lsm.websocket.server.queryEndpoint;
import org.openiot.lsm.websocket.server.webSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.hp.hpl.jena.sparql.core.Var;


public class CQELSManager {
	public static final String defaultCQELSGraphURI = "/";
	// "select ?s ?p ?o  where { stream <http://lsm.deri.ie/resource/1409756730575832000> [NOW] {?s ?p ?o} }";
	public final ExecContext context = new ExecContext(Constant.CQELSHOME, false);
	private Map<String, CQELSStream> activeStreamMap = new HashMap<String, CQELSStream>();
	final static Logger logger = LoggerFactory.getLogger(CQELSManager.class);
//	private String latestResult = "";
	
	
	//THU-------->
	private String meetingId;
    private Map<String, Map> meetingThresholds = new HashMap();
    private Map<String, Map> userFlags = new HashMap();
    private long userLocationTime = 0;
    //<---------
	
	private CQELSManager() {}
	public static CQELSManager instance = null;
	public static CQELSManager getCQELSManager() {
		if (instance == null) {
			instance = new CQELSManager();
		}
		return instance;
	}//--
		public void CQELSManagerStartWs() {
		logger.info("Initializing CQELS manager.");
		try {
			// // TODO start a websocket server to listen for query inputs
			// // TODO when message arrives, parse the message as a CQELS query
			(new Thread(new webSocketServer())).start();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void registerSelect(String queryStr, Session userSession, String userID, String MeetingID) {
		
		//THU---->
		meetingId = MeetingID;
		this.userLocationTime = System.currentTimeMillis();// after 5 minute, update notification of location of user
		//<------------
		
		System.out
				.println("Websocket CQELS Query Loaded before context:=============================================================================================="
						+ queryStr);
		Constant.continuousSelectMap.put(userSession,
				context.registerSelect(queryStr));
//		Constant.continuousSelectMap2.put(userSession,
//				context.registerSelect(queryStr2));
		
		// ContinuousSelect selQuery = context.registerSelect(queryStr);

		System.out
				.println("Websocket CQELS Query Loaded after context================================================================================================ :"
						+ queryStr);
		// selQuery.register(new ContinuousListener() {
		
		Constant.continuousSelectMap.get(userSession).register(
				new ContinuousListener() {
					private List<String> timestamps = new ArrayList<String>();
					private Session userSession;
					String userID="";
					public ContinuousListener init(Session userSession, String userID) {
						this.userSession = userSession;
						this.userID = userID;
						System.out.println("this.userSession: "
								+ this.userSession.getId());
						return this;
					}

					@Override
					public void update(Mapping mapping) {

						// String result = "";
						String result = "";
//						String proximityResult="";
						// String result3 = ""; //zia
						String s = new Date().toString();
						System.out.println(s);
						if (!timestamps.contains(s)) {
							timestamps.add(s);
							// System.out.println("date if");

							// Date());
						}
						for (Iterator<Var> vars = mapping.vars(); vars
								.hasNext();)
							
						// Use context.engine().decode(...) to decode the
						// encoded
						// value to RDF Node

						{
							Var var = vars.next();
							System.out.println("var");

							long value = mapping.get(var);
							result += " "
									+ context.engine().decode(value).toString();
							logger.info("result inside========" + result);
							
						
							
							// TODO Time function for Evaluation (NAOMI T6)
							//

							
							//
							// result3 += " " +
							// context.engine().decode(value).toString();

							// System.out.println("step CQELS result from manager--in loop:"+result);
						}

						// userSession.getAsyncRemote().sendText("");
//						Constant.queryResult = result; // zia

//						Double noiseValue = Double.MIN_VALUE;
//						boolean isNoise = false;
//						boolean isProximity = true;
//						if (result.contains("\"")) {
//
//							String str[] = result.split("\"");
//							noiseValue = Double.parseDouble(str[1]);
////							System.out
////									.println("CQELSManager: update(): noisevalue: "
////											+ noiseValue);
//							if (noiseValue > 65.0) {
//								isNoise = true;
//								isProximity = false;
//							} else {
//								isNoise = false;
//								isProximity = true;
//							}
//
//						}
//						System.out
//								.println("CQELSManager: update(): isNoise: "
//										+ isNoise+" isProximity: "+isProximity);
//						try {
//							if (userSession != null)
//								userSession.getAsyncRemote().sendText(
//										Json.createObjectBuilder()
//												.add("USER_ID", userID)
//												.add("NOISE", "" + isNoise)
//												.add("PROXIMITY",
//														"" + isProximity)
//												.build().toString());
////							Thread.sleep(2000);
//						} catch (Exception e) {
//							e.printStackTrace();
//							
//						}
						
						System.out
								.println("====================================================================================: "
										+ userSession.getId());
						
						logger.info("\n"
								+ "\n"
								+ "*************************************************************CQELS Result of query**************************************************************"
								+ "\n"
								+ result
								+ "\n"
								+ "\n"
								+ "*************************************************************End of CQELS Result of query********************************************************"
								+ "\n");
						
						//send result to Thu
				QueryResult queryResult = new QueryResult(meetingId, userID, result);
				// String str[] = result.split("\"");
				// String valueStr = str[1];
				// String datatype = str[3];
				// if(datatype.equals("Noise")){
				// SensorCapability senCap = new
				// SensorCapability(SensorCapabilityName.Noise,
				// new
				// DoubleSensorCapabilityValue(Double.parseDouble(valueStr)));
				// queryResult = new QueryResult(meetingId,userID, senCap);
				// }else if (datatype.equals("Light")){
				// SensorCapability senCap = new
				// SensorCapability(SensorCapabilityName.Light,
				// new
				// DoubleSensorCapabilityValue(Double.parseDouble(valueStr)));
				// queryResult = new QueryResult(meetingId,userID, senCap);
				// }else if(datatype.equals("Proximity")){
				// SensorCapability senCap = new
				// SensorCapability(SensorCapabilityName.Proximity,
				// new
				// DoubleSensorCapabilityValue(Double.parseDouble(valueStr)));
				// queryResult = new QueryResult(meetingId,userID, senCap);
				// } else if(datatype.equals("Location")){
				// String[] lonlat = valueStr.split("_");
				// double lon = Double.parseDouble(lonlat[1]);
				// double lat = Double.parseDouble(lonlat[0]);
				// SensorCapability senCap = new
				// SensorCapability(SensorCapabilityName.Location,
				// new Coordinate(lon, lat));
				// queryResult = new QueryResult(meetingId,userID, senCap);
				// }else if(datatype.equals("User_Activity")){
				// SensorCapability senCap = new
				// SensorCapability(SensorCapabilityName.User_Activity,
				// new
				// DoubleSensorCapabilityValue(Double.parseDouble(valueStr)));
				// queryResult = new QueryResult(meetingId,userID, senCap);
				// } else if(datatype.equals("User_Place")){
				// SensorCapability senCap = new
				// SensorCapability(SensorCapabilityName.User_Place,
				// new
				// DoubleSensorCapabilityValue(Double.parseDouble(valueStr)));
				// queryResult = new QueryResult(meetingId,userID, senCap);
				// }
				// Thu added 12/01/2016
				// final GsonBuilder builder = new GsonBuilder();
				// builder.registerTypeAdapter(SensorCapabilityValue.class, new
				// SensorCapabilityValueAdapter());
				// final Gson gson = builder.create();
				userSession.getAsyncRemote().sendText(new Gson().toJson(queryResult));
						
											
						/*
						 * THU - TODO
						 * result (string): "45.57507201905658"^^http://www.w3.org/2001/XMLSchema#double "Noise"
						 * Thresold? 
						 */
						//Thu Disabld 11thJan16
//						Map<String,String> thes = meetingThresholds.get(meetingId);
////						logger.info("Meeting Threshold = " + thes);
//					    Map<String, String> Flags = userFlags.get(userID);
//					    if((System.currentTimeMillis()-userLocationTime) >= 0){ // after 1 minute, update location of user
//					    	userLocationTime = System.currentTimeMillis();
//					    	Flags.put(Constants.LOCATION, Constants.ON);
//					    }
//						SensorDataFilter filter = new SensorDataFilter(meetingId, userID, result,userSession, thes,Flags);
//						(new Thread(filter)).start();
						
						
//						filterNotification( meetingId, userID, result, userSession);
						//THU disabled 11thJan16
						// logger.info("\n"
						// + "\n"
						// +
						// "*************************************************************CQELS Result of Light query***************************************************************"
						// + "\n"
						// + result3
						// + "\n"
						// + "\n"
						// +
						// "*************************************************************End of CQELS Result of Light query********************************************************"
						// + "\n");
					}
				}.init(userSession, userID));
		
		
/*		////////////////2nd Register Select
		Constant.continuousSelectMap2.get(userSession).register(
				new ContinuousListener() {
					private List<String> timestamps = new ArrayList<String>();
					private Session userSession;
					String userID="";
					public ContinuousListener init(Session userSession, String userID) {
						this.userSession = userSession;
						this.userID = userID;
						System.out.println("this.userSession: "
								+ this.userSession.getId());
						return this;
					}

					public void update(Mapping mapping) {

						// String result = "";
						String result = "";
						// String result3 = ""; //zia
						String s = new Date().toString();
						System.out.println(s);
						if (!timestamps.contains(s)) {
							timestamps.add(s);
							// System.out.println("date if");

							// Date());
						}
						for (Iterator<Var> vars = mapping.vars(); vars
								.hasNext();)
						// Use context.engine().decode(...) to decode the
						// encoded
						// value to RDF Node

						{
							Var var = vars.next();
							System.out.println("var");

							long value = mapping.get(var);
							result += " "
									+ context.engine().decode(value).toString();
							// result3 += " " +
							// context.engine().decode(value).toString();

							// System.out.println("step CQELS result from manager--in loop:"+result);
						}

						// userSession.getAsyncRemote().sendText("");
//						Constant.queryResult = result; // zia

						Double noiseValue = Double.MIN_VALUE;
						boolean isNoise = false;
						boolean isProximity = true;
						if (result.contains("\"")) {

							String str[] = result.split("\"");
							noiseValue = Double.parseDouble(str[1]);
//							System.out
//									.println("CQELSManager: update(): noisevalue: "
//											+ noiseValue);
							if (noiseValue > 65.0) {
								isNoise = true;
								isProximity = false;
							} else {
								isNoise = false;
								isProximity = true;
							}

						}
						System.out
								.println("CQELSManager: update(): isNoise: "
										+ isNoise+" isProximity: "+isProximity);
						try {
							if (userSession != null)
								userSession.getAsyncRemote().sendText(
										Json.createObjectBuilder()
												.add("USER_ID", userID)
												.add("NOISE", "" + isNoise)
												.add("PROXIMITY",
														"" + isProximity)
												.build().toString());
//							Thread.sleep(2000);
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						System.out
								.println("====================================================================================: "
										+ userSession.getId());
						
						logger.info("\n"
								+ "\n"
								+ "*************************************************************CQELS Result of Proximity query**************************************************************"
								+ "\n"
								+ proximityResult
								+ "\n"
								+ "\n"
								+ "*************************************************************End of CQELS Result of Proximity query********************************************************"
								+ "\n");

						// logger.info("\n"
						// + "\n"
						// +
						// "*************************************************************CQELS Result of Light query***************************************************************"
						// + "\n"
						// + result3
						// + "\n"
						// + "\n"
						// +
						// "*************************************************************End of CQELS Result of Light query********************************************************"
						// + "\n");
					}
				}.init(userSession, userID));
		
*/
	}

	// //

	// public List<String> loadQuery(String fileName) {
	// try {
	// File queryFile = new File(fileName);
	// FileReader fr = new FileReader(queryFile);
	// BufferedReader br = new BufferedReader(fr);
	// List<String> results = new ArrayList<String>();
	// String query = "";
	// String line;
	// while ((line = br.readLine()) != null) {
	// query += (line + "\n");
	// }
	// results.add(query);
	// logger.info("Query loaded:" + "\n" + query);
	//
	// return results;
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return null;
	// }
	// //
	public void insertTriplesToStream(String streamURI, String triples) {
		CQELSStream currentStream;
		if (this.activeStreamMap.containsKey(streamURI)) {
			currentStream = this.activeStreamMap.get(streamURI);

		} else {
			// should not happen
			currentStream = new CQELSStream(this.context, streamURI);
			
			
			//TODO Put the Time stamp for registe 
			this.activeStreamMap.put(streamURI, currentStream);
			Thread thread = new Thread(currentStream);
			thread.start();
			logger.debug("Unknown sensor stream specified, creating new stream.");

		}
		logger.info("Adding triples to: " + currentStream.getURI());
		currentStream.streamStatements(triples);
//TODO Tentative: Time function for evaluation (NAOMI T5)
		
		
	}

	public void addSensorStream(String streamURI) {
		CQELSStream currentStream = new CQELSStream(this.context, streamURI);
		this.activeStreamMap.put(streamURI, currentStream);
		Thread thread = new Thread(currentStream);
		thread.start();
	}

	public static int noOfFilesInDirectory(File directory) {
		int noOfFiles = 0;
		for (File file : directory.listFiles()) {
			if (file.isFile()) {
				noOfFiles++;
			}
			if (file.isDirectory()) {
				noOfFiles += noOfFilesInDirectory(file);
			}
		}
		return noOfFiles;
	}
	
//	//THU-------------------------------------------------->
//	/*
//	 * THU
//	 * @data: "45.57507201905658"^^http://www.w3.org/2001/XMLSchema#double "Noise"
//	 * @thes: <noise, noiselimit>, <light, lightlimit>, ...
//	 * Flag: on/off. On: capability is on. Off: capability is off 
//	 * Send notification to Reasoning when value changes
//	 * JsonObject for sending to Reasoning: "Data": "Flag". Ex: "Noise":"On"
//	 */
//	public void filterNotification(String meetingId, String userId, String result, Session userSession){
//		String data = result;
//		logger.info("Received result from Zia: " + userId + "---result == " + data);
//		Map<String,String> thes = meetingThresholds.get(meetingId);
//	    Map<String, String> Flags = userFlags.get(userId);
//	    logger.info("Thresholds of meeting " + meetingId + " = " + thes);
//	    logger.info("Flags of user " + userId + " = " + Flags);
////		Flags.put(Constants.USER_ID, userId);
////		initThresholds();
//		boolean send = false;
//		String str[] = data.split("\"");
//		double value = Double.parseDouble(str[1]);
//		String datatype = str[3];
//		logger.info("value == " +  value + "--- datatype = " + datatype);
//		
//		if(datatype.equals(Constants.Noise)){
//			Double Limit = Double.parseDouble(thes.get(Constants.NOISE));
//			if(value >= Limit && Flags.get(Constants.NOISE).equals(Constants.ON)){
//				logger.info("Change Flags because sensor value changes");
//				Flags.put(Constants.NOISE, Constants.OFF);
//				send  =true;
//			}
//				
//			else if(value < Limit && Flags.get(Constants.NOISE).equals(Constants.OFF)){
//				logger.info("Change Flags because sensor value changes");
//				Flags.put(Constants.NOISE,Constants.ON);
//				send = true;
//			}
//			
//				
//		}
//		else if(datatype.contains(Constants.Light)){
//			Double Limit = Double.parseDouble(thes.get(Constants.LIGHT));
//			if(value <= Limit && Flags.get(Constants.LIGHT).equals(Constants.ON)){
//				logger.info("Change Flags because sensor value changes");
//				Flags.put(Constants.LIGHT, Constants.OFF);
//				send  =true;
//			}
//			else if(value > Limit && Flags.get(Constants.LIGHT).equals(Constants.OFF)){
//				logger.info("Change Flags because sensor value changes");
//				Flags.put(Constants.LIGHT, Constants.ON);
//				send = true;
//			}
//		}
//		
//		
//		else if(datatype.contains(Constants.Proximity)){
//			Double Limit = Double.parseDouble(thes.get(Constants.PROXIMITY));
//			if(value <= Limit && Flags.get(Constants.PROXIMITY).equals(Constants.ON)){
//				logger.info("Change Flags because sensor value changes");
//				Flags.put(Constants.PROXIMITY, Constants.OFF);
//				send  =true;
//			}
//			else if(value > Limit && Flags.get(Constants.PROXIMITY).equals(Constants.OFF)){
//				logger.info("Change Flags because sensor value changes");
//				Flags.put(Constants.PROXIMITY, Constants.ON);
//				send = true;
//			}
//		}
//		
//		
//		else if(datatype.contains(Constants.User_Place)){
//			Double Limit = Double.parseDouble(thes.get(Constants.USER_PLACE));
//			if(value <= Limit && Flags.get(Constants.USER_PLACE).equals(Constants.ON)){
//				logger.info("Change Flags because sensor value changes");
//				Flags.put(Constants.USER_PLACE, Constants.OFF);
//				send  =true;
//			}
//			else if(value > Limit && Flags.get(Constants.USER_PLACE).equals(Constants.OFF)){
//				logger.info("Change Flags because sensor value changes");
//				Flags.put(Constants.USER_PLACE, Constants.ON);
//				send = true;
//			}
//		}
//		
//		else if(datatype.contains(Constants.User_Activity)){
//			Double Limit = Double.parseDouble(thes.get(Constants.USER_ACTIVITY));
//			if(value <= Limit && Flags.get(Constants.USER_ACTIVITY).equals(Constants.ON)){
//				logger.info("Change Flags because sensor value changes");
//				Flags.put(Constants.USER_ACTIVITY, Constants.OFF);
//				send  =true;
//			}
//			else if(value > Limit && Flags.get(Constants.USER_ACTIVITY).equals(Constants.OFF)){
//				logger.info("Change Flags because sensor value changes");
//				Flags.put(Constants.USER_ACTIVITY, Constants.ON);
//				send = true;
//			}
//		}
//		
////		else if(datatype.contains(Constants.Longitude)){
////			Flags.put(Constants.LONGITUDE, String.format("%f",value));
////			if(System.currentTimeMillis()-this.userLocationTime == (5*60*1000)){
////				send = true;
////				this.userLocationTime = 0;
////			}
////		}
////		
////		else if(datatype.contains(Constants.Latitude)){
////			Flags.put(Constants.LATITUDE, String.format("%f",value));
////			if(System.currentTimeMillis()-this.userLocationTime == (5*60*1000)){
////				send = true;
////				this.userLocationTime=0;
////			}
////		}
//		
//		if(send){
//			send = false; 
//			JsonQueryResult json = new JsonQueryResult();
//    		json.setMEETING_ID(meetingId);
//    		json.setUSER_ID(userId);
//    		json.getFEATURES().put(Constants.NOISE, Flags.get(Constants.NOISE));
//    		json.getFEATURES().put(Constants.LIGHT, Flags.get(Constants.LIGHT));
//    		json.getFEATURES().put(Constants.PROXIMITY, Flags.get(Constants.PROXIMITY));
//    		json.getFEATURES().put(Constants.USER_PLACE, Flags.get(Constants.USER_PLACE));
//    		json.getFEATURES().put(Constants.USER_ACTIVITY, Flags.get(Constants.USER_ACTIVITY));
////    		json.getFEATURES().put(Constants.LONGITUDE, Flags.get(Constants.LONGITUDE));
////    		json.getFEATURES().put(Constants.LATITUDE, Flags.get(Constants.LATITUDE));
//    		
//			Gson gson = new Gson();
//			String message = gson.toJson(json);
//			userSession.getAsyncRemote().sendText(message);
//		}
//	}
//	

	public Map<String,String> initFlags(){
		Map<String,String> Flags = new HashMap();
		Flags.put(Constants.NOISE, Constants.ON); // noise does not affect user's capabilities
		Flags.put(Constants.LIGHT, Constants.ON); // light does not affect user's capabilities
		Flags.put(Constants.PROXIMITY, Constants.ON);// proximity does not affect user's capabilities
		Flags.put(Constants.USER_PLACE, Constants.ON); //user is in "private" place
		Flags.put(Constants.USER_ACTIVITY, Constants.ON); //user's activity is free
		Flags.put(Constants.LOCATION, Constants.OFF);
		return Flags;
	}
//	
//	public void initThresholds(Map Thresholds){
//		Thresholds = new HashMap();
//		Thresholds.put(Constants.NOISE,"70");
//		Thresholds.put(Constants.LIGHT,"70");
//		Thresholds.put(Constants.PROXIMITY, "0"); // 0: close; 1: far
//		Thresholds.put(Constants.USER_PLACE,"0"); // 0: public; 1: private
//		Thresholds.put(Constants.USER_ACTIVITY,"0");//0: no_free, 1: free
//	}
////	public Map<String, String> getThresholds() {
////		return Thresholds;
////	}
////	public void setThresholds(Map<String, String> thresholds) {
////		Thresholds = thresholds;
////	}
	public Map<String, Map> getUserFlags() {
		return userFlags;
	}
	public void setUserFlags(Map<String, Map> userFlags) {
		userFlags = userFlags;
	}
	public Map<String, Map> getMeetingThresholds() {
		return meetingThresholds;
	}
	public void setMeetingThresholds(Map<String, Map> meetingThresholds) {
		this.meetingThresholds = meetingThresholds;
	}
	
	
	//<---------------------------------------------------------------------

}
