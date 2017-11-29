package com.group14.findeyourfriend;

import java.io.IOException;
import java.io.InputStream;
import java.util.Queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.group14.findeyourfriend.chart.Chart;
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
			InputStream stream = Simulator.class.getClassLoader().getResourceAsStream("events.txt");
			Queue<Event> events = EventParser.parse(stream);
			simulation.init(events, 864000000);// Simulate 10 days in ms
			simulation.add(calculator::calculate);
			simulation.run();
			Chart.main(new String[0]);// Show chart
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}