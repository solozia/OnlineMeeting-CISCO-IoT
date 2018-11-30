package org.openiot.lsm.websocket.server;

import java.io.IOException;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;




//import org.deri.cqels.lang.cqels.CQELSParserBase;
import org.openiot.lsm.manager.CQELSManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openiot.lsm.cqels.CQELSStream;

//30/01/2015
//@ServerEndpoint(value = "/query")
@ServerEndpoint(value = "/result")

public class SubscriberServerEndpoint {
			 
	//final static Logger logger = LoggerFactory.getLogger(SubscriberServerEndpoint.class);
		
	@OnOpen
	public void onOpen(Session session) {
		System.out.println("Subscriber channel activated on ... " + session.getId());
//		CQELSManager.returnResult();
		//logger.info("Query channel activated on ... " + session.getId());
	}
	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		//logger.info(String.format("Session %s closed because of %s", session.getId(), closeReason));
		System.out.println("Session %s closed because of %s"+ session.getId()+": "+closeReason);
		//System.out.println("");
	}
	
	@OnMessage
	public void onMessage(String message, Session session) throws Exception {
	
//		if(!Constant.queryResult.equals("")){
//			session.getAsyncRemote().sendText(Constant.queryResult);//zia
			session.getAsyncRemote().sendObject(Json.createObjectBuilder().add("NOISE", "true")); //send json object
//		}
//		else
//		{
//			session.getAsyncRemote().sendText("Waiting...");
//		}
			
		
		

	}
	
}
