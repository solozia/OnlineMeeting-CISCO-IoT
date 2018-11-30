package org.openiot.lsm.cqels;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.deri.cqels.engine.ExecContext;
import org.deri.cqels.engine.RDFStream;
import org.joda.time.DateTime;
import org.slf4j.Logger;
//import org.owasp.esapi.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.XSD;

public class CQELSStream extends RDFStream implements Runnable {
	private boolean stop = false;
	private int sleep = 500;
	static final Logger logger = LoggerFactory.getLogger(CQELSStream.class);
	private String exSid;

	public int getSleep() {
		return sleep;
	}

	public void setSleep(int sleep) {
		this.sleep = sleep;
	}

	public CQELSStream(ExecContext context, String uri) {
		super(context, uri);
		// TODO Auto-generated constructor stub
	}

	// public void addTriples(String triples) {
	// // List<Statement> stmts = this.getStatements(triples);
	// // for (Statement stmt : stmts)
	// this.streamStatements(triples);
	//
	// }

	public void streamStatements(String triplesStr) {
		String[] stmts = triplesStr.split("\n");
		logger.info("Streamed triples to: " + this.getURI());
		for (String stmt : stmts) {
			// logger.info("STMT: " + stmt);
			// String subject=stmt.spl
			String s = removebracket(stmt.split(" ")[0]);
			String p = removebracket(stmt.split(" ")[1]);
			String o = stmt.split(" ")[2];
			o = o.substring(0, o.length() - 1);
			// logger.info("Streaming: " + s + " " + p + " " + o); //shows after
			// CQELS result
			if (o.contains("^^")) {
				if (o.contains("double")) {
					o = o.substring(1, o.indexOf("^^") - 1);
					this.stream(n(s), n(p), l(o));
				} else if (o.contains("dateTime")) {
					o = o.substring(1, o.indexOf("^^") - 1);
					this.stream(n(s), n(p), dt(o));
				}
			} else {
				o = removebracket(o);
				this.stream(n(s), n(p), n(o));
			}
			
			String tr=triplesStr;//zia
			//System time function (NAOMI T5)
			//Added 18/03/2015
			if (tr.contains("observedBy>")){
				int index=tr.indexOf("observedBy>");
				//THU changed it-avoid null error - 20150330
				String sensorid=tr.substring(index+12, tr.indexOf("\n", index+12));	
//				String sensorid=tr.substring(index+12, indnex+59);
				
				int slashIndex = sensorid.lastIndexOf("/");
				int arrowindex = sensorid.indexOf(">");// // String ExSensorID =
				exSid = sensorid.substring(slashIndex+1, arrowindex);
				Date today;
				SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");
				today = new Date();
				String date = DATE_FORMAT.format(today);
				
				//System.out.println("AfterGetStream(T5):" +  " "+ "SensorID: "+exSid +  " " + date);
			}
		
			//06032015 Naomi for testing
			 logger.info("\n"
			 +
			 "#########################################################Triple Stream #########################################################"
			 + "\n"
			 + triplesStr
			 + "\n"
			 +
			 "#########################################################End of Triple Stream #########################################################");
		}
	}

	private Node dt(String o) {
		Model m = ModelFactory.createDefaultModel();
		Literal l = m.createTypedLiteral(DateTime.parse(o));
		Node n = l.asNode();
		// logger.info("Literal value: " + n.getLiteralDatatypeURI()); //shows
		// after CQELS result
		return n;
	}

	public static Node n(String str) {
		// if (str.contains("^^"))
		// return Node.createLiteral(str);

		return Node.createURI(str);
	}

	static Node l(String str) {
		// logger.info("Creating literal: " + str); //shows after CQELS result
		Model m = ModelFactory.createDefaultModel();
		Literal l = m.createTypedLiteral(Double.parseDouble(str));
		Node n = l.asNode();
		// logger.info("Literal value: " + n.getLiteralDatatypeURI()); //shows
		// after CQELS result
		return n;
	}

	private String removebracket(String string) {
		string = string.replaceAll("<", "").replaceAll(">", "");
		// logger.info("postprocessed string: " + string);
		return string;
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		stop = true;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (sleep > 0 && stop != false)
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}