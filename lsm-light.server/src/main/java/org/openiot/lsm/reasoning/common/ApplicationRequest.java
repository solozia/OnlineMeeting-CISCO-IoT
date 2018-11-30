package org.openiot.lsm.reasoning.common;

/**
 * @author Thu-Le Pham
 *
 *         This class represents the request from Application
 */
public class ApplicationRequest {

	private String meetingId;
	private String userId;

	public ApplicationRequest() {
		super();
	}

	public ApplicationRequest(String meetingId, String userId) {
		super();
		this.meetingId = meetingId;
		this.userId = userId;
	}

	public String getMeetingId() {
		return meetingId;
	}

	public void setMeetingId(String meetingId) {
		this.meetingId = meetingId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}

