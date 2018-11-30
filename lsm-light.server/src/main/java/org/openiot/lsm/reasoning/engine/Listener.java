package org.openiot.lsm.reasoning.engine;

import javax.websocket.Session;

interface Listener extends java.util.EventListener{
	public void update(Event e);
	public void update(Event e, Session s);
}
