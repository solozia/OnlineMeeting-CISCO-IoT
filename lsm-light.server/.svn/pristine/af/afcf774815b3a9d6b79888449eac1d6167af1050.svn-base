package org.openiot.lsm.manager;

import java.util.HashMap;
import java.util.Map;

import javax.websocket.Session;

import org.openiot.lsm.reasoning.data.Constants;
import org.openiot.lsm.reasoning.data.JsonQueryResult;
import org.openiot.lsm.reasoning.data.Coordinate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class SensorDataFilter implements Runnable{
	
	final static Logger logger = LoggerFactory.getLogger(SensorDataFilter.class);
	
	private String meetingId;
	private String userId;
	private String result;
	private Session userSession;
	private Map<String,String> thes;
    private Map<String,String> Flags;
    public String THREADID;

	public SensorDataFilter(String meetingId, String userId, String result,
			Session userSession, Map<String, String> thresholds,
			Map<String, String> flags) {
		
		super();
		
		this.meetingId = meetingId;
		this.userId = userId;
		this.result = result;
		this.userSession = userSession;
		thes = thresholds;
		Flags = flags;
		
	}

	@Override
	public void run() {
//		logger.info("Start running filter of User = " + userId);
		THREADID = String.format("%s_%d", userId, Thread.currentThread().getId());
		logger.info("BeforeFilter(T6): THREADID: " + THREADID + " " + System.currentTimeMillis());
		filterObservation();
		logger.info("AfterFilter(T7): THREADID: " + THREADID + " " + System.currentTimeMillis());
		
	}

		//THU-------------------------------------------------->
		/*
		 * THU
		 * @data: "45.57507201905658"^^http://www.w3.org/2001/XMLSchema#double "Noise"
		 * @thes: <noise, noiselimit>, <light, lightlimit>, ...
		 * Flag: on/off. On: capability is on. Off: capability is off 
		 * Send notification to Reasoning when value changes
		 * JsonObject for sending to Reasoning: "Data": "Flag". Ex: "Noise":"On"
		 */
		public void filterObservation(){
			//20150507-check is userSession is closed, because query is not killed
			if(!userSession.isOpen()){
				
				return;
			}
			double lon=0, lat = 0;
			String data = result;
			logger.info("Received result from Zia: " + userId + "---result == " + data);
//			Map<String,String> thes = meetingThresholds.get(meetingId);
//		    Map<String, String> Flags = userFlags.get(userId);
		    logger.info("Thresholds of meeting " + meetingId + " = " + thes);
		    logger.info("Flags of user " + userId + " = " + Flags);
//			Flags.put(Constants.USER_ID, userId);
//			initThresholds();
		    
			boolean send = false;
			boolean sendLocation = false;
			String str[] = data.split("\"");
			String valueStr = str[1];
			String datatype = str[3];
			logger.info("value == " +  valueStr + "--- datatype = " + datatype);
			
			if(datatype.equals(Constants.Noise)){
				double value = Double.parseDouble(str[1]);
				Double Limit = Double.parseDouble(thes.get(Constants.NOISE));
				if(value >= Limit && Flags.get(Constants.NOISE).equals(Constants.ON)){
					logger.info("Change Flags because sensor value changes");
					Flags.put(Constants.NOISE, Constants.OFF);
					send  =true;
				}
					
				else if(value < Limit && Flags.get(Constants.NOISE).equals(Constants.OFF)){
					logger.info("Change Flags because sensor value changes");
					Flags.put(Constants.NOISE,Constants.ON);
					send = true;
				}
				
					
			}
			else if(datatype.contains(Constants.Light)){
				double value = Double.parseDouble(str[1]);
				Double Limit = Double.parseDouble(thes.get(Constants.LIGHT));
				if(value <= Limit && Flags.get(Constants.LIGHT).equals(Constants.ON)){
					logger.info("Change Flags because sensor value changes");
					Flags.put(Constants.LIGHT, Constants.OFF);
					send  =true;
				}
				else if(value > Limit && Flags.get(Constants.LIGHT).equals(Constants.OFF)){
					logger.info("Change Flags because sensor value changes");
					Flags.put(Constants.LIGHT, Constants.ON);
					send = true;
				}
			}
			
			
			else if(datatype.contains(Constants.Proximity)){
				double value = Double.parseDouble(str[1]);
				Double Limit = Double.parseDouble(thes.get(Constants.PROXIMITY));
				if(value <= Limit && Flags.get(Constants.PROXIMITY).equals(Constants.ON)){
					logger.info("Change Flags because sensor value changes");
					Flags.put(Constants.PROXIMITY, Constants.OFF);
					send  =true;
				}
				else if(value > Limit && Flags.get(Constants.PROXIMITY).equals(Constants.OFF)){
					logger.info("Change Flags because sensor value changes");
					Flags.put(Constants.PROXIMITY, Constants.ON);
					send = true;
				}
			}
			
			
			else if(datatype.contains(Constants.User_Place)){
				double value = Double.parseDouble(str[1]);
				double Limit = 0;// 0: public; 1: private 

//				Double Limit = Double.parseDouble(thes.get(Constants.USER_PLACE));
				if(value <= Limit && Flags.get(Constants.USER_PLACE).equals(Constants.ON)){
					logger.info("Change Flags because sensor value changes");
					Flags.put(Constants.USER_PLACE, Constants.OFF);
					send  =true;
				}
				else if(value > Limit && Flags.get(Constants.USER_PLACE).equals(Constants.OFF)){
					logger.info("Change Flags because sensor value changes");
					Flags.put(Constants.USER_PLACE, Constants.ON);
					send = true;
				}
			}
			
			else if(datatype.contains(Constants.User_Activity)){
				double value = Double.parseDouble(str[1]);
				double Limit = 0; //0: no_free, 1: free
//				Double Limit = Double.parseDouble(thes.get(Constants.USER_ACTIVITY));
				if(value <= Limit && Flags.get(Constants.USER_ACTIVITY).equals(Constants.ON)){
					logger.info("Change Flags because sensor value changes");
					Flags.put(Constants.USER_ACTIVITY, Constants.OFF);
					send  =true;
				}
				else if(value > Limit && Flags.get(Constants.USER_ACTIVITY).equals(Constants.OFF)){
					logger.info("Change Flags because sensor value changes");
					Flags.put(Constants.USER_ACTIVITY, Constants.ON);
					send = true;
				}
			}
			
			else if(datatype.contains(Constants.Location)){
				if(Flags.get(Constants.LOCATION).equals(Constants.ON)){
					//using "decode function" to change from long to Longitude & Lattitude
					String[] lonlat = valueStr.split("_");
					lon = Double.parseDouble(lonlat[1]);
					lat = Double.parseDouble(lonlat[0]);
//					double[] encodeValues = splitDoubleValuesFromString(valueStr);
//					lat = encodeValues[0];
//					lon = encodeValues[1];
					sendLocation = true;
					Flags.put(Constants.LOCATION, Constants.OFF);
				}
			}
			

			JsonQueryResult json = new JsonQueryResult();
    		json.setMEETING_ID(meetingId);
    		json.setUSER_ID(userId);
    		
    		//2015/04/22 for testing- all data will be sent to reasoner---
			//send = true;
			json.setThreadId(THREADID);
			//---->
    		
			if(send){
	    		json.setFEATURES(new HashMap());
	    		json.getFEATURES().put(Constants.NOISE, Flags.get(Constants.NOISE));
	    		json.getFEATURES().put(Constants.LIGHT, Flags.get(Constants.LIGHT));
	    		json.getFEATURES().put(Constants.PROXIMITY, Flags.get(Constants.PROXIMITY));
	    		json.getFEATURES().put(Constants.USER_PLACE, Flags.get(Constants.USER_PLACE));
	    		json.getFEATURES().put(Constants.USER_ACTIVITY, Flags.get(Constants.USER_ACTIVITY));
				
			}
			if(sendLocation){
//				logger.info("Received coordinate of userId = " + userId + "Longitude = " + lon + "--- Latitude = " + lat);
				json.setLOCATION(new Coordinate(lat, lon));
			}
			
						
			if(send || sendLocation){
				send = false;
				sendLocation = false;
				Gson gson = new Gson();
				String message = gson.toJson(json);
				userSession.getAsyncRemote().sendText(message);
					
			}
		}
		
		
//		private static double[] splitDoubleValuesFromString(String decodeValue) {
//			// TODO Auto-generated method stub
//			double[] encodes=new double[2];
//			//System.out.println(Long.parseLong("01"));
//			int fa= Integer.parseInt(decodeValue.substring(0, 1));
//			int fb= Integer.parseInt(decodeValue.substring(1, 2));
//			int a= Integer.parseInt(decodeValue.substring(2, 4));
//			int b= Integer.parseInt(decodeValue.substring(4, 6));
//			int c =Integer.parseInt(decodeValue.substring(6, 8));
//			
//			String value = decodeValue.substring(8, decodeValue.length());
//			//System.out.println(value);
//			String ss1 = value.substring(0, a+b);
//			String ss2 = value.substring(a+b, value.length());
//			double aa=Double.parseDouble(ss1.substring(0, a)+"."+ss1.substring(a,a+b));
//			double bb=Double.parseDouble(ss2.substring(0, c)+"."+ss2.substring(c,ss2.length()));
//			if(fa==1)
//				encodes[0]=(-1)*aa;
//			else
//				encodes[0]=aa;
//			if(fb==1)
//				encodes[1]=(-1)*bb;
//			else
//				encodes[1]=bb;
//			//System.out.println(aa +" "+ bb);
//			return encodes;
//		}

		
		/*
		 * THU
		 */
		
//		public Map<String, Map> getUserFlags() {
//			return userFlags;
//		}
//		public void setUserFlags(Map<String, Map> userFlags) {
//			userFlags = userFlags;
//		}
//		public Map<String, Map> getMeetingThresholds() {
//			return meetingThresholds;
//		}
//		public void setMeetingThresholds(Map<String, Map> meetingThresholds) {
//			this.meetingThresholds = meetingThresholds;
//		}
		
//		public String getMeetingId() {
//			return meetingId;
//		}
//
//		public void setMeetingId(String meetingId) {
//			this.meetingId = meetingId;
//		}
}
