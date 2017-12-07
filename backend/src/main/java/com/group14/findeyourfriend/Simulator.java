package com.group14.findeyourfriend;

import java.io.InputStream;
import java.util.Queue;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

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
		DebugLog.setEnabledTimers(false);
		SpringApplication.run(Simulator.class, args);
		// Simulator simulator = ctx.getBean(Simulator.class);
		// simulator.start();
	}

	@PostConstruct
	public void start() {
		new Thread(() -> {
			try {
				Thread.sleep(5000);
				boolean runSR = false;
				InputStream eventStream;
				InputStream paramStream;
				if (!runSR) {
					eventStream = Simulator.class.getClassLoader().getResourceAsStream("events.txt");
					paramStream = Simulator.class.getClassLoader().getResourceAsStream("params.txt");
				} else {
					eventStream = Simulator.class.getClassLoader().getResourceAsStream("events.txt");
					paramStream = Simulator.class.getClassLoader().getResourceAsStream("SRparams.txt");
				}
				Queue<Event> events = EventParser.parse(eventStream);
				Queue<Parameters> params = ParameterParser.parse(paramStream, runSR);
				simulation.add(calculator::calculate);
				simulation.addEventListener(calculator::braceletEvent);
				simulation.init(events, runSR, 3600000);// Simulate 1 hour in ms
				simulation.run(params.poll());
				// Chart.main(new String[0]);// Show chart
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}
}