package com.group14.findeyourfriend;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.group14.common_interface.Position;
import com.group14.common_interface.Vector2;
import com.group14.findeyourfriend.debug.DebugLog;

@Component
public class Simulation {
	private int clock;
	private java.util.Map<Integer, Person> guests;
	private HashMap<Integer, ArrayList<Event>> timeEvents;
	private Map map;
	private Battery battery;
	private Radio radio;
	private int simulationTime;
	private Broker broker = new Broker();

	@Autowired
	private Notifier notifier;

	public Simulation() {
		// TODO Auto-generated constructor stub
	}

	public void init(Queue<Event> events, int simulationTime) {
		// notifier = new Notifier("producer", IConstants.QUEUE_NAME);
		clock = 0;
		timeEvents = new HashMap<>();
		while (!events.isEmpty()) {
			Event nextEvent = events.poll();
			nextEvent.setSimulation(this);

			if (timeEvents.containsKey(nextEvent.getStart())) {
				timeEvents.get(nextEvent.getStart()).add(nextEvent);
			} else {
				ArrayList<Event> eventArrayList = new ArrayList<>();
				eventArrayList.add(nextEvent);
				timeEvents.put(nextEvent.getStart(), eventArrayList);
			}
		}
		this.simulationTime = simulationTime;
		guests = new HashMap<>();
		map = new Map(Constants.MAX_HEIGHT, Constants.MAX_WIDTH);
		map.setSimulation(this);
	}

	public void run() {

		// TODO implement parameter passing with Simulator
		battery = new Battery(10000);
		radio = new Radio(1000, 0.01, 7.0, 5);

		while (clock <= simulationTime) {
			ArrayList<Event> events = timeEvents.getOrDefault(clock, new ArrayList<>());
			for (Event e : events) {
				e.process();
			}
			for (Person person : guests.values())
				person.getBracelet().transition(clock);

			// System.out.println(map.AllInBound());

			if (clock % 1000 == 0) {
				// Map.clrscr();
				// map.Print();
				for (Person person : guests.values())
					person.UpdatePosition();
				notifier.notify(guests.values());
				System.out.println("UI Notified");
				try {
					Thread.sleep(10);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			clock++;

			// try {
			// Thread.sleep(1000);
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }

		}
		DebugLog.log("All events processed or simulation time has run out. Simulation finished.");
	}

	public void newGuestsArrived(List<Person> newGuests) {
		for (Person guest : newGuests) {
			Bracelet bracelet = new Bracelet(battery, radio, guest);
			bracelet.Subscribe(broker);
			guest.setBracelet(bracelet);

			guest.setPosition(
					new Position(ThreadLocalRandom.current().nextInt(Constants.MIN_WIDTH, Constants.MAX_WIDTH),
							ThreadLocalRandom.current().nextInt(Constants.MIN_HEIGHT, Constants.MAX_HEIGHT)));

			int randomX = ThreadLocalRandom.current().nextInt(-50, 50 + 10);
			int randomY = ThreadLocalRandom.current().nextInt(-50, 5 + 10);
			float x = (float) randomX / 10;
			float y = (float) randomY / 10;
			guest.setAcceleration(new Vector2(x, y));

			guests.put(guest.getId(), guest);
		}
	}

	public Collection<Person> getGuests() {
		return guests.values();
	}

	public Person getPersonById(int id) {
		return guests.get(id);
	}

}
