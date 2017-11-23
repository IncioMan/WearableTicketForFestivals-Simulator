package com.group14.findeyourfriend;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Queue;

import javax.jms.JMSException;

import com.group14.findeyourfriend.debug.DebugLog;

public class Simulator {
	public static void main(String[] args) throws URISyntaxException, JMSException {
		DebugLog.setEnabled(true);
		try {
			InputStream stream = Simulator.class.getClassLoader().getResourceAsStream("events.txt");
			Queue<Event> events = EventParser.parse(stream);
			Simulation sim = new Simulation(events, 30);
			sim.run();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}