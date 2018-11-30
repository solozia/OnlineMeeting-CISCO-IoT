package org.openiot.lsm.reasoning.data;

import java.util.Map;

public class JsonApplicationResult {
	String MEETING_ID;
	String USER_ID;
	Map<String, String> CAPABILITIES;
	
	
	
	
	public JsonApplicationResult() {
		super();
	}

	public JsonApplicationResult(String meetingId, String userId,
			Map<String, String> capabicities) {
		super();
		this.MEETING_ID = meetingId;
		this.USER_ID = userId;
		this.CAPABILITIES = capabicities;
	}

	public String getMeetingId() {
		return MEETING_ID;
	}

	public void setMeetingId(String meetingId) {
		this.MEETING_ID = meetingId;
	}

	public String getUserId() {
		return USER_ID;
	}

	public void setUserId(String userId) {
		this.USER_ID = userId;
	}

	public Map<String, String> getCapabicities() {
		return CAPABILITIES;
	}

	public void setCapabicities(Map<String, String> capabicities) {
		this.CAPABILITIES = capabicities;
	}
	
	
	
	
}
