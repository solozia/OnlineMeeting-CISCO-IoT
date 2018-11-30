//package org.openiot.lsm.reasoning.test;
//
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.util.HashMap;
//import java.util.Map;
//
//import org.openiot.lsm.reasoning.data.Constants;
//import org.openiot.lsm.reasoning.websocket.AndroidClientEndPoint;
//import org.openiot.lsm.reasoning.websocket.ApplicationClientEndPoint;
//
//import com.google.gson.Gson;
//import com.google.gson.JsonArray;
//import com.google.gson.JsonObject;
//
//public class TestClient {
//
//	public static void main(String[] args) throws InterruptedException {
//		//Mock up to receive message from Application
//				try {
//		            // open websocket
//		            final AndroidClientEndPoint clientEndPoint = new AndroidClientEndPoint(new URI("ws://localhost:9000/websockets/application"));
//		            // add listener
//		            clientEndPoint.addMessageHandler(new AndroidClientEndPoint.MessageHandler() {
//		                public void handleMessage(String message) {
//		                	AndroidClientEndPoint.logger.info("Aplication received message: " + message);
//		                  
//		                       
//		                  
//		                }
//		            });
//		            
//		            
//		           Map<String, String> USERINFO = new HashMap();
//		           USERINFO.put(Constants.USER_ID, "naomi1");
//		           USERINFO.put(Constants.USER_PLACE, "public");
//		           USERINFO.put(Constants.USER_ACTIVITY, "no_free");
//		           Gson gson = new Gson();
//		           
//		           
//		    		clientEndPoint.sendMessage(gson.toJson(USERINFO));
//		  
//		            // wait 5 seconds for messages from websocket
//		            Thread.sleep(500);
//		            
//		            clientEndPoint.getLatch().await();
//		    		
//		        } catch (URISyntaxException ex) {
//		            System.err.println("URISyntaxException exception: " + ex.getMessage());
//		        }
//		
//	}
//
//}
