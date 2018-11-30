package org.openiot.lsm.reasoning.engine;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import javax.websocket.Session;

import org.openiot.lsm.reasoning.data.Constants;
import org.openiot.lsm.reasoning.data.JsonQueryResult;
import org.openiot.lsm.reasoning.data.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class QueryListener implements Listener{
	
	final static Logger logger = LoggerFactory.getLogger(QueryListener.class);
	public UserReasoner userReasoner;
	boolean locationFlag = false;
	boolean featuresFlag = false;
	boolean nfcTagFlag = false;
	public String THREADID;
	
	public QueryListener(UserReasoner reasoner) {
		super();
		this.userReasoner = reasoner;
	}

	
	
	@Override
	public void update(Event e) {
		if(parse(e.getJsonObject())){
			if(locationFlag){
				userReasoner.notifyLocation();
				locationFlag = false;
				logger.info("AfterUserReasoner(T9): THREADID: " + THREADID + " " + System.currentTimeMillis());
			}
			if(featuresFlag){
				try {
					userReasoner.notifyCapabilities();
					featuresFlag = false;
					logger.info("AfterUserReasoner(T9): THREADID: " + THREADID + " " + System.currentTimeMillis());
				} catch (UnsupportedOperationException | IOException
						| CloneNotSupportedException e1) {
					e1.printStackTrace();
				}
			}
			if(this.nfcTagFlag){
				userReasoner.notifyNFCTagData();
				locationFlag = false;
				logger.info("AfterUserReasoner(T9): THREADID: " + THREADID + " " + System.currentTimeMillis());
			}
		}
		
		
	}
	/*
	 * @jsonString: received from Query Component: {"MEETING_ID":"1","USER_ID":"kaysar2","FEATURES":{"NOISE":"OFF","PROXIMITY":"OFF","LIGHT":"OFF"}}
	 * 	 
	 */
	public boolean parse(String jsonString){
		
		JsonParser parser = new JsonParser();
		JsonObject jo = (JsonObject) parser.parse(jsonString);
		Gson gson = new GsonBuilder().create();
        JsonQueryResult result = gson.fromJson(jo, JsonQueryResult.class);
        THREADID = result.getThreadId();
        logger.info("BeforeUserReasoner(T8): THREADID: " + THREADID + " " + System.currentTimeMillis());
        
        if(result.getMEETING_ID() == null || result.getUSER_ID() == null) return false;
        if(!result.getUSER_ID().equals(userReasoner.getUser().getUserId())){
        	logger.info("Received wrong userId = %s, instead of userId = %s",result.getUSER_ID(),userReasoner.getUser().getUserId());
        	return false;
        }
        if(result.getFEATURES()!= null){
        	  this.userReasoner.getUser().setFeatures(result.getFEATURES());
        	  logger.info("Has features!!!");
        	  this.featuresFlag = true;
        }
        else
        	this.featuresFlag = false;
      
        if(result.getLOCATION()!=null){
        	this.userReasoner.getUser().setUserCoordinate(result.getLOCATION());
        	logger.info("Has location!!!");
        	this.locationFlag = true;
        }
        else
        	this.locationFlag = false;
        
        if(result.getNfcTagData()!=null){
        	this.userReasoner.getUser().setNfcTagData(result.getNfcTagData());
        	logger.info("Has NFC Data!!!");
        	this.nfcTagFlag = true;
        }
        else
        	this.nfcTagFlag = false;
        
		return true;
		
	}



	@Override
	public void update(Event e, Session s) {
		// TODO Auto-generated method stub
		
	}

}
