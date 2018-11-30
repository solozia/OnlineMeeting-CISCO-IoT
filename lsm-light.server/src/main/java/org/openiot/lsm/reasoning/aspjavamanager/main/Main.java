package org.openiot.lsm.reasoning.aspjavamanager.main;

import java.io.IOException;

import org.openiot.lsm.reasoning.aspjavamanager.manager.AspManager;

public class Main {
	public static void main(final String[] args) throws IOException, CloneNotSupportedException {
		final AspManager manager = new AspManager();
		System.out.println("Start");
		manager.callClingo("b|c:-a.a.");
		System.out.println("End");


	}
}
