package org.openiot.lsm.manager;

/**
*    Copyright (c) 2011-2014, OpenIoT
*   
*    This file is part of OpenIoT.
*
*    OpenIoT is free software: you can redistribute it and/or modify
*    it under the terms of the GNU Lesser General Public License as published by
*    the Free Software Foundation, version 3 of the License.
*
*    OpenIoT is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU Lesser General Public License for more details.
*
*    You should have received a copy of the GNU Lesser General Public License
*    along with OpenIoT.  If not, see <http://www.gnu.org/licenses/>.
*
*     Contact: OpenIoT mailto: info@openiot.eu
*/
/**
 * 
 * @author Hoan Nguyen Mau Quoc
 *
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.bouncycastle.util.encoders.Hex;
import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.services.RegisteredService;
import org.jasig.cas.ticket.ExpirationPolicy;
import org.openiot.lsm.beans.Observation;
import org.openiot.lsm.beans.Place;
import org.openiot.lsm.beans.Sensor;
import org.openiot.lsm.pooling.ConnectionManager;
import org.openiot.lsm.security.oauth.LSMRegisteredServiceImpl;
import org.openiot.lsm.security.oauth.LSMServiceTicketImpl;
import org.openiot.lsm.security.oauth.LSMTicketGrantingTicketImpl;
import org.openiot.lsm.security.oauth.mgmt.Permission;
import org.openiot.lsm.security.oauth.mgmt.Role;
import org.openiot.lsm.security.oauth.mgmt.User;
import org.openiot.lsm.utils.DateUtil;
import org.openiot.lsm.utils.NumberUtil;
import org.openiot.lsm.utils.VirtuosoConstantUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.SerializationUtils;



/**
 * @author Hoan Nguyen Mau Quoc
 * 
 */
public class SensorManager {
	private String dataGraph;
	private String metaGraph;
	final static Logger logger = LoggerFactory.getLogger(SensorManager.class);
	
	public SensorManager(){
		if(ConnectionManager.getConnectionPool()==null)
			ConnectionManager.init();
	}
	
	public SensorManager(String metaGraph,String dataGraph){
		if(ConnectionManager.getConnectionPool()==null)
			ConnectionManager.init();
		this.dataGraph = dataGraph;
		this.metaGraph = metaGraph;
	}
	
	
	public String getDataGraph() {
		return dataGraph;
	}

	public void setDataGraph(String dataGraph) {
		this.dataGraph = dataGraph;
	}

	public String getMetaGraph() {
		return metaGraph;
	}

	public void setMetaGraph(String metaGraph) {
		this.metaGraph = metaGraph;
	}

	public void runSpatialIndex(){
		Connection conn = null;
		String sql = "DB.DBA.RDF_GEO_FILL()";
		try{
			logger.info("run spatial index function");
			conn = ConnectionManager.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			boolean i = ps.execute(sql);
			if(i) 
				logger.info("create spatial index successfully");		
			ConnectionManager.attemptClose(ps);
			ConnectionManager.attemptClose(conn);			
		}catch(Exception e){
//			e.printStackTrace();
			logger.error("Spatial index functino returns error",e);
			ConnectionManager.attemptClose(conn);
		}		
	}
	
	public void insertTriplesToGraph(String graphName, String triples) {
		// TODO Auto-generated method stub
		Connection conn = null;
		try{
			logger.info("insert triples into graph "+graphName);
			conn = ConnectionManager.getConnectionPool().getConnection();
			String sql = "sparql insert into graph <" + graphName + ">{" + triples +"}";
			PreparedStatement ps = conn.prepareStatement(sql);
			boolean i = ps.execute(sql);
			logger.info("Insert triples to graph "+graphName +"successfully");
			ConnectionManager.attemptClose(ps);
			ConnectionManager.attemptClose(conn);
		}catch(Exception e){
//			e.printStackTrace();
			logger.info("Fail to insert triples into graph "+graphName,e);
			ConnectionManager.attemptClose(conn);
		}
	}
	
	public void clearGraph(String graphName) {
		// TODO Auto-generated method stub
		Connection conn = null;
		try{
			logger.info("Check allowed graphs to be deleted");
			if(!VirtuosoConstantUtil.authorizedGraphs.contains(graphName)){
				logger.info("You do not have right to delete this graph "+graphName);
				return;
			}
			conn = ConnectionManager.getConnectionPool().getConnection();
			logger.info("Start clearing graph "+graphName);
			String sql = "sparql clear graph <" + graphName + ">";
			PreparedStatement ps = conn.prepareStatement(sql);
			boolean i = ps.execute(sql);
			logger.info("Remove triples of graph "+graphName);
			ConnectionManager.attemptClose(ps);
			ConnectionManager.attemptClose(conn);
		}catch(Exception e){
//			e.printStackTrace();
			logger.error("Fail to clear graph",e);
			ConnectionManager.attemptClose(conn);
		}
	}
		
	public void deleteTriples(String graphName, String triples) {
		// TODO Auto-generated method stub
		Connection conn = null;
		try{
			logger.info("Check allowed graphs to be deleted");
			//07/04/2015 By Naomi to delete triples
//			if(!VirtuosoConstantUtil.authorizedGraphs.contains(graphName)){
//				logger.info("You do not have right to delete this graph "+graphName);
//				return;
//			}
			conn = ConnectionManager.getConnectionPool().getConnection();
			String sql = "sparql delete from <" + graphName + "> {"+triples+"}";
			logger.info("Start deleting triples ");
			PreparedStatement ps = conn.prepareStatement(sql);
			boolean i = ps.execute(sql);
			System.out.println("Remove triples of graph "+graphName);
			ConnectionManager.attemptClose(ps);
			ConnectionManager.attemptClose(conn);
		}catch(Exception e){
//			e.printStackTrace();
			logger.error("Fail to delete triples",e);
			ConnectionManager.attemptClose(conn);
		}
	}
	
	public void deleteAllReadings(String graphURL,String sensorURL) {
		// TODO Auto-generated method stub
		Connection conn = null;
		logger.info("Check allowed graphs to be deleted");
		if(!VirtuosoConstantUtil.authorizedGraphs.contains(graphURL)){
			logger.info("You do not have right to delete this graph "+graphURL);
			return;
		}
		String sql = "sparql delete from <"+graphURL+"> {?s ?p ?o} "+
						"where{ "+
							"{ "+
								"?observation <http://purl.oclc.org/NET/ssnx/ssn#observedBy>  <"+sensorURL+">."+    
								"?s <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> ?observation."+
								"?s ?p ?o."+
							"}"+
						"union{ "+
							"?s <http://purl.oclc.org/NET/ssnx/ssn#observedBy>  <"+sensorURL+">."+
							"?s ?p  ?o."+
						"}"+
					"}";
		try{
			conn = ConnectionManager.getConnectionPool().getConnection();
			logger.info("execute query:"+sql);
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.executeQuery();					
			logger.info("All triples were deleted");
			ConnectionManager.attemptClose(ps);
			ConnectionManager.attemptClose(conn);
		}catch(Exception e){
			logger.error("fail to execute query "+sql,e);
			e.printStackTrace();
			ConnectionManager.attemptClose(conn);
		}
	}


	public void deleteAllReadings(String sensorURL, String dateOperator, Date fromTime, Date toTime) {
		// TODO Auto-generated method stub
		String sql = "";				
		Connection conn = null;
		logger.info("Check allowed graphs to be deleted");
		if(!VirtuosoConstantUtil.authorizedGraphs.contains(dataGraph)){
			logger.info("You do not have right to delete this graph "+dataGraph);
			return;
		}
		if(toTime!=null){
			sql = "sparql delete from <"+ dataGraph+"> {?s ?p ?o} "+
				"where{ "+
					"{ {"+
							"?observation <http://purl.oclc.org/NET/ssnx/ssn#observedBy>  <"+sensorURL+">."+  						
							"?observation <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> ?time."+
						    "filter( ?time "+dateOperator+" \""+DateUtil.date2StandardString(fromTime)+"\"^^xsd:dateTime && "+
						    "?time <= \""+DateUtil.date2StandardString(toTime)+"\"^^xsd:dateTime)."+
						 "}"+
						"?s <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> ?observation."+
						"?s ?p ?o."+
					"}"+
				"union{ "+
					"?s <http://purl.oclc.org/NET/ssnx/ssn#observedBy>  <"+sensorURL+">."+
					"?s ?p  ?o."+
				"}"+
			"}";
		}else{
			sql = "sparql delete from <"+ dataGraph+"> {?s ?p ?o} "+
					"where{ "+
						"{ {"+
								"?observation <http://purl.oclc.org/NET/ssnx/ssn#observedBy>  <"+sensorURL+">."+  						
								"?observation <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> ?time."+
							    "filter( ?time "+dateOperator+" \""+DateUtil.date2StandardString(fromTime)+"\"^^xsd:dateTime)."+
							 "}"+
							"?s <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> ?observation."+
							"?s ?p ?o."+
						"}"+
					"union{ "+
						"?s <http://purl.oclc.org/NET/ssnx/ssn#observedBy>  <"+sensorURL+">."+
						"?s ?p  ?o."+
					"}"+
				"}";					
		}
		try{
			conn = ConnectionManager.getConnectionPool().getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			logger.info("execute query:"+sql);
			ps.executeQuery();	
			logger.info("All triples were deleted");
			ConnectionManager.attemptClose(ps);
			ConnectionManager.attemptClose(conn);
		}catch(Exception e){
//			e.printStackTrace();
			logger.info("fail to execute query:"+sql,e);
			ConnectionManager.attemptClose(conn);
		}
	}
	
	public void updateGraph(String graphURL, String updatePatterns, String deletePatterns) {
		// TODO Auto-generated method stub
		
	}
	
	public ArrayList<List> getAllSensorsHasLatLongWithSpatialCriteria(String spatialOperator,
			double lng,double lat,double distance){		
		ArrayList<List> lst = new ArrayList<List>(3);
		List<String> l1= new ArrayList<String>();
		List<String> l2 = new ArrayList<String>();
		List<Double> l3 = new ArrayList<Double>();
		Connection conn = null;
		String sql = "sparql select distinct(?sensor) ?city ?country <bif:st_distance>(?geo,<bif:st_point>("+
				lng+","+lat+")) as ?distance "+		
					" from <"+ metaGraph +"> " + 			
					"where {"+			
					"?sensor <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Sensor>."+												
					"?sensor <"+ VirtuosoConstantUtil.sensorHasPlacePrefix+"> ?place. "+
					"?place <http://lsm.deri.ie/ont/lsm.owl#is_in_city> ?cityId."+
					"?cityId <http://www.w3.org/2000/01/rdf-schema#label> ?city."+
					"?place <http://linkedgeodata.org/property/is_in_country> ?counId."+
					"?counId <http://www.w3.org/2000/01/rdf-schema#label> ?country."+
					"?place geo:geometry ?geo."+
					"filter (<bif:"+ spatialOperator +">(?geo,<bif:st_point>("+
							lng+","+lat+"),"+distance+"))." +
					"} order by ?distance";		
		try{
			if(conn.isClosed())
				conn = ConnectionManager.getConnectionPool().getConnection();			
			PreparedStatement ps = conn.prepareStatement(sql);
			logger.info("execute query:"+sql);
			ResultSet rs = ps.executeQuery();		
			while(rs.next()){
				l1.add(rs.getString(1));
				l2.add(rs.getString(2)+", "+rs.getString(3));
				l3.add(rs.getDouble(4));
			}
			lst.add(l1);
			lst.add(l2);
			lst.add(l3);
			ConnectionManager.attemptClose(rs);
			ConnectionManager.attemptClose(ps);
			ConnectionManager.attemptClose(conn);
		}catch(Exception e){
			logger.info("fail to execute query:"+sql,e);
//			e.printStackTrace();
			ConnectionManager.attemptClose(conn);
		}
		return lst;
	}
			
		
	//**********************sensor table***************************/
	public Sensor getSpecifiedSensorWithSource(String source){
		PlaceManager placeManager = new PlaceManager(metaGraph,dataGraph);
		Connection conn = null;
		Sensor sensor = null;
		String sql = "sparql select ?sensor ?sensorType ?sourceType ?place "+
				" from <"+ metaGraph +"> \n" +
					"where{ "+
					   "?sensor <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Sensor>."+
					   "?sensor <http://purl.org/net/provenance/ns#PerformedBy> <"+source+">."+
					   "?sensor <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> ?place."+
					   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?typeId."+
					   "?typeId <http://www.w3.org/2000/01/rdf-schema#label> ?sensorType."+
					   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSourceType> ?sourceType."+
					"}";			 
			try{
				conn = ConnectionManager.getConnectionPool().getConnection();				
				Statement st = conn.createStatement();
				if(st.execute(sql)){
					ResultSet rs = st.getResultSet();
					while(rs.next()){
						sensor = new Sensor();
						sensor.setId(rs.getString("sensor"));
						sensor.setSensorType(rs.getString("sensorType"));
						sensor.setSource(source);
						sensor.setSourceType(rs.getString("sourceType"));
						Place place = placeManager.getPlaceWithPlaceId(rs.getString("place"));
						sensor.setPlace(place);
						sensor.setProperties(getObservesListOfSensor(rs.getString("sensor")));
					}
					ConnectionManager.attemptClose(rs);				
				}
				ConnectionManager.attemptClose(st);
				ConnectionManager.attemptClose(conn);
			}catch(Exception e){
				e.printStackTrace();
				ConnectionManager.attemptClose(conn);
			}		
			return sensor;
	}
	
	@SuppressWarnings("unchecked")
	public Sensor getSpecifiedSensorWithPlaceId(String placeId){
		Sensor sensor = null;
		Connection conn = null;
		PlaceManager placeManager = new PlaceManager(metaGraph,dataGraph);		
		String sql = "sparql select ?sensor ?sensorType ?source ?sourceType ?place "+
				" from <"+ metaGraph +"> \n" +
				"where{ "+
				   "?sensor <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Sensor>."+
				   "?sensor <http://purl.org/net/provenance/ns#PerformedBy> ?source."+
				   "?sensor <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> <"+placeId+">."+
				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?typeId."+
				   "?typeId <http://www.w3.org/2000/01/rdf-schema#label> ?sensorType."+
				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSourceType> ?sourceType."+	  
				"}";			 
		try{
			conn = ConnectionManager.getConnectionPool().getConnection();			
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					sensor = new Sensor();					
					sensor.setId(rs.getString("sensor"));
					sensor.setSource(rs.getString("source"));
					sensor.setSensorType(rs.getString("sensorType"));
					sensor.setSourceType(rs.getString("sourceType"));
					Place place = placeManager.getPlaceWithPlaceId(placeId);
					sensor.setPlace(place);								
					sensor.setProperties(getObservesListOfSensor(rs.getString("sensor")));
				}
				ConnectionManager.attemptClose(rs);				
			}
			ConnectionManager.attemptClose(st);
			ConnectionManager.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionManager.attemptClose(conn);
		}
		return sensor;
	}
	

	public Sensor getSpecifiedSensorWithSensorId(String id){		
		Sensor sensor = null;
		Connection conn = null;
		String sql = "sparql select ?name ?sensorType ?source ?sourceType ?place  "+
				" from <"+ metaGraph +"> \n" +
				"where{ "+
				   "<"+id+"> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Sensor>."+
				   "<"+id+"> <http://purl.org/net/provenance/ns#PerformedBy> ?source."+
				   "<"+id+"> <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> ?place."+
				   "<"+id+"> <http://www.w3.org/2000/01/rdf-schema#label> ?name."+
				   "<"+id+"> <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?typeId."+
				   "?typeId <http://www.w3.org/2000/01/rdf-schema#label> ?sensorType."+
				   "<"+id+"> <http://lsm.deri.ie/ont/lsm.owl#hasSourceType> ?sourceType."+	  
				"}";			 
		try{
			conn = ConnectionManager.getConnectionPool().getConnection();			
			PlaceManager placeManager = new PlaceManager(metaGraph,dataGraph);		
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					sensor = new Sensor();					
					sensor.setId(id);
					sensor.setSource(rs.getString("source"));
					sensor.setSensorType(rs.getString("sensorType"));
					sensor.setSourceType(rs.getString("sourceType"));
					sensor.setName(rs.getString("name"));
					Place place = placeManager.getPlaceWithPlaceId(rs.getString("place"));
					sensor.setPlace(place);
					sensor.setProperties(getObservesListOfSensor(id));
				}
				ConnectionManager.attemptClose(rs);				
			}
			ConnectionManager.attemptClose(st);
			ConnectionManager.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionManager.attemptClose(conn);
		}
		return sensor;
	}
	

	public Sensor getSpecifiedSensorWithLatLng(double lat, double lng) {		
		Sensor sensor = null;
		Connection conn = null;
//		String sql = "sparql select ?sensor ?source ?sourceType ?place "+
		String sql = "sparql select ?sensor ?sensorType ?source ?sourceType ?place "+
				" from <"+ metaGraph +"> \n" +
				"where{ "+
				   "?sensor <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Sensor>."+
				   "?sensor <http://purl.org/net/provenance/ns#PerformedBy> ?source."+
				   "?sensor <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> ?place."+
				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?typeId."+
				   "?typeId <http://www.w3.org/2000/01/rdf-schema#label> ?sensorType."+
				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSourceType> ?sourceType."+				  
				   "?place <http://www.w3.org/2003/01/geo/wgs84_pos#lat> "+lat+";" +
				   "<http://www.w3.org/2003/01/geo/wgs84_pos#long> "+lng+"." +
				"}";			 
		try{
			conn = ConnectionManager.getConnectionPool().getConnection();
			PlaceManager placeManager = new PlaceManager(metaGraph,dataGraph);
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					sensor = new Sensor();
					sensor.setId(rs.getString("sensor"));
					sensor.setSource(rs.getString("source"));
					sensor.setSensorType(rs.getString("sensorType"));
					sensor.setSourceType(rs.getString("sourceType"));
					Place place = placeManager.getPlaceWithPlaceId(rs.getString("place"));
					sensor.setPlace(place);
					sensor.setProperties(getObservesListOfSensor(rs.getString("sensor")));
				}
				ConnectionManager.attemptClose(rs);				
			}
			ConnectionManager.attemptClose(st);
			ConnectionManager.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionManager.attemptClose(conn);
		}
		return sensor;
	}
	
	public HashMap<String,String> getObservesListOfSensor(String sensorId){
		HashMap<String, String> lstPro = new HashMap<>();
		Connection conn = null;
		String sql = "sparql select ?obs ?type"+
				" from <"+ metaGraph +"> \n" +
				"where{ "+
				   "<"+sensorId+"> <http://purl.oclc.org/NET/ssnx/ssn#observes> ?obs."+
				   "?obs rdf:type ?type." +				   
				"}";			 
		try{
			conn = ConnectionManager.getConnectionPool().getConnection();			
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					lstPro.put(rs.getString("type"), rs.getString("obs"));					
				}
				ConnectionManager.attemptClose(rs);				
			}
			ConnectionManager.attemptClose(st);
			ConnectionManager.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionManager.attemptClose(conn);
		}
		return lstPro;		
	}
	
	
	//**********************observation table***************************/
	public Observation getNewestObservationForOneSensor(String sensorId) {
		Observation observation = null;
		Connection conn = null;
		String sql = "sparql select ?obs ?time ?foi"+
				" from <"+ dataGraph +"> \n" +
				"where{ "+
				   "?obs <http://purl.oclc.org/NET/ssnx/ssn#observedBy> <"+sensorId+">."+
				   "?obs <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> ?time." +
				   "?obs <http://purl.oclc.org/NET/ssnx/ssn#featureOfInterest> ?foi."+
				"}order by desc(?time) limit 1";			 
		try{
			conn = ConnectionManager.getConnectionPool().getConnection();			
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					observation = new Observation();
					observation.setId(rs.getString(1));
					observation.setSensor(sensorId);
					observation.setTimes(DateUtil.string2Date(rs.getString(2),"yyyy-MM-dd HH:mm:ss.SSS"));		
					observation.setFeatureOfInterest(rs.getString(3));
				}
				ConnectionManager.attemptClose(rs);				
			}
			ConnectionManager.attemptClose(st);
			ConnectionManager.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionManager.attemptClose(conn);
		}
		return  observation;
	}
	
	public List<String> getObservationsWithTimeCriteria(String sensorId,
			String dateOperator, Date fromTime, Date toTime) {
		// TODO Auto-generated method stub				
		String sql;
		Connection conn = null;
		if(toTime!=null){
			sql= "sparql select ?obs"+
					" from <"+ dataGraph+"> "+
					"where{ "+
					   "?obs <http://purl.oclc.org/NET/ssnx/ssn#observedBy>  <"+sensorId+">."+					   
					   "?obs <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> ?time."+		
					   "filter (?time "+dateOperator+" \""+DateUtil.date2StandardString(fromTime)+"\"^^xsd:dateTime"+" && ?time <= \""+DateUtil.date2StandardString(toTime)+"\"^^xsd:dateTime)" +
					"}";
		}else{
			sql= "sparql select ?obs"+
					" from <"+ dataGraph+"> "+
					"where{ "+
					   "?obs <http://purl.oclc.org/NET/ssnx/ssn#observedBy>  <"+sensorId+">."+					   
					   "?obs <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> ?time."+		
					   "filter (?time "+dateOperator+" \""+DateUtil.date2StandardString(fromTime)+"\"^^xsd:dateTime)" +
					"}";
		}
		List<String> observations = new ArrayList<String>();		
		try{
			conn = ConnectionManager.getConnectionPool().getConnection();
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					observations.add(rs.getString(1));
				}
				ConnectionManager.attemptClose(rs);				
			}
			ConnectionManager.attemptClose(st);
			ConnectionManager.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionManager.attemptClose(conn);
		}
		return observations;
	}
	
	public List<Observation> getObservationsForOneSensor(String sensorId) {
		// TODO Auto-generated method stub		
		List<Observation> observations = new ArrayList<Observation>();
		Connection conn = null;
		String sql = "sparql select ?obs ?time ?foi"+
				" from <"+ dataGraph +"> \n" +
				"where{ "+
				   "?obs <http://purl.oclc.org/NET/ssnx/ssn#observedBy> <"+sensorId+">."+
				   "?obs <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> ?time." +
				   "?obs <http://purl.oclc.org/NET/ssnx/ssn#featureOfInterest> ?foi."+
				"}order by desc(?time)";			 
		try{
			conn = ConnectionManager.getConnectionPool().getConnection();			
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					Observation observation = new Observation();
					observation.setId(rs.getString(1));
					observation.setSensor(sensorId);
					observation.setTimes(DateUtil.string2Date(rs.getString(2),"yyyy-MM-dd HH:mm:ss.SSS"));		
					observation.setFeatureOfInterest(rs.getString(3));
					observations.add(observation);
				}
				ConnectionManager.attemptClose(rs);				
			}
			ConnectionManager.attemptClose(st);
			ConnectionManager.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionManager.attemptClose(conn);
		}
		return  observations;
	}
	
	public List<String> getObservationsForNonSpatialCriteria(
			String sensorId,String timeOper, String dateTime, String readingType,
			String oper, String value) {		
		// TODO Auto-generated method stub
		Date date = DateUtil.standardString2Date(dateTime);
		Connection conn = null;
		String sql;
		if(timeOper.equals("latest")){
			if(value!=null){
				sql= "sparql select ?obs"+
						" from <"+ dataGraph +"> \n" +
					"where{ "+
					   "?obs <http://purl.oclc.org/NET/ssnx/ssn#observedBy>  <"+sensorId+">."+
					   "?sign <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> ?obs."+
					   "?sign rdf:type ?type." +
					   "?sign <http://lsm.deri.ie/ont/lsm.owl#value> ?value."+
					   "?sign <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> ?time."+					  
					   " filter regex(?type,'"+readingType+"','i')"+
					   " filter (?value" + oper + value +")" +
					"}order by desc(?time) limit 1";
			}else{
				sql= "sparql select ?obs"+
						" from <"+ dataGraph +"> \n" +
						"where{ "+
						   "?obs <http://purl.oclc.org/NET/ssnx/ssn#observedBy>  <"+sensorId+">."+						   
						   "?obs <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> ?time."+	
						"}order by desc(?time) limit 1";
			}
		}else{
			if(value!=null){
				sql= "sparql select ?obs"+
						" from <"+ dataGraph +"> \n" +
						"where{ "+
						   "?obs <http://purl.oclc.org/NET/ssnx/ssn#observedBy>  <"+sensorId+">."+
						   "?sign <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> ?obs."+
						   "?sign rdf:type ?type." +
						   "?sign <http://lsm.deri.ie/ont/lsm.owl#value> ?value."+
						   "?sign <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> ?time."+					  
						   " filter regex(?type,'"+readingType+"','i')"+
						   " filter (?value" + oper + value +" && ?time "+timeOper+" \""+dateTime+"\"^^xsd:dateTime)" +
						"}";
			}else{
				sql= "sparql select ?obs"+
						" from <"+ dataGraph +"> \n" +
						"where{ "+
						   "?obs <http://purl.oclc.org/NET/ssnx/ssn#observedBy>  <"+sensorId+">."+
						   "?sign <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> ?obs."+
						   "?sign rdf:type ?type." +
						   "?sign <http://lsm.deri.ie/ont/lsm.owl#value> ?value."+
						   "?sign <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> ?time."+					  
						   " filter regex(?type,'"+readingType+"','i')"+
						   " filter (?time "+timeOper+" \""+dateTime+"\"^^xsd:dateTime)" +
						"}";
				
			}
		}
		List<String> observations = new ArrayList<String>();		
		try{
			conn = ConnectionManager.getConnectionPool().getConnection();			
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					observations.add(rs.getString(1));
				}
				ConnectionManager.attemptClose(rs);				
			}
			ConnectionManager.attemptClose(st);
			ConnectionManager.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionManager.attemptClose(conn);
		}
		return observations;
	}
	
	public List<ArrayList> getReadingDataOfObservation(String observationId) {		
		// TODO Auto-generated method stub
		List<ArrayList> list = new ArrayList<ArrayList>();
		Connection conn = null;
		String sql = "sparql select ?type ?value ?uni ?name "+
				" from <"+ dataGraph+"> "+
				"where{ "+
				   "?sign <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> <"+observationId+">."+
				   "?sign rdf:type ?type." +
				   "?sign <http://lsm.deri.ie/ont/lsm.owl#value> ?value."+
				   "OPTIONAL{?sign <http://lsm.deri.ie/ont/lsm.owl#unit> ?unit.}" +				  
				   "OPTIONAL{?sign <http://www.w3.org/2000/01/rdf-schema#label> ?name.}"+
				"}";			 
		try{
			conn = ConnectionManager.getConnectionPool().getConnection();	
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					ArrayList<String> arr = new ArrayList<String>();
					arr.add(rs.getString("type"));
					arr.add(rs.getString("value"));
					arr.add(rs.getString("uni"));
					arr.add(rs.getString("name"));
					list.add(arr);
				}				
				ConnectionManager.attemptClose(rs);				
			}
			ConnectionManager.attemptClose(st);
			ConnectionManager.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionManager.attemptClose(conn);
		}
		return list;
	}


	public Sensor getSpecifiedSensorWithObservationId(String obsId) {
		// TODO Auto-generated method stub 
		Sensor sensor = null;
		Connection conn = null;
		PlaceManager placeManager = new PlaceManager(metaGraph,dataGraph);
		String sql = "sparql select ?sensor ?source ?sourceType ?sensorType ?place "+
				" from <"+ metaGraph +"> " +
				"where{ "+
				   "{select ?sensor from <"+dataGraph +"> " +
				   " where{ <"+obsId+"> <http://purl.oclc.org/NET/ssnx/ssn#observedBy> ?sensor.}"+
				   "}"+
				   "?sensor <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Sensor>."+
				   "?sensor <http://purl.org/net/provenance/ns#PerformedBy> ?source."+
				   "?sensor <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> ?place."+
				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?typeId."+
				   "?typeId <http://www.w3.org/2000/01/rdf-schema#label> ?sensorType."+
				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSourceType> ?sourceType."+		  
				"}";			 
		try{
			conn = ConnectionManager.getConnectionPool().getConnection();
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					sensor = new Sensor();
					sensor.setId(rs.getString("sensor"));
					sensor.setSource(rs.getString("source"));
					sensor.setSensorType(rs.getString("sensorType"));
					sensor.setSourceType(rs.getString("sourceType"));
					Place place = placeManager.getPlaceWithPlaceId(rs.getString("place"));
					sensor.setPlace(place);
				}
				ConnectionManager.attemptClose(rs);				
			}
			ConnectionManager.attemptClose(st);
			ConnectionManager.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionManager.attemptClose(conn);
		}
		return sensor;
	}
	
	

	public ArrayList getSensorHistoricalData(String sensorURL, Date fromTime) {
		// TODO Auto-generated method stub
		Connection conn = null;
		LinkedHashMap<String, String> reading = new LinkedHashMap<>();
		ArrayList arr = new ArrayList<>();
		String query = "sparql select ?s ?type ?name ?value ?time "+
				" from <"+ dataGraph +"> " +
					"where{ "+
					  "{ "+
					   "select ?observation where "+
					     "{ "+
					       "?observation <http://purl.oclc.org/NET/ssnx/ssn#observedBy>  <"+sensorURL+">. "+
					       "?observation <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> ?time. "+
					    "filter( ?time >\""+DateUtil.date2StandardString(fromTime)+"\"^^xsd:dateTime).} "+
					  "} "+ 
					  "?s <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> ?observation. "+
					  "?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?type. "+
					  "?s <http://lsm.deri.ie/ont/lsm.owl#value> ?value."+
					  "?s <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> ?time. "+
					  "OPTIONAL{?s <http://www.w3.org/2000/01/rdf-schema#label> ?name.}"+
					"}";			 
		try{
			conn = ConnectionManager.getConnectionPool().getConnection();			
			Statement st = conn.createStatement();
			String sign = "";
			if(st.execute(query)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){				
					reading = new LinkedHashMap<>();					
					if(rs.getString("name")==null)
						sign = rs.getString("type");
					else sign = rs.getString("name");
					reading.put("property",sign.substring(sign.lastIndexOf("#")+1));
					reading.put("value", rs.getString("value"));
					reading.put("time", rs.getString("time"));
					arr.add(reading);
				}			
				ConnectionManager.attemptClose(rs);				
			}
			ConnectionManager.attemptClose(st);
			ConnectionManager.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();			
			ConnectionManager.attemptClose(conn);
		}
//		return json;
		return arr;
	}

	public void sensorDelete(String graphURL,String sensorURL) {
		// TODO Auto-generated method stub
		Connection conn = null;
		String sql = "sparql DELETE from <"+ graphURL +">{" +
				"<"+sensorURL + "> ?p ?o.}" + 			
				"where{<"+sensorURL + "> ?p ?o." +							     	
				"}";				  
							 
		try{
			conn = ConnectionManager.getConnectionPool().getConnection();		
			Statement st = conn.createStatement();
			st.execute(sql);
			ConnectionManager.attemptClose(st);
			ConnectionManager.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionManager.attemptClose(conn);
		}
		
	}

	public String getSensorTypeId(String sensorType) {
		// TODO Auto-generated method stub
		String typeId = "";
		Connection conn = null;
		String sql = "sparql select ?type "+
				" from <"+ metaGraph +"> \n" +
				"where{ "+
				  "?type <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://lsm.deri.ie/ont/lsm.owl#SensorType>."+
				  "?type <http://www.w3.org/2000/01/rdf-schema#label> \""+sensorType+"\"."+				 
				"}";			 
		try{
			conn = ConnectionManager.getConnectionPool().getConnection();							
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					typeId = rs.getString("type");
				}
				ConnectionManager.attemptClose(rs);				
			}
			ConnectionManager.attemptClose(st);
			ConnectionManager.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionManager.attemptClose(conn);
		}
		return typeId;
	}

	/**
	 * ***************************************************************************************************************
	 * security and privacy functionalities
	 */
	public Role getRoleById(String roleId){
		Connection conn = null;
		Role role = null;
		String roleURL = VirtuosoConstantUtil.RolePrefix+roleId;
		if(roleId.contains(VirtuosoConstantUtil.RolePrefix)){
			roleURL = roleId;
			roleId = roleId.substring(roleId.lastIndexOf("/")+1);
		}		
		String sql = "sparql select ?des"+
				" from <"+ metaGraph +"> \n" +
				"where{ "+
				   "<"+roleURL+"> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://openiot.eu/ontology/ns/ClientRole>."+				   
				   "OPTIONAL{<"+roleURL+"> <http://www.w3.org/2000/01/rdf-schema#comment> ?des.}"+
				"}";			 
		try{
			conn = ConnectionManager.getConnectionPool().getConnection();			
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					role = new Role();
					role.setName(roleId);
					role.setDescription(rs.getString("des"));
					HashMap<Long, Set<Permission>> permissionsPerService = getPermissionsPerServiceForRole(roleURL);
					role.setPermissionsPerService(permissionsPerService);
				}
				ConnectionManager.attemptClose(rs);				
			}
			ConnectionManager.attemptClose(st);
			ConnectionManager.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionManager.attemptClose(conn);
		}
		return  role;		
	}
	
	public boolean deleteRoleById(String roleId){
		Connection conn = null;
		String roleURL = VirtuosoConstantUtil.RolePrefix+roleId;
		if(roleId.contains(VirtuosoConstantUtil.RolePrefix)){
			roleURL = roleId;
			roleId = roleId.substring(roleId.lastIndexOf("/")+1);
		}		
		String sql = "sparql delete from <"+ metaGraph +"> {" +
				   "<"+roleURL+"> ?p ?o.} \n"+
					"where {\n"+
					   "<"+roleURL+"> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://openiot.eu/ontology/ns/ClientRole>."+				   
					   "<"+roleURL+"> ?p ?o."+
					"}";				 
		try{
			//delete permission list for Role
			deletePermissionsPerServiceForRole(roleURL);
			
			//delete Role
			conn = ConnectionManager.getConnectionPool().getConnection();			
			Statement st = conn.createStatement();
			st.execute(sql);
			ConnectionManager.attemptClose(st);
			ConnectionManager.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionManager.attemptClose(conn);
			return false;
		}	
		return true;
	}
	
	
	public HashMap<Long, Set<Permission>> getPermissionsPerServiceForRole(String roleURL){
		Connection conn = null;
		HashMap<Long, Set<Permission>> permissionsPerService = null;
		String sql = "sparql select ?right ?serviceId ?per"+
				" from <"+ metaGraph +"> \n" +
				"where{ "+
				   "?right <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://openiot.eu/ontology/ns/RoleRight>."+				   
				   "?right <http://openiot.eu/ontology/ns/forRole> <"+roleURL+">."+
				   "?right <http://openiot.eu/ontology/ns/forService> ?serviceId."+
				   "?right <http://openiot.eu/ontology/ns/forPermission> ?per."+
				"}";			 
		try{
			conn = ConnectionManager.getConnectionPool().getConnection();			
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				permissionsPerService = new HashMap<Long, Set<Permission>>();
				while(rs.next()){
					String serviceURL = rs.getString("serviceId");
					long serviceId = Long.parseLong(serviceURL.substring(serviceURL.lastIndexOf("/")+1));					
					Set<Permission> permissions = permissionsPerService.get(serviceId);
					if (permissions == null) {
						permissions = new HashSet<Permission>();
						permissionsPerService.put(serviceId, permissions);
					}
					Permission permission = getPermissionById(rs.getString("per"));
					if (!permissions.contains(permission)) {
						permissions.add(permission);							
					}
				}
				ConnectionManager.attemptClose(rs);				
			}
			ConnectionManager.attemptClose(st);
			ConnectionManager.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionManager.attemptClose(conn);
			return null;
		}
		return  permissionsPerService;		
	}
	
	public boolean deletePermissionsPerServiceForRole(String roleURL){
		Connection conn = null;
		String sql = "sparql delete from <"+ metaGraph +"> {?right ?p ?o.} " +
				   " where {\n"+
				   "?right <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://openiot.eu/ontology/ns/RoleRight>."+				   
				   "?right <http://openiot.eu/ontology/ns/forRole> <"+roleURL+">."+
				   "?right ?p ?o."+
				"}";			 
		try{
			conn = ConnectionManager.getConnectionPool().getConnection();			
			Statement st = conn.createStatement();
			st.execute(sql);
			ConnectionManager.attemptClose(st);
			ConnectionManager.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionManager.attemptClose(conn);
			return false;
		}	
		return true;
	}
	
	public Permission getPermissionById(String perId){
		Connection conn = null;
		Permission per = null;
		String perPrefix = "http://lsm.deri.ie/resource/";	
		String perURL = perPrefix + perId;
		if(perId.contains(perPrefix)){
			perURL = perId;
			perId = perId.substring(perId.lastIndexOf("/")+1);
		}
		String sql = "sparql select ?des"+
				" from <"+ metaGraph +"> \n" +
				"where{ "+
				   "<"+perURL+"> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://openiot.eu/ontology/ns/ClientPermission>."+				   
				   "OPTIONAL{<"+perURL+"> <http://www.w3.org/2000/01/rdf-schema#comment> ?des.}"+
				"}";			 
		try{
			conn = ConnectionManager.getConnectionPool().getConnection();			
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					per = new Permission();
					per.setName(perId);
					per.setDescription(rs.getString("des"));		
				}
				ConnectionManager.attemptClose(rs);				
			}
			ConnectionManager.attemptClose(st);
			ConnectionManager.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionManager.attemptClose(conn);
		}
		return  per;		
	}
	
	public boolean deletePermissionById(String perId){
		Connection conn = null;
		String perPrefix = "http://lsm.deri.ie/resource/";	
		String perURL = perPrefix + perId;
		if(perId.contains(perPrefix)){
			perURL = perId;
			perId = perId.substring(perId.lastIndexOf("/")+1);
		}
		String sql = "sparql delete from <"+metaGraph+"> {"+
				   "<"+perURL+"> ?p ?o.} \n"+
				"where {\n"+
				   "<"+perURL+"> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://openiot.eu/ontology/ns/ClientPermission>."+				   
				   "<"+perURL+"> ?p ?o."+
				"}";			 
		try{
			conn = ConnectionManager.getConnectionPool().getConnection();			
			Statement st = conn.createStatement();
			st.execute(sql);					
			ConnectionManager.attemptClose(st);
			ConnectionManager.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionManager.attemptClose(conn);
			return false;
		}	
		return true;
	}
	
	public org.openiot.lsm.security.oauth.mgmt.User getOAuthUserById(String userId){
		Connection conn = null;
		org.openiot.lsm.security.oauth.mgmt.User user = null;
		String userURL = VirtuosoConstantUtil.OAuthUserPrefix+userId;
		if(userId.contains(VirtuosoConstantUtil.OAuthUserPrefix)){
			userURL = userId;
			userId = userId.substring(userId.lastIndexOf("/")+1);
		}
		String sql = "sparql select ?nick ?mbox ?pass ?role"+
				" from <"+ metaGraph +"> \n" +
				"where{ "+
				   "<"+userURL+"> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://openiot.eu/ontology/ns/User>."+				   
				   "OPTIONAL{<"+userURL+"> <http://xmlns.com/foaf/0.1/nick> ?nick.}"+
				   "OPTIONAL{<"+userURL+"> <http://xmlns.com/foaf/0.1/mbox> ?mbox.}"+
				   "<"+userURL+"> <http://openiot.eu/ontology/ns/password> ?pass."+
				   "<"+userURL+"> <http://openiot.eu/ontology/ns/role> ?role."+
				"}";			 
		try{
			conn = ConnectionManager.getConnectionPool().getConnection();			
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				user = new org.openiot.lsm.security.oauth.mgmt.User();
				user.setUsername(userId);				
				while(rs.next()){
					user.setEmail(rs.getString("mbox"));
					user.setPassword(rs.getString("pass"));
					user.setName(rs.getString("nick"));
					List<Role> roles = user.getRoles();
					if(roles==null){
						roles = new ArrayList<Role>();
						user.setRoles(roles);
					}
					Role role = getRoleById(rs.getString("role"));
					if(!roles.contains(role)){
						roles.add(role);
					}
				}
				ConnectionManager.attemptClose(rs);				
			}
			ConnectionManager.attemptClose(st);
			ConnectionManager.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionManager.attemptClose(conn);
			return null;
		}
		return  user;		
	}
	
	public boolean deleteOAuthUserById(String userId){
		Connection conn = null;
		String userURL = VirtuosoConstantUtil.OAuthUserPrefix+userId;
		if(userId.contains(VirtuosoConstantUtil.OAuthUserPrefix)){
			userURL = userId;
			userId = userId.substring(userId.lastIndexOf("/")+1);
		}
		String sql = "sparql delete from <"+ metaGraph +"> {" +
				"<"+userURL+"> ?p ?o.} \n"+
				"where {\n"+
				   "<"+userURL+"> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://openiot.eu/ontology/ns/User>."+				   
				   "<"+userURL+"> ?p ?o."+
				"}";				   			 
		try{
			conn = ConnectionManager.getConnectionPool().getConnection();			
			Statement st = conn.createStatement();
			st.execute(sql);				
			ConnectionManager.attemptClose(st);
			ConnectionManager.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionManager.attemptClose(conn);
			return false;
		}	
		return true;
	}
	
	public LSMRegisteredServiceImpl getServiceById(String serviceId){
		Connection conn = null;
		LSMRegisteredServiceImpl service  = null;
		String serviceURL = VirtuosoConstantUtil.CloudServicePrefix+serviceId;
		if(serviceId.contains(VirtuosoConstantUtil.CloudServicePrefix)){
			serviceURL = serviceId;
			serviceId = serviceId.substring(serviceId.lastIndexOf("/")+1);
		}
		String sql = "sparql select ?name ?des ?access ?status ?order ?att_status ?sso ?theme ?nameAtt ?addId ?attId"+
				" from <"+ metaGraph +"> \n" +
				"where{ "+
				   "<"+serviceURL+"> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://openiot.eu/ontology/ns/CloudService>."+				   
				   "OPTIONAL{<"+serviceURL+"> <http://www.w3.org/2000/01/rdf-schema#label> ?name.}"+
				   "OPTIONAL{<"+serviceURL+"> <http://www.w3.org/2000/01/rdf-schema#comment> ?des.}"+
				   "<"+serviceURL+"> <http://openiot.eu/ontology/ns/access> ?access."+
				   "<"+serviceURL+"> <http://openiot.eu/ontology/ns/status> ?status."+
				   "<"+serviceURL+"> <http://openiot.eu/ontology/ns/evaluationOrder> ?order."+
				   "<"+serviceURL+"> <http://openiot.eu/ontology/ns/attributeStatus> ?att_status."+
				   "<"+serviceURL+"> <http://openiot.eu/ontology/ns/ssoStatus> ?sso."+
				   "OPTIONAL{<"+serviceURL+"> <http://openiot.eu/ontology/ns/theme> ?theme.}"+
				   "OPTIONAL{<"+serviceURL+"> <http://openiot.eu/ontology/ns/usernameAttr> ?nameAtt.}"+
				   "OPTIONAL{<"+serviceURL+"> <http://openiot.eu/ontology/ns/addressId> ?addId.}"+ 
				   "OPTIONAL{<"+serviceURL+"> <http://openiot.eu/ontology/ns/attribute> ?attId.}"+ 
				"}";			 
		try{
			conn = ConnectionManager.getConnectionPool().getConnection();			
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();			
				while(rs.next()){
					service = new LSMRegisteredServiceImpl();
					service.setId(Long.parseLong(serviceId));
					service.setDescription(rs.getString("des"));
					service.setName(rs.getString("name"));
					service.setEvaluationOrder(rs.getInt("order"));
					service.setServiceId(rs.getString("addId"));
					service.setUsernameAttribute(rs.getString("nameAtt"));
					service.setTheme(rs.getString("theme"));
					service.setServiceId(rs.getString("addId"));
					if(rs.getString("access")!=null&&rs.getString("access").contains("http://openiot.eu/ontology/ns/AnonymousAccess"))
						service.setAnonymousAccess(true);
					else service.setAnonymousAccess(false);
					if(rs.getString("status")!=null&&rs.getString("status").contains("http://openiot.eu/ontology/ns/StatusEnabled"))
						service.setEnabled(true);
					else service.setEnabled(false);
					if(rs.getString("att_status")!=null&&rs.getString("att_status").contains("http://openiot.eu/ontology/ns/AttributeEnabled"))
						service.setIgnoreAttributes(false);
					else service.setIgnoreAttributes(true);
					if(rs.getString("sso")!=null&&rs.getString("sso").contains("http://openiot.eu/ontology/ns/SSOStatusEnabled"))
						service.setSsoEnabled(true);
					else service.setSsoEnabled(false);
					service.setAllowedAttributes(getAllowedAttributesForService(serviceId));
				}
				ConnectionManager.attemptClose(rs);				
			}
			ConnectionManager.attemptClose(st);
			ConnectionManager.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionManager.attemptClose(conn);
			return null;
		}
		return  service;
	}
	
	public boolean deleteServiceById(String serviceId){
		Connection conn = null;
		String serviceURL = VirtuosoConstantUtil.CloudServicePrefix+serviceId;
		if(serviceId.contains(VirtuosoConstantUtil.CloudServicePrefix)){
			serviceURL = serviceId;
			serviceId = serviceId.substring(serviceId.lastIndexOf("/")+1);
		}
		String sql = "sparql delete from <"+ metaGraph +"> {" +
					"<"+serviceURL+"> ?p ?o.} \n"+
					"where{ "+
				   "<"+serviceURL+"> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://openiot.eu/ontology/ns/CloudService>."+		
				   "<"+serviceURL+"> ?p ?o."+
				"}";			 
		try{
			deleteAllowedAttributesForService(serviceURL);
			conn = ConnectionManager.getConnectionPool().getConnection();			
			Statement st = conn.createStatement();
			st.execute(sql);				
			ConnectionManager.attemptClose(st);
			ConnectionManager.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionManager.attemptClose(conn);
			return false;
		}
		return true;
	}
		
	public List<String> getAllowedAttributesForService(String serviceId){
		Connection conn = null;
		String serviceURL = VirtuosoConstantUtil.CloudServicePrefix+serviceId;
		if(serviceId.contains(VirtuosoConstantUtil.CloudServicePrefix)){
			serviceURL = serviceId;
			serviceId = serviceId.substring(serviceId.lastIndexOf("/")+1);
		}
		List<String> atts = new ArrayList<String>();
		String sql = "sparql select distinct ?attName"+
				" from <"+ metaGraph +"> \n" +
				"where{ "+
				   "?att <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://openiot.eu/ontology/ns/ServiceAttribute>.\n"+
				   "?att <http://openiot.eu/ontology/ns/attributeFor> <"+serviceURL+">."+				   
				   "?att <http://www.w3.org/2000/01/rdf-schema#label> ?attName."+ 
				"}";			 
		try{
			conn = ConnectionManager.getConnectionPool().getConnection();			
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();			
				while(rs.next()){
					if(!atts.contains(rs.getString("attName")))
						atts.add(rs.getString("attName"));
				}
				ConnectionManager.attemptClose(rs);				
			}
			ConnectionManager.attemptClose(st);
			ConnectionManager.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionManager.attemptClose(conn);
			return null;
		}
		return  atts;
	}
	
	public boolean deleteAllowedAttributesForService(String serviceId){
		Connection conn = null;
		String serviceURL = VirtuosoConstantUtil.CloudServicePrefix+serviceId;
		if(serviceId.contains(VirtuosoConstantUtil.CloudServicePrefix)){
			serviceURL = serviceId;
			serviceId = serviceId.substring(serviceId.lastIndexOf("/")+1);
		}
		String sql = "sparql delete from <"+ metaGraph +"> {?att ?p ?o.} \n"+
					"where{ "+
				    "?att <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://openiot.eu/ontology/ns/ServiceAttribute>.\n"+
				    "?att <http://openiot.eu/ontology/ns/attributeFor> <"+serviceURL+">."+				   
				    "?att ?p ?o."+ 
				"}";			 
		try{
			conn = ConnectionManager.getConnectionPool().getConnection();			
			Statement st = conn.createStatement();
			st.execute(sql);
			ConnectionManager.attemptClose(st);
			ConnectionManager.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionManager.attemptClose(conn);	
			return false;
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public LSMTicketGrantingTicketImpl getTicketSchedulerById(String sche_Id){
		Connection conn = null;
		LSMTicketGrantingTicketImpl sche  = null;
		String prefix = "http://lsm.deri.ie/resource/";
		String schedulerURL = prefix + sche_Id;
		if(sche_Id.contains(prefix)){
			schedulerURL = sche_Id;
			sche_Id = sche_Id.substring(sche_Id.lastIndexOf("/")+1);
		}
		String sql = "sparql select ?timeUsed ?creationTime ?lastTimeUsed ?preUsed ?grant ?auth ?exp ?ser ?expired"+
				" from <"+ metaGraph +"> \n" +
				"where{ "+
				   "<"+schedulerURL+"> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://openiot.eu/ontology/ns/TicketScheduler>."+				   
				   "OPTIONAL{<"+schedulerURL+"> <http://openiot.eu/ontology/ns/timesUsed> ?timeUsed.}"+
				   "OPTIONAL{<"+schedulerURL+"> <http://openiot.eu/ontology/ns/creationTime> ?creationTime.}"+
				   "OPTIONAL{<"+schedulerURL+"> <http://openiot.eu/ontology/ns/lastTimeUsed> ?lastTimeUsed.}"+
				   "OPTIONAL{<"+schedulerURL+"> <http://openiot.eu/ontology/ns/prevLastTimeUsed> ?preUsed.}"+
				   "OPTIONAL{<"+schedulerURL+"> <http://openiot.eu/ontology/ns/grants> ?grant.}"+
				   "<"+schedulerURL+"> <http://openiot.eu/ontology/ns/authenticatedBy> ?auth."+
				   "<"+schedulerURL+"> <http://openiot.eu/ontology/ns/expirationPolicy> ?exp."+
				   "<"+schedulerURL+"> <http://openiot.eu/ontology/ns/servicesGranted> ?ser."+
				   "<"+schedulerURL+"> <http://openiot.eu/ontology/ns/validity> ?expired."+
				"}";			 
		try{
			conn = ConnectionManager.getConnectionPool().getConnection();			
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();			
				while(rs.next()){
					sche = new LSMTicketGrantingTicketImpl();
					sche.setId(sche_Id);
					sche.setCountOfUses(rs.getInt("timeUsed"));
					sche.setCreationTime(DateUtil.string2Date(rs.getString("creationTime"),"yyyy-MM-dd HH:mm:ss.SSS").getTime());
					sche.setLastTimeUsed(DateUtil.string2Date(rs.getString("lastTimeUsed"),"yyyy-MM-dd HH:mm:ss.SSS").getTime());
					sche.setPreviousLastTimeUsed(DateUtil.string2Date(rs.getString("preUsed"),"yyyy-MM-dd HH:mm:ss.SSS").getTime());
					sche.setAuthentication((Authentication)SerializationUtils.deserialize(Hex.decode(rs.getString("auth"))));
					sche.setExpirationPolicy((ExpirationPolicy)SerializationUtils.deserialize(Hex.decode(rs.getString("exp"))));
					sche.setServices((HashMap<String, Service>)SerializationUtils.deserialize(Hex.decode(rs.getString("ser"))));
					if(rs.getString("grant")!=null){
						sche.setTicketGrantingTicket(getTicketSchedulerById(rs.getString("grant")));
					}
					if(rs.getString("expired")!=null&&rs.getString("expired").contains("http://openiot.eu/ontology/ns/TicketGrantingExpired"))
						sche.setExpired(true);
					else sche.setExpired(false);
				}
				ConnectionManager.attemptClose(rs);				
			}
			ConnectionManager.attemptClose(st);
			ConnectionManager.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionManager.attemptClose(conn);
			return null;
		}
		return  sche;
	}
	
	public boolean deleteTicketSchedulerById(String sche_Id){
		Connection conn = null;
		String prefix = "http://lsm.deri.ie/resource/";
		String schedulerURL = prefix + sche_Id;
		if(sche_Id.contains(prefix)){
			schedulerURL = sche_Id;
			sche_Id = sche_Id.substring(sche_Id.lastIndexOf("/")+1);
		}
		String sql = "sparql delete from <"+ metaGraph +"> {<"+schedulerURL+"> ?p ?o.} \n"+
				"where{ "+
				   "<"+schedulerURL+"> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://openiot.eu/ontology/ns/TicketScheduler>."+			
				   "<"+schedulerURL+"> ?p ?o."+
				"}";			 
		try{
			conn = ConnectionManager.getConnectionPool().getConnection();			
			Statement st = conn.createStatement();
			st.execute(sql);
			ConnectionManager.attemptClose(st);
			ConnectionManager.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionManager.attemptClose(conn);
			return false;
		}
		return  true;
	}
	
	public LSMServiceTicketImpl getTicketById(String ticketId){
		Connection conn = null;
		LSMServiceTicketImpl ticket  = null;
		String prefix = "http://lsm.deri.ie/resource/";
		String ticketURL = prefix + ticketId;
		if(ticketId.contains(prefix)){
			ticketURL = ticketId;
			ticketId = ticketId.substring(ticketId.lastIndexOf("/")+1);
		}
		String sql = "sparql select ?timeUsed ?creationTime ?lastTimeUsed ?preUsed ?grant ?ser ?exp ?login "+
				" from <"+ metaGraph +"> \n" +
				"where{ "+
				   "<"+ticketURL+"> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://openiot.eu/ontology/ns/Ticket>."+				   
				   "OPTIONAL{<"+ticketURL+"> <http://openiot.eu/ontology/ns/timesUsed> ?timeUsed.}"+
				   "OPTIONAL{<"+ticketURL+"> <http://openiot.eu/ontology/ns/creationTime> ?creationTime.}"+
				   "OPTIONAL{<"+ticketURL+"> <http://openiot.eu/ontology/ns/lastTimeUsed> ?lastTimeUsed.}"+
				   "OPTIONAL{<"+ticketURL+"> <http://openiot.eu/ontology/ns/prevLastTimeUsed> ?preUsed.}"+
				   "<"+ticketURL+"> <http://openiot.eu/ontology/ns/grantedBy> ?grant."+
				   "<"+ticketURL+"> <http://openiot.eu/ontology/ns/serviceBinary> ?ser."+
				   "<"+ticketURL+"> <http://openiot.eu/ontology/ns/expirationPolicy> ?exp."+
				   "<"+ticketURL+"> <http://openiot.eu/ontology/ns/ticketFrom> ?login."+
				"}";			 
		try{
			conn = ConnectionManager.getConnectionPool().getConnection();			
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();			
				while(rs.next()){
					ticket = new LSMServiceTicketImpl();
					ticket.setId(ticketId);
					ticket.setCountOfUses(rs.getInt("timeUsed"));
					ticket.setCreationTime(DateUtil.string2Date(rs.getString("creationTime"),"yyyy-MM-dd HH:mm:ss.SSS").getTime());
					ticket.setLastTimeUsed(DateUtil.string2Date(rs.getString("lastTimeUsed"),"yyyy-MM-dd HH:mm:ss.SSS").getTime());
					ticket.setPreviousLastTimeUsed(DateUtil.string2Date(rs.getString("preUsed"),"yyyy-MM-dd HH:mm:ss.SSS").getTime());
					ticket.setExpirationPolicy((ExpirationPolicy)SerializationUtils.deserialize(Hex.decode(rs.getString("exp"))));
					ticket.setTicketGrantingTicket(getTicketSchedulerById(rs.getString("grant")));
					ticket.setService((Service)SerializationUtils.deserialize(Hex.decode(rs.getString("ser"))));
					if(rs.getString("login")!=null&&rs.getString("login").contains("http://openiot.eu/ontology/ns/NewLogin"))
						ticket.setFromNewLogin(true);
					else ticket.setFromNewLogin(false);
				}
				ConnectionManager.attemptClose(rs);				
			}
			ConnectionManager.attemptClose(st);
			ConnectionManager.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionManager.attemptClose(conn);
			return null;
		}
		return  ticket;
	}
	
	public boolean deleteTicketById(String ticketId){
		Connection conn = null;
		String prefix = "http://lsm.deri.ie/resource/";
		String ticketURL = prefix + ticketId;
		if(ticketId.contains(prefix)){
			ticketURL = ticketId;
			ticketId = ticketId.substring(ticketId.lastIndexOf("/")+1);
		}
		String sql = "sparql delete from <"+ metaGraph +"> {<"+ticketURL+"> ?p ?o.} \n"+
				"where{ "+
				   "<"+ticketURL+"> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://openiot.eu/ontology/ns/Ticket>."+
				   "<"+ticketURL+"> ?p ?o."+
				"}";			 
		try{
			conn = ConnectionManager.getConnectionPool().getConnection();			
			Statement st = conn.createStatement();
			st.execute(sql);
			ConnectionManager.attemptClose(st);
			ConnectionManager.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionManager.attemptClose(conn);
			return false;
		}
		return  true;
	}
	
	public List<LSMServiceTicketImpl> getAllServiceTicketsOfTicketGrantingTicket(String grantId) {
		Connection conn = null;
		String prefix = "http://lsm.deri.ie/resource/";
		String grantURL = prefix + grantId;
		if(grantId.contains(prefix)){
			grantURL = grantId;
			grantId = grantId.substring(grantId.lastIndexOf("/")+1);
		}
		List<LSMServiceTicketImpl> ticketList  = null;
		String sql = "sparql select ?ticket"+
				" from <"+ metaGraph +"> \n" +
				"where{ "+
				   "?ticket <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://openiot.eu/ontology/ns/Ticket>."+		
				   "?ticket <http://openiot.eu/ontology/ns/grantedBy> "+"<"+grantURL+">. \n" +
				"}";			 
		try{
			conn = ConnectionManager.getConnectionPool().getConnection();			
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();	
				ticketList = new ArrayList<LSMServiceTicketImpl>();
				while(rs.next()){
					LSMServiceTicketImpl t = getTicketById(rs.getString("ticket"));
					ticketList.add(t);
				}
				ConnectionManager.attemptClose(rs);				
			}
			ConnectionManager.attemptClose(st);
			ConnectionManager.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionManager.attemptClose(conn);
			return null;
		}
		return  ticketList;
	}

	/**
	 * Returns the list of all LSMTicketGrantingTicketImpls
	 * 
	 * @return
	 */
	public List<LSMTicketGrantingTicketImpl> getAllTicketGrantingTickets() {
		Connection conn = null;
		List<LSMTicketGrantingTicketImpl> grantList  = null;
		String sql = "sparql select ?tic_grant"+
				" from <"+ metaGraph +"> \n" +
				"where{ "+
				   "?tic_grant <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://openiot.eu/ontology/ns/TicketScheduler>."+		
				"}";			 
		try{
			conn = ConnectionManager.getConnectionPool().getConnection();			
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();	
				grantList = new ArrayList<LSMTicketGrantingTicketImpl>();
				while(rs.next()){
					LSMTicketGrantingTicketImpl t = getTicketSchedulerById(rs.getString("tic_grant"));
					grantList.add(t);
				}
				ConnectionManager.attemptClose(rs);				
			}
			ConnectionManager.attemptClose(st);
			ConnectionManager.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionManager.attemptClose(conn);
			return null;
		}
		return  grantList;
	}

	/**
	 * Returns the list of all LSMServiceTicketImpls
	 * 
	 * @return
	 */
	public List<LSMServiceTicketImpl> getAllServiceTickets() {		
		Connection conn = null;
		List<LSMServiceTicketImpl> ticketList  = null;
		String sql = "sparql select ?ticket"+
				" from <"+ metaGraph +"> \n" +
				"where{ "+
				   "?ticket <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://openiot.eu/ontology/ns/Ticket>."+		
				"}";			 
		try{
			conn = ConnectionManager.getConnectionPool().getConnection();			
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();	
				ticketList = new ArrayList<LSMServiceTicketImpl>();
				while(rs.next()){
					LSMServiceTicketImpl t = getTicketById(rs.getString("ticket"));
					ticketList.add(t);
				}
				ConnectionManager.attemptClose(rs);				
			}
			ConnectionManager.attemptClose(st);
			ConnectionManager.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionManager.attemptClose(conn);
			return null;
		}
		return  ticketList;
	}

	/**
	 * Returns the the number of available LSMTicketGrantingTicketImpls
	 * 
	 * @return
	 */
	public int getTicketGrantingTicketsCount() {
		return -1;
	}

	/**
	 * Returns the the number of available LSMServiceTicketImpls
	 * 
	 * @return
	 */
	public int getServiceTicketsCount() {
		return -1;
	}

	/**
	 * Retrievs a user by the username
	 * 
	 * @param username
	 * @return
	 */
	public User getUserByUsername(String username) {
		Connection conn = null;
		org.openiot.lsm.security.oauth.mgmt.User user = null;
		String userURL = VirtuosoConstantUtil.OAuthUserPrefix+username;
		if(username.contains(VirtuosoConstantUtil.OAuthUserPrefix)){
			userURL = username;
			username = username.substring(username.lastIndexOf("/")+1);
		}
		String sql = "sparql select ?nick ?mbox ?pass ?role"+
				" from <"+ metaGraph +"> \n" +
				"where{ "+
				   "<"+userURL+"> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://openiot.eu/ontology/ns/User>."+				   
				   "OPTIONAL{<"+userURL+"> <http://xmlns.com/foaf/0.1/nick> ?nick.}"+
				   "OPTIONAL{<"+userURL+"> <http://xmlns.com/foaf/0.1/mbox> ?mbox.}"+
				   "<"+userURL+"> <http://openiot.eu/ontology/ns/password> ?pass."+
				   "<"+userURL+"> <http://openiot.eu/ontology/ns/role> ?role."+
				"}";			 
		try{
			conn = ConnectionManager.getConnectionPool().getConnection();			
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				user = new org.openiot.lsm.security.oauth.mgmt.User();
				user.setUsername(username);				
				while(rs.next()){
					user.setEmail(rs.getString("mbox"));
					user.setPassword(rs.getString("pass"));
					user.setName(rs.getString("nick"));
					List<Role> roles = user.getRoles();
					if(roles==null){
						roles = new ArrayList<Role>();
						user.setRoles(roles);
					}
					Role role = getRoleById(rs.getString("role"));
					if(!roles.contains(role)){
						roles.add(role);
					}
				}
				ConnectionManager.attemptClose(rs);				
			}
			ConnectionManager.attemptClose(st);
			ConnectionManager.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionManager.attemptClose(conn);
			return null;
		}
		return  user;
	}

	/**
	 * Retrieves all LSMRegisteredServiceImpls
	 * 
	 * @return
	 */
	public List<RegisteredService> getAllRegisteredServices() {		
		Connection conn = null;
		List<RegisteredService> serviceList  = null;
		String sql = "sparql select ?service"+
				" from <"+ metaGraph +"> \n" +
				"where{ "+
				   "?service <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://openiot.eu/ontology/ns/CloudService>."+		
				"}";			 
		try{
			conn = ConnectionManager.getConnectionPool().getConnection();			
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();	
				serviceList = new ArrayList<RegisteredService>();
				while(rs.next()){
					LSMRegisteredServiceImpl t = getServiceById(rs.getString("service"));
					serviceList.add(t);
				}
				ConnectionManager.attemptClose(rs);				
			}
			ConnectionManager.attemptClose(st);
			ConnectionManager.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionManager.attemptClose(conn);
			return null;
		}
		return  serviceList;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
