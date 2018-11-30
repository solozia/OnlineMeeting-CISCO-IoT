package org.openiot.lsm.reasoning.engine;

public class Event extends java.util.EventObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String jsonObject;
	
	public Event(Object source, String s) {
		super(source);
		this.jsonObject = s;

	}

	public String getJsonObject() {
		return jsonObject;
	}

	public void setJsonObject(String jsonObject) {
		this.jsonObject = jsonObject;
	}
	
}
