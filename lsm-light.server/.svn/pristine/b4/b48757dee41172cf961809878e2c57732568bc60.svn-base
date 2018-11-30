//package org.openiot.lsm.reasoning.test;
//
//import static java.lang.Math.asin;
//import static java.lang.Math.cos;
//import static java.lang.Math.sin;
//import static java.lang.Math.sqrt;
//import static java.lang.Math.toRadians;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.openiot.lsm.reasoning.data.Coordinate;
//import org.openiot.lsm.reasoning.data.Meeting;
//import org.openiot.lsm.reasoning.data.User;
//import org.openiot.lsm.reasoning.engine.MeetingReasoner;
//import org.openiot.lsm.reasoning.engine.UserReasoner;
//
//import com.hp.hpl.jena.query.Query;
//import com.hp.hpl.jena.query.QueryExecution;
//import com.hp.hpl.jena.query.QueryExecutionFactory;
//import com.hp.hpl.jena.query.QueryFactory;
//import com.hp.hpl.jena.query.QuerySolution;
//import com.hp.hpl.jena.query.ResultSet;
//import com.hp.hpl.jena.query.ResultSetFormatter;
//import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;
//
//
//
//
//
//public class DumbTest {
//
//	public final static double R_EQ = 6378137;
//	public static void main(String[] args) {
//
//		//Deri: 53.28991301691684,-9.074204787611961
//		Coordinate deri = new Coordinate(53.28991301691684,-9.074204787611961);
//		Coordinate house = new Coordinate(53.28569122956173,-9.071424007415771);
//		System.out.println(distance(deri,house ));
//
////		Reasoner reasoner = new Reasoner();
////		reasoner.queryMeetingInfo("1");
////
//
//
////		List<Coordinate> meetingRoom = new ArrayList<Coordinate>();
////		meetingRoom.add(new Coordinate(53.29080428019696, -9.077561522753967));
////		meetingRoom.add(new Coordinate(53.2900019, -9.0742254));
////		meetingRoom.add(new Coordinate(53.2900085, -9.0742129));
////		meetingRoom.add(new Coordinate(53.2899934, -9.0743745));
//////
////		Coordinate userLocation = new Coordinate(53.2899989, -9.0742248);
////		boolean result = pointInPolygon(meetingRoom, userLocation);
////		System.out.println("resultin = " + result);
////
////		System.out.println(distance(new Coordinate(53.2899978, -9.0742487), new Coordinate(53.2899989, -9.0742248)));
////		System.out.println(distance(new Coordinate(53.2900019, -9.0742254), new Coordinate(53.2899989, -9.0742248)));
////		System.out.println(distance(new Coordinate(53.2900085, -9.0742129), new Coordinate(53.2899989, -9.0742248)));
////		System.out.println(distance(new Coordinate(53.2899934, -9.0743745), new Coordinate(53.2899989, -9.0742248)));
////
////		userLocation = new Coordinate(53.2900118, -9.0742009);
////		result = pointInPolygon(meetingRoom, userLocation);
////		System.out.println("resultout = " + result);
////
////		System.out.println(distance(new Coordinate(53.2899978, -9.0742487), new Coordinate(53.2900118, -9.0742009)));
////		System.out.println(distance(new Coordinate(53.2900019, -9.0742254), new Coordinate(53.2900118, -9.0742009)));
////		System.out.println(distance(new Coordinate(53.2900085, -9.0742129), new Coordinate(53.2900118, -9.0742009)));
////		System.out.println(distance(new Coordinate(53.2899934, -9.0743745), new Coordinate(53.2900118, -9.0742009)));
////
////		userLocation = new Coordinate(53.2901266,-9.0745245);
////		result = pointInPolygon(meetingRoom, userLocation);
////		System.out.println("resultin = " + result);
////
////
////		System.out.println(distance(new Coordinate(53.2899978, -9.0742487), new Coordinate(53.2901266,-9.0745245)));
////		System.out.println(distance(new Coordinate(53.2900019, -9.0742254), new Coordinate(53.2901266,-9.0745245)));
////		System.out.println(distance(new Coordinate(53.2900085, -9.0742129), new Coordinate(53.2901266,-9.0745245)));
////		System.out.println(distance(new Coordinate(53.2899934, -9.0743745), new Coordinate(53.2901266,-9.0745245)));
////
////
////		userLocation = new Coordinate(53.2898529, -9.0742763);
////		result = pointInPolygon(meetingRoom, userLocation);
////		System.out.println("resultin = " + result);
////
////
////		System.out.println(distance(new Coordinate(53.29080428019696, -9.077561522753967), new Coordinate(53.2898529, -9.0742763)));
////		System.out.println(distance(new Coordinate(53.2900019, -9.0742254), new Coordinate(53.2898529, -9.0742763)));
////		System.out.println(distance(new Coordinate(53.2900085, -9.0742129), new Coordinate(53.2898529, -9.0742763)));
////		System.out.println(distance(new Coordinate(53.2899934, -9.0743745), new Coordinate(53.2898529, -9.0742763)));
//
//
//
////		List<Coordinate> meetingRoomCoordinates = new ArrayList<Coordinate>();
////		meetingRoomCoordinates.add(new Coordinate(53.2901076, -9.0746491));
////		meetingRoomCoordinates.add(new Coordinate(53.2899316, -9.0745647));
////		meetingRoomCoordinates.add(new Coordinate(53.2899643, -9.0745213));
////		meetingRoomCoordinates.add(new Coordinate(53.2898701, -9.0742674));
////
////		Coordinate userLocation = new Coordinate(53.2901266,-9.0745245);
////		boolean result = pointInPolygon(meetingRoomCoordinates, userLocation);
////		System.out.println("resultin = " + result);
////
////		userLocation = new Coordinate(53.2899989, -9.0742248);
////		result = pointInPolygon(meetingRoomCoordinates, userLocation);
////		System.out.println("resultin = " + result);
////
////		userLocation = new Coordinate(53.2900118, -9.0742009);
////
////		result = pointInPolygon(meetingRoomCoordinates, userLocation);
////		System.out.println("resultout = " + result);
//
////		List<Coordinate> meetingRoomCoordinates = new ArrayList<Coordinate>();
////		meetingRoomCoordinates.add(new Coordinate(3,4));
////		meetingRoomCoordinates.add(new Coordinate(7,4));
////		meetingRoomCoordinates.add(new Coordinate(7,-5));
////		meetingRoomCoordinates.add(new Coordinate(3,-5));
////
////		Coordinate userLocation = new Coordinate(3,0);
////		boolean result = pointInPolygon(meetingRoomCoordinates, userLocation);
////		System.out.println("resultin = " + result);
////
////		userLocation = new Coordinate(4,-4);
////		result = pointInPolygon(meetingRoomCoordinates, userLocation);
////		System.out.println("resultin = " + result);
////
////		userLocation = new Coordinate(0,0);
////
////		result = pointInPolygon(meetingRoomCoordinates, userLocation);
////		System.out.println("resultout = " + result);
//
//	}
//
//
//static boolean pointInPolygon(List<Coordinate> meetingRoom, Coordinate userLocation) {
//
//		int polyCorners;
//		boolean oddNodes = false;
//
//		polyCorners = meetingRoom.size();
//		double[] polyX = new double[polyCorners];
//		double[] polyY = new double[polyCorners];
//		for(int l = 0; l < polyCorners; l++){
//			polyX[l] = meetingRoom.get(l).getLat();
//			polyY[l] = meetingRoom.get(l).getLon();
//		}
//		int i, j =polyCorners-1;
//		double x = userLocation.getLat();
//		double y = userLocation.getLon();
//
//		for (i=0; i<polyCorners; i++) {
//			if ((polyY[i]< y && polyY[j]>=y
//					||   polyY[j]< y && polyY[i]>=y)
//					&&  (polyX[i]<=x || polyX[j]<=x)) {
//				oddNodes^=((double)(polyX[i]+(y-polyY[i]))/(polyY[j]-polyY[i])*(polyX[j]-polyX[i])<x);
//			}
//			j=i;
//		}
//
//		return oddNodes;
//}
//
//public static double distance(Coordinate l1, Coordinate l2) {
//	double toLat = l2.getLat();
//	double toLon = l2.getLon();
//	double fromLat = l1.getLat();
//	double fromLon = l1.getLon();
//	double sinDeltaLat = sin(toRadians(toLat - fromLat) / 2);
//	double sinDeltaLon = sin(toRadians(toLon - fromLon) / 2);
//	double normedDist = sinDeltaLat * sinDeltaLat + sinDeltaLon
//			* sinDeltaLon * cos(toRadians(fromLat)) * cos(toRadians(toLat));
//	return Math.round(R_EQ * 2 * asin(sqrt(normedDist)));
//}
//
// }
