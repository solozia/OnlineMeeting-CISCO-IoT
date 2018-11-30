/*
 * @author: Thu-Le Pham
 * Date: 12/08/2014
 * This class manages the execution the Asp solver (clingo 4.3.0) from java
 * Support for MacOx, Win, and Linux
 * Run from file
 */
package org.openiot.lsm.reasoning.aspjavamanager.manager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.openiot.lsm.reasoning.aspjavamanager.data.Formula;
import org.openiot.lsm.reasoning.aspjavamanager.data.Formulas;

public class AspManager {

	private static final AspManager instance = new AspManager();
	public static AspManager getInstance() {
		return AspManager.instance;
	}

	private List<Formulas> result = new ArrayList();
	Filter filter = new Filter();
	private String clingoURI = "./";


	public AspManager() {
		super();
	}

	public AspManager(String clingoURI) {
		super();
		this.clingoURI = clingoURI;
	}



	/*
	 * 2015-03-11
	 * Call Clingo without writing to File
	 */
	public void callClingo(String proStr) throws UnsupportedOperationException, IOException, CloneNotSupportedException{

		final String os = System.getProperty("os.name").toLowerCase();

		String executable = "";

		if (os.contains("win")) {
			executable = "clingo-4.3.0-win32.exe";
		} else if (os.contains("mac")) {
			executable = "clingo-4.3.0-mac";
		} else if (os.contains("linux")) {
			executable = "clingo-4.3.0-linux";
		} else {
			throw new UnsupportedOperationException();
		}

		//Form the command line
		String commandline = clingoURI + executable + " ";
		commandline = commandline.concat("0 --verbose=0");




		// execute the command line
		final Process process = Runtime.getRuntime().exec(commandline);
		final OutputStream stdin = process.getOutputStream ();
		final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin));
		writer.write(proStr);
		writer.flush();
		writer.close();



		// All output from clingo are added to bufferedReader
     	final BufferedReader bufferedReader = new BufferedReader(
     				new InputStreamReader(process.getInputStream()));


		//parse answer set
     	final List<String> answerSetLines =  new ArrayList<String>();
     	String currentLine;
		while ((currentLine = bufferedReader.readLine()) != null) {
//			System.out.println("------- = " + currentLine);
			if (!currentLine.startsWith("SATISFIABLE")) {
				answerSetLines.add(currentLine);

            }
		}


		for(final String line: answerSetLines){
			final String[] as = line.split(" ");
			Formulas fas;
			if(!filter.isEmpty()){
				fas = new Formulas();
				for (final String element : as) {
					if(filter.accepts(this.getPredicate(element))){
						fas.add(new Formula(element));
					}
				}
			}
			else{
				fas = new Formulas(as);
			}
			System.out.println(fas);
			this.result.add(fas);
		}

	}

	public String getPredicate(String atom){
		if(atom.indexOf('(') >= 0) {
			return atom.substring(0,atom.indexOf('('));
		}
		return null;
	}

	public String getClingoURI() {
		return clingoURI;
	}

	public void setClingoURI(String clingoURI) {
		this.clingoURI = clingoURI;
	}

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	public List<Formulas> getResult() {
		return result;
	}

	public void setResult(List<Formulas> result) {
		this.result = result;
	}

}
