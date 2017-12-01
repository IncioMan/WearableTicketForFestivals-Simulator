package com.group14.findeyourfriend.simulation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

import com.group14.findeyourfriend.bracelet.*;
import com.group14.findeyourfriend.message.Broker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.group14.common_interface.Position;
import com.group14.common_interface.Vector2;
import com.group14.findeyourfriend.Constants;
import com.group14.findeyourfriend.Notifier;
import com.group14.findeyourfriend.Parameters;
import com.group14.findeyourfriend.debug.DebugLog;

@Component
public class Simulation {
	private int clock;
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

	public void init(Queue<Event> events, boolean SRSimulation, int simulationTime) {
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
		this.SRSimulation = SRSimulation;
		guests = new HashMap<>();
		map = new Map(Constants.MAX_HEIGHT, Constants.MAX_WIDTH);
		map.setSimulation(this);
	}

	public void run(Parameters parameters) {
		this.radio = parameters.radio;
		this.cpu = parameters.cpu;
		// TODO implement parameter passing with Simulator
		battery = new Battery(225); // Coincell battery
		// radio = new Radio(1000, 0.01, 7.0, 5);

		while (clock <= simulationTime) {
			ArrayList<Event> events = timeEvents.getOrDefault(clock, new ArrayList<>());
			for (Event e : events) {
				e.process();
			}
			for (Person person : guests.values())
				person.getBracelet().transition(clock);

			if (clock % 500 == 0) {
				for (Person person : guests.values())
					person.UpdatePosition();
				if (notifier != null) {
					notifier.notify(guests.values());
				}
			}
			if(clock % 3 == 0){
				guestsConsumers.forEach(c -> {
					c.accept(guests.values());
				});

			}
			clock++;
		}
		DebugLog.log("All events processed or simulation time has run out. Simulation finished.");
	}

	public void newGuestsArrived(List<Person> newGuests) {
        for (Person guest : newGuests) {
            Bracelet bracelet;
            if(SRSimulation)
                bracelet = new SRBracelet(new Battery(battery.getCapacity_mAh()), radio, cpu, guest);
            else
                bracelet = new Bracelet(new Battery(battery.getCapacity_mAh()), radio, cpu, guest);
            bracelet.Subscribe(broker);
			guest.setBracelet(bracelet);

			guest.setPosition(
					new Position(ThreadLocalRandom.current().nextInt(Constants.MIN_WIDTH, Constants.MAX_WIDTH),
							ThreadLocalRandom.current().nextInt(Constants.MIN_HEIGHT, Constants.MAX_HEIGHT)));

			int randomX = ThreadLocalRandom.current().nextInt(-50, 50 + 10);
			int randomY = ThreadLocalRandom.current().nextInt(-50, 5 + 10);
			float x = (float) randomX / 10;
			float y = (float) randomY / 10;
			guest.setSpeed(Vector2.Normalize(new Vector2(x, y)));

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

}
