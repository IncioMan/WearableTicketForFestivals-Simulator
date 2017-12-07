package com.group14.findeyourfriend.simulation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.group14.common_interface.Position;
import com.group14.common_interface.Vector2;
import com.group14.findeyourfriend.Clock;
import com.group14.findeyourfriend.Constants;
import com.group14.findeyourfriend.Notifier;
import com.group14.findeyourfriend.Parameters;
import com.group14.findeyourfriend.bracelet.Battery;
import com.group14.findeyourfriend.bracelet.Bracelet;
import com.group14.findeyourfriend.bracelet.BraceletEvent;
import com.group14.findeyourfriend.bracelet.CPU;
import com.group14.findeyourfriend.bracelet.Person;
import com.group14.findeyourfriend.bracelet.Radio;
import com.group14.findeyourfriend.bracelet.SRBracelet;
import com.group14.findeyourfriend.debug.DebugLog;
import com.group14.findeyourfriend.message.Broker;
import com.group14.findeyourfriend.simulation.events.ConcertEvent;
import com.group14.findeyourfriend.simulation.events.Event;

@Component
public class Simulation {
	private java.util.Map<Integer, Person> guests;
	private HashMap<Integer, ArrayList<Event>> timeEvents;
	private Map map;
	private Battery battery;
	private Radio radio;
	private CPU cpu;
	private int simulationTime;
	private Broker broker = new Broker();

	@Autowired
	private Notifier notifier;
	private List<Consumer<Collection<Person>>> guestsConsumers;
	private boolean SRSimulation;

	public Simulation() {
		guestsConsumers = new ArrayList<>();
	}

	public void init(Queue<Event> events, boolean SRSimulation, int simulationTime) {// notifier = new
																						// Notifier("producer",
		// IConstants.QUEUE_NAME);
		Clock.resetClock();
		timeEvents = new HashMap<>();
		List<ConcertEvent> concertEvents = new ArrayList<>();

		while (!events.isEmpty()) {
			Event nextEvent = events.poll();
			nextEvent.setSimulation(this);

			if (nextEvent instanceof ConcertEvent) {
				concertEvents.add((ConcertEvent) nextEvent);
			}

			if (timeEvents.containsKey(nextEvent.getStart())) {
				timeEvents.get(nextEvent.getStart()).add(nextEvent);
			} else {
				ArrayList<Event> eventArrayList = new ArrayList<>();
				eventArrayList.add(nextEvent);
				timeEvents.put(nextEvent.getStart(), eventArrayList);
			}
		}
		this.simulationTime = simulationTime;
		this.SRSimulation = SRSimulation;
		if (notifier != null)
			notifier.notifyConcertEvents(concertEvents);
		guests = new HashMap<>();
		map = new Map(Constants.MAX_HEIGHT, Constants.MAX_WIDTH);
		map.setSimulation(this);
	}

	public void run(Parameters parameters) {
		this.radio = parameters.radio;
		this.cpu = parameters.cpu;
		battery = new Battery(225); // Coincell battery
		// radio = new Radio(1000, 0.01, 7.0, 5);

		while (Clock.getClock() <= simulationTime) {
			for (Person person : guests.values())
				person.getBracelet().transition(Clock.getClock().intValue());

			ArrayList<Event> events = timeEvents.getOrDefault(Clock.getClock().intValue(), new ArrayList<>());
			for (Event e : events) {
				e.process();
				if (notifier != null)
					notifier.addEvent(e);
			}

			if (Clock.getClock() % 500 == 0) {
				for (Person person : guests.values())
					person.UpdatePosition();
				if (notifier != null) {
					notifier.notify(guests.values());
				}
			}

			if (Clock.getClock() % 100 == 0) {
				// StopWatch stopWatch = new StopWatch();
				// stopWatch.start();
				guestsConsumers.forEach(c -> {
					c.accept(guests.values());
				});
				// stopWatch.stop();
				// System.out.println(stopWatch.getTime());
			}
			Clock.incrementClock();
		}
		DebugLog.log("All events processed or simulation time has run out. Simulation finished.");
	}

	public void newGuestsArrived(List<Person> newGuests) {
		for (Person guest : newGuests) {
			guest.setPosition(
					new Position(ThreadLocalRandom.current().nextInt(Constants.MIN_WIDTH, Constants.MAX_WIDTH),
							ThreadLocalRandom.current().nextInt(Constants.MIN_HEIGHT, Constants.MAX_HEIGHT)));

			guest.setSpeed(Vector2.getRandomVector());

			Bracelet bracelet;
			if (SRSimulation)
				bracelet = new SRBracelet(new Battery(battery.getCapacity_mAh()), radio, cpu, guest);
			else
				bracelet = new Bracelet(new Battery(battery.getCapacity_mAh()), radio, cpu, guest);
			bracelet.Subscribe(broker);
			guest.setBracelet(bracelet);

			DebugLog.log("ArrivalEvent: guest " + guest.getId() + " arrived");

			guests.put(guest.getId(), guest);
		}
	}

	public Collection<Person> getGuests() {
		return guests.values();
	}

	public Person getPersonById(int id) {
		return guests.get(id);
	}

	public void add(Consumer<Collection<Person>> consumer) {
		guestsConsumers.add(consumer);
	}

	public void addEventListener(Consumer<Pair<Person, BraceletEvent>> consumer) {
		broker.addEventConsumer(consumer);
	}

}
