package org.openiot.lsm.reasoning.aspjavamanager.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/*
 * This class represents the knowledge base of a context
 *
 */
public class Formulas implements Cloneable {

	final static Logger logger = LoggerFactory.getLogger(Formulas.class);
	private List<Formula> formulas  = new ArrayList<Formula>();

	public Formulas() {
	}

	public Formulas(Formulas kb){
		this.formulas = ((List) ( (ArrayList) kb.formulas).clone());
	}

	public Formulas(List<Formula> formulas) {
		this.formulas = ((List) ( (ArrayList) formulas).clone());
	}

	public Formulas(String filename) throws IOException{
		this.readFromFile(filename);
	}

	public Formulas(String[] as){
		for(final String s: as){
			this.formulas.add(new Formula(s));
		}
	}

	public boolean add(Formula e) throws CloneNotSupportedException {
		return formulas.add(e.clone());
	}

	public boolean addAll(Collection<? extends Formula> c) {
		return formulas.addAll(((List) ( (ArrayList) c).clone()));
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		final Formulas cloned = (Formulas) super.clone();
		return cloned;
	}

	public boolean contains(Formula o) {
		for(final Formula f: this.formulas){
			if(f.equals(o)) {
				return true;
			}
		}
		return false;
	}

	public List<Formula> getFormulas() {
		return formulas;
	}

	public boolean isEmpty() {
		return formulas.isEmpty();
	}

	public void print(){
		for(final Formula f: this.formulas){
			f.print();
		}
	}


	/*
	 * read formulas from file (in asp format)
	 */
	public void readFromFile(String kbFile) throws IOException{
		BufferedReader br = null;
		String line = "";
		br = new BufferedReader(new FileReader(kbFile));
		//one formula in one line
		while ((line = br.readLine()) != null) {
			System.out.println(line);
			if(line.length() > 1){
				final String temp = line.substring(0,line.length()-1); // omit '.' at the end of a line
				final Formula f = new Formula(temp);
				this.formulas.add(f);
			}

		}
//		this.print();
		br.close();
	}

	public void setFormulas(List<Formula> formulas) {
		this.formulas =  ((List) ( (ArrayList) formulas).clone());
	}

	/*
	 * write "rules" down to file
	 */
	public void toFile(String filename) throws IOException{
		final String s = this.toString();
		final File file = new File(filename);
		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}
		final FileWriter fw = new FileWriter(file.getAbsoluteFile());
		final BufferedWriter bw = new BufferedWriter(fw);
		bw.write(s);
		bw.close();
	}

	@Override
	public String toString() {
		String s = "";
		for(final Formula r:this.formulas){
			s += r.toString();
		}
		return s;
	}

}
