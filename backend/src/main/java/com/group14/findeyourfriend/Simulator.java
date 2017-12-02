package com.group14.findeyourfriend;

import java.io.IOException;
import java.io.InputStream;
import java.util.Queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.group14.findeyourfriend.debug.DebugLog;
import com.group14.findeyourfriend.simulation.Simulation;
import com.group14.findeyourfriend.simulation.StatisticsCalculator;
import com.group14.findeyourfriend.simulation.events.Event;
import com.group14.findeyourfriend.simulation.events.EventParser;

@SpringBootApplication
@EnableScheduling
public class Simulator {

	@Autowired
	private Simulation simulation;

	@Autowired
	private StatisticsCalculator calculator;

	public static void main(String[] args) {
		DebugLog.setEnabled(true);
		DebugLog.setEnabledTimers(true);
		SpringApplication.run(Simulator.class, args);
		// Simulator simulator = ctx.getBean(Simulator.class);
		// simulator.start();
	}

	@Scheduled(fixedDelay = 15000)
	public void start() {
		try {
			InputStream eventStream = Simulator.class.getClassLoader().getResourceAsStream("events.txt");
			//InputStream paramStream = Simulator.class.getClassLoader().getResourceAsStream("params.txt");
			InputStream paramStream = Simulator.class.getClassLoader().getResourceAsStream("SRparams.txt");
			Queue<Event> events = EventParser.parse(eventStream);
			Queue<Parameters> params = ParameterParser.parse(paramStream, true);
			simulation.add(calculator::calculate);
			simulation.init(events, true, 86400000);// Simulate 1 days in ms
			simulation.run(params.poll());
			// Chart.main(new String[0]);// Show chart
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}