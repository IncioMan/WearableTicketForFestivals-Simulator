package com.group14.findeyourfriend.simulation;

import java.io.IOException;
import java.io.InputStream;
import java.util.Queue;

import com.group14.findeyourfriend.ParameterParser;
import com.group14.findeyourfriend.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.group14.findeyourfriend.debug.DebugLog;

@SpringBootApplication
@EnableScheduling
public class Simulator {

	@Autowired
	private Simulation simulation;

	@Autowired
	private StatisticsCalculator calculator;

	public static void main(String[] args) {
		DebugLog.setEnabled(true);
		DebugLog.setEnabledTimers(false);
		SpringApplication.run(Simulator.class, args);
		// Simulator simulator = ctx.getBean(Simulator.class);
		// simulator.start();
	}

	@Scheduled(fixedDelay = 15000)
	public void start() {
		try {
			InputStream eventStream = Simulator.class.getClassLoader().getResourceAsStream("events.txt");
			InputStream paramStream = Simulator.class.getClassLoader().getResourceAsStream("params.txt");
			Queue<Event> events = EventParser.parse(eventStream);
			Queue<Parameters> params = ParameterParser.parse(paramStream);
			simulation.init(events, 86400000);// Simulate 1 days in ms
			simulation.run(params.poll());
//			Chart.main(new String[0]);// Show chart
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}