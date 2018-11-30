package org.openiot.lsm.reasoning.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Meeting {
	String meetingId;
	long meetingStartTime;
	long meetingEndTime;
	String meetingLocation;
//	List<Coordinate> meetingCoordinates;
	Coordinate meetingCoordinate = new Coordinate(53.28991301691684,-9.074204787611961);
	List<User> users = new ArrayList<User>(); //attendees login into the meeting room
	List<String> attendees = new ArrayList<String>(); // attendees in meeting information
	String meetingOrganizerId;
	List<AgendaItem> agendaItems;
	Map<String, List<String>> agendaItemUsers;
	Map<String, String> agendaItemUser = new HashMap<String, String>();
	
	public Meeting() {
		super();
	}
	
	public Meeting(String meetingId) {
		super();
		this.meetingId = meetingId;
	}



	public String getMeetingId() {
		return meetingId;
	}

	public void setMeetingId(String meetingId) {
		this.meetingId = meetingId;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}
	
	public long getMeetingStartTime() {
		return meetingStartTime;
	}

	public void setMeetingStartTime(long meetingStartTime) {
		this.meetingStartTime = meetingStartTime;
	}

	public long getMeetingEndTime() {
		return meetingEndTime;
	}

	public void setMeetingEndTime(long meetingEndTime) {
		this.meetingEndTime = meetingEndTime;
	}

	public String getMeetingLocation() {
		return meetingLocation;
	}

	public void setMeetingLocation(String meetingLocation) {
		this.meetingLocation = meetingLocation;
	}

	public List<AgendaItem> getAgendaItems() {
		return agendaItems;
	}

	public void setAgendaItems(List<AgendaItem> agendaItems) {
		this.agendaItems = agendaItems;
	}

//	public List<Coordinate> getMeetingCoordinates() {
//		return meetingCoordinates;
//	}
//
//	public void setMeetingCoordinates(List<Coordinate> meetingCoordinates) {
//		this.meetingCoordinates = meetingCoordinates;
//	}

	public String getMeetingOrganizerId() {
		return meetingOrganizerId;
	}

	public void setMeetingOrganizerId(String meetingOrganizerId) {
		this.meetingOrganizerId = meetingOrganizerId;
	}

	public List<String> getAttendees() {
		return attendees;
	}

	public void setAttendees(List<String> attendees) {
		this.attendees = attendees;
	}
	
	

	public Map<String, String> getAgendaItemUser() {
		return agendaItemUser;
	}

	public void setAgendaItemUser(Map<String, String> agendaItemUser) {
		this.agendaItemUser = agendaItemUser;
	}

	/*
	 * search a User by USER_ID
	 */
	public User indexOf(String userId){
		
		for(User user:this.getUsers()){
			if(user.getUserId().equals(userId)) return user;  
		}
		return null;
	}

	public Coordinate getMeetingCoordinate() {
		return meetingCoordinate;
	}

	public void setMeetingCoordinate(Coordinate meetingCoordinate) {
		this.meetingCoordinate = meetingCoordinate;
	}
	
	

}
