package org.openiot.lsm.http;

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
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.HashMap;
//import java.util.Map;


import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


//import org.deri.cqels.engine.ExecContext;
import org.openiot.commons.util.PropertyManagement;
import org.openiot.lsm.beans.Observation;
import org.openiot.lsm.beans.ObservedProperty;
import org.openiot.lsm.beans.RDFTuple;
import org.openiot.lsm.beans.Sensor;
import org.openiot.lsm.manager.CQELSManager;
//import org.openiot.lsm.cqels.CQELSStream;
//import org.openiot.lsm.manager.CQELSManager;
import org.openiot.lsm.manager.SensorManager;
import org.openiot.lsm.manager.TriplesDataRetriever;
import org.openiot.lsm.utils.ConstantsUtil;
import org.openiot.lsm.utils.NumberUtil;
import org.openiot.lsm.utils.VirtuosoConstantUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * 
 * @author Hoan Nguyen Mau Quoc
 * 
 */

//30/01/2015 
@WebServlet("/ObjectServlet")
public class ObjectServlet extends HttpServlet {
	// private static final long serialVersionUID = 2L;

	final static Logger logger = LoggerFactory.getLogger(ObjectServlet.class);
	static private PropertyManagement propertyManagement = null;
	//private CQELSManager +;
	private CQELSManager cqelsManager = CQELSManager.getCQELSManager();
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ObjectServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		System.out.println("JBoss server home dir is: "
				+ System.getProperty("jboss.server.home.dir"));// Jboss
		ConstantsUtil.realPath = this.getServletContext()
				.getRealPath("WEB-INF");
		propertyManagement = new PropertyManagement();
		//cqelsManager = new CQELSManager();
		cqelsManager.CQELSManagerStartWs();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String sb = "";

		PrintWriter out = response.getWriter();
		try {
			ObjectInputStream inputFromClient = new ObjectInputStream(
					request.getInputStream());
			// deserialize the object, note the cast
			Object object = inputFromClient.readObject();
			String graphURL = request.getHeader("graphURL");
			String api = request.getHeader("api");
			String apiType = request.getHeader("apiType");
			// System.out.println(api);
			logger.debug("API function:" + api);

			if (NumberUtil.isInteger(api)) {
				sb = processRequestImpl(api, object);
			}
			response.setContentType("text/xml");
			response.setHeader("Pragma", "no-cache"); // HTTP 1.0
			response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
			out.println(sb);
			out.close();
			logger.info(sb);
		} catch (Exception ex) {
			out.println(sb);
			// ex.printStackTrace();
			out.close();
			logger.error("Server returns error", ex);
		}
	}

	private String processRequestImpl(String api, Object object) {
		// TODO Auto-generated method stub
		String result = "Your request processed successfully";
		logger.info("object = " + object);
		logger.info("Naomi = " + object);
		try {
			SensorManager sensorManager = new SensorManager();

			Sensor sensor = null;
			logger.info("api id= " + api);
			switch (api) {
			//Temp code for testing----
//			case "99":
//				if (object instanceof Sensor)
//					sensor = (Sensor) object;
//				else
//					break;
//				String tempSensorIDForZia = sensor.getId();
//				logger.info("Get Zia sensor with id = " + tempSensorIDForZia);
//				break;
//				//-------------------------------	
			case "21":
				if (object instanceof Sensor)
					sensor = (Sensor) object;
				else
					break;
				logger.info("add new sensor with id = " + sensor.getId());
				String sensorTypeId = sensorManager.getSensorTypeId(sensor
						.getSensorType().toLowerCase());
				String triples = TriplesDataRetriever.getSensorTripleMetadata(
						sensor, sensorTypeId);
				// System.out.println(triples);
				if ((sensor.getMetaGraph() == null)
						|| (sensor.getMetaGraph() == ""))
					sensor.setMetaGraph(propertyManagement
							.getSchedulerLsmMetaGraph());
				sensorManager.setDataGraph(sensor.getDataGraph());
				sensorManager.setMetaGraph(sensor.getMetaGraph());
				sensorManager.insertTriplesToGraph(sensor.getMetaGraph(),
						triples);
				sensorManager.runSpatialIndex();
				logger.info("Add new sensor");
				logger.debug("Add new sensor");
				// cqelsManager.addSensorStream(sensor.getId());
				break;
			case "22":
				Observation observation = null;
				if (object instanceof Observation)
					observation = (Observation) object;
				else
					break;
				logger.debug("Add observation object with id = "
						+ observation.getId());
				triples = "";

				if ((observation.getMetaGraph() == null)
						|| (observation.getMetaGraph() == ""))
					observation.setMetaGraph(propertyManagement
							.getLSMLocalMetaGraph());

				sensorManager.setDataGraph(observation.getDataGraph());
				sensorManager.setMetaGraph(observation.getMetaGraph());

				logger.info("observation.getSensor = " + observation.getSensor());
				sensor = sensorManager
						.getSpecifiedSensorWithSensorId(observation.getSensor());
				logger.info("sensor object = " + sensor);
//				 if (sensor == null)
//				 logger.error("NULL SENSOR");
//				 else if (sensor.getPlace() == null)
//				 logger.error("NULL PLACE");
//				 else if(sensor.getPlace().getLat())
//				 System.out.println("NULL LAT");
				
				 
				 String foi = VirtuosoConstantUtil.sensorObjectDataPrefix
						+ Double.toString(sensor.getPlace().getLat())
								.replace(".", "").replace("-", "")
						+ Double.toString(sensor.getPlace().getLng())
								.replace(".", "").replace("-", "");
				triples += TriplesDataRetriever.getObservationTripleData(
						observation.getId(), observation.getSensor(), foi,
						observation.getTimes());
				logger.debug("Testing Feature Of Interest-FOI " + foi);
				// System.out.println("testind foi" + foi);
				OntModel model = ModelFactory.createOntologyModel();
				for (ObservedProperty obv : observation.getReadings()) {
					OntClass cl = model.createClass(obv.getPropertyType());
					if (obv.getUnit().equals(""))
						triples += TriplesDataRetriever
								.getTripleDataHasNoUnit(
										"http://purl.oclc.org/NET/ssnx/ssn#ObservationValue",
										cl.getLocalName(),
										obv.getValue(),
										observation.getId(),
										sensor.getProperties().get(
												obv.getPropertyType()),
										observation.getTimes());
					else
						triples += TriplesDataRetriever
								.getTripleDataHasUnit(
										"http://purl.oclc.org/NET/ssnx/ssn#ObservationValue",
										cl.getLocalName(),
										obv.getValue(),
										obv.getUnit(),
										observation.getId(),
										sensor.getProperties().get(
												obv.getPropertyType()),
										observation.getTimes());
				}
				
				sensorManager.insertTriplesToGraph(observation.getDataGraph(),// ///////
						triples);
				cqelsManager.insertTriplesToStream(sensor.getId(), triples);
				logger.info("Added new sensor data successfully");
				break;
			case "23":
				RDFTuple tuple = null;
				if (object instanceof RDFTuple)
					tuple = (RDFTuple) object;
				else
					break;
				// System.out.println(tuple.getNtriple());
				sensorManager.insertTriplesToGraph(tuple.getGraphURL(),
						tuple.getNtriple());
				logger.info("Add triples to graph " + tuple.getGraphURL());
				break;
			case "24":
				tuple = null;
				if (object instanceof RDFTuple)
					tuple = (RDFTuple) object;
				else
					break;
				// System.out.println(tuple.getNtriple());
				if (tuple.getNtriple().equals("all")) {
					sensorManager.clearGraph(tuple.getGraphURL());
					logger.info("Delete all triples of graph "
							+ tuple.getGraphURL());
				} else {
					sensorManager.deleteTriples(tuple.getGraphURL(),
							tuple.getNtriple());
					logger.info("Delete triples patterns of graph "
							+ tuple.getGraphURL());
				}
				break;
			case "25":
				HashMap<String, String> patterns = null;
				if (object instanceof HashMap<?, ?>)
					patterns = (HashMap<String, String>) object;
				sensorManager.updateGraph(patterns.get("graph"),
						patterns.get("update"), patterns.get("delete"));
			default:
				break;
			}

		} catch (Exception e) {
			// e.printStackTrace();
			logger.error("Server returns error", e);
			result = e.toString();
		}
		return result;
	}
}