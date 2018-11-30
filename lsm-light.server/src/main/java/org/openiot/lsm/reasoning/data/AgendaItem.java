package org.openiot.lsm.reasoning.data;

public class AgendaItem {
	String agendaItemId;
	String agendaItemTitle;
	long agendaItemTime;
	int agendaItemOrder;
	String agendaItemProperty; // sensitive or non_sensitive
	
	
	public AgendaItem() {
		super();
	}


	public AgendaItem(String agendaItemId) {
		super();
		this.agendaItemId = agendaItemId;
	}


	public String getAgendaItemId() {
		return agendaItemId;
	}


	public void setAgendaItemId(String agendaItemId) {
		this.agendaItemId = agendaItemId;
	}


	public String getAgendaItemTitle() {
		return agendaItemTitle;
	}


	public void setAgendaItemTitle(String agendaItemTitle) {
		this.agendaItemTitle = agendaItemTitle;
	}


	public long getAgendaItemTime() {
		return agendaItemTime;
	}


	public void setAgendaItemTime(long agendaItemTime) {
		this.agendaItemTime = agendaItemTime;
	}


	public int getAgendaItemOrder() {
		return agendaItemOrder;
	}


	public void setAgendaItemOrder(int agendaItemOrder) {
		this.agendaItemOrder = agendaItemOrder;
	}
	
	

}
