//package org.openiot.lsm.reasoning.test;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Random;
//
//import javax.websocket.Session;
//
//import org.openiot.lsm.manager.CQELSManager;
//import org.openiot.lsm.reasoning.data.Constants;
//import org.openiot.lsm.reasoning.data.User;
//import org.openiot.lsm.reasoning.engine.ApplicationListener;
//import org.openiot.lsm.reasoning.engine.QueryListener;
//import org.openiot.lsm.reasoning.engine.Reasoner;
//
//import aspjavamanager.main.Main;
//
//import com.google.gson.Gson;
//import com.google.gson.JsonObject;
//
//public class testReasoner {
//
//	public static void main(String[] args) {
//		Reasoner reasoner = new Reasoner();
//		reasoner.addListener(new ApplicationListener(reasoner));
//		reasoner.addListener(new QueryListener(reasoner));
//		
//		
//		JsonObject jo = new JsonObject();
//		jo.addProperty(Constants.MEETING_ID, "1");
//		jo.addProperty(Constants.USER_ID, "kaysar2");
//		reasoner.receiveFromApplication(jo.toString(), null);
//		
//		jo = new JsonObject();
//		jo.addProperty(Constants.MEETING_ID, "1");
//		jo.addProperty(Constants.USER_ID, "naomi1");
//		reasoner.receiveFromApplication(jo.toString(), null);
//		
//		Gson gson = new Gson();
//		String result = gson.toJson(reasoner.getMeeting());
//		System.out.println("START:" + result);
//		
//		
//		JsonObject qjo = new JsonObject();
//		qjo.addProperty(Constants.USER_ID, "kaysar2");
//		qjo.addProperty(Constants.NOISE,Constants.OFF);
//		qjo.addProperty(Constants.LIGHT, Constants.OFF);
//		qjo.addProperty(Constants.PROXIMITY, Constants.OFF);
//		reasoner.receiveFromQuery(qjo.toString());
//		
//		qjo = new JsonObject();
//		qjo.addProperty(Constants.USER_ID, "naomi1");
//		qjo.addProperty(Constants.NOISE,Constants.ON);
//		qjo.addProperty(Constants.LIGHT, Constants.OFF);
//		qjo.addProperty(Constants.PROXIMITY, Constants.OFF);
//		reasoner.receiveFromQuery(qjo.toString());
//		try {
//			Thread.sleep(200);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		gson = new Gson();
//		result = gson.toJson(reasoner.getMeeting());
//		System.out.println("AFTER:" + result);
////		
//		
////		CQELSManager cqels = new CQELSManager();
////		Map<String,String> thes = new HashMap();
////		thes.put(Constants.NOISE, "50.345566");
////		thes.put(Constants.LIGHT, "40.134");
////		thes.put(Constants.PROXIMITY, "20.23423543");
////		Session userSession = null;
////		cqels.initFlags();
////		for(int i = 0; i <10; i++){
////			Random r=new Random();
////			double d = 10 + (100 - 10) * r.nextDouble();
////			String data =  d+ "^^http://www.w3.org/2001/XMLSchema#double NOISE";
////			cqels.filterNotification("kaysar2", data, thes, userSession);
////		}
////		
//
//	}
//
//}
