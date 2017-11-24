package com.group14.findeyourfriend;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Queue;

import javax.jms.JMSException;

import com.group14.findeyourfriend.chart.Chart;
import com.group14.findeyourfriend.debug.DebugLog;

public class Simulator
{
	public static void main(String[] args) {
		DebugLog.setEnabled(false);
		DebugLog.setEnabledTimers(false);
		try{
			Queue<Event> events = EventParser.parse("./com/findyourfriend/events.txt");
			Simulation sim = new Simulation(events, 864000000);//Simulate 10 days in ms
			sim.run();
			Chart.main(new String[0]);//Show chart
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}