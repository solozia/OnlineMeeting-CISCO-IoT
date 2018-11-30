package org.openiot.lsm.reasoning.data;

import java.util.HashMap;
import java.util.Map;

public class User {
	private String userId;
	private String userPlace; // private; public
	private String userActivity; //free; no-free
	private String userDevice = Constants.LAPTOP;
	private Coordinate userCoordinate; 
	private Map<String, String> features; // Noise, Light, Proximity
	private Map<String, String> capabilities; //take, type, ...
	private NFCTagData nfcTagData; 

	


	public User(String userId) {
		super();
		this.userId = userId;
		initFeatures();
		initCapabilities();
	}
	
	public void initFeatures(){
		this.features = new HashMap();
		this.features.put(Constants.NOISE, Constants.ON);
		this.features.put(Constants.LIGHT, Constants.ON);
		this.features.put(Constants.PROXIMITY, Constants.ON);
	}
	
	public void initCapabilities(){
		this.capabilities = new HashMap();
		this.capabilities.put(Constants.TALK, Constants.ON);
		this.capabilities.put(Constants.READ, Constants.ON);
		this.capabilities.put(Constants.LISTEN, Constants.ON);
		this.capabilities.put(Constants.TYPE, Constants.ON);
		this.capabilities.put(Constants.SHARE, Constants.ON);
	}


	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}


	public String getUserPlace() {
		return userPlace;
	}


	public void setUserPlace(String userPlace) {
		this.userPlace = userPlace;
	}


	public String getUserActivity() {
		return userActivity;
	}


	public void setUserActivity(String userActivity) {
		this.userActivity = userActivity;
	}


	public String getUserDevice() {
		return userDevice;
	}


	public void setUserDevice(String userDevice) {
		this.userDevice = userDevice;
	}

	public Map<String, String> getFeatures() {
		return features;
	}

	public void setFeatures(Map<String, String> features) {
		this.features = features;
	}

	public Map<String, String> getCapabilities() {
		return capabilities;
	}

	public void setCapabilities(Map<String, String> capabilities) {
		this.capabilities = capabilities;
	}

	public Coordinate getUserCoordinate() {
		return userCoordinate;
	}

	public void setUserCoordinate(Coordinate userLocation) {
		this.userCoordinate = userLocation;
	}

	
	
	public NFCTagData getNfcTagData() {
		return nfcTagData;
	}

	public void setNfcTagData(NFCTagData nfcTagData) {
		this.nfcTagData = nfcTagData;
	}
	
	
	
	
}
