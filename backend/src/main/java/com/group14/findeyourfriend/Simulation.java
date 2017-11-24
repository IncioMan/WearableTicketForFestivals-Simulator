package com.group14.findeyourfriend;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import com.group14.findeyourfriend.debug.DebugLog;
import com.group14.findeyourfriend.math.Vector2;

public class Simulation {
	private int clock;
	private java.util.Map<Integer, Person> guests;
	private HashMap<Integer, ArrayList<Event>> timeEvents;
	private Map map;
	private Battery battery;
	private Radio radio;
	private int simulationTime;
	private Broker broker = new Broker();
	private Connection connection;
	private Session session;
	private MessageProducer messageProducer;

	public Simulation(Queue<Event> events, int simulationTime) throws JMSException {
		createQueue("producer", "pointtopoint.q");
		clock=0;
		timeEvents = new HashMap<>();
		while(!events.isEmpty()) {
			Event nextEvent = events.poll();
			nextEvent.setSimulation(this);

			if(timeEvents.containsKey(nextEvent.getStart())){
				timeEvents.get(nextEvent.getStart()).add(nextEvent);
			}else{
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

	public void createQueue(String clientId, String queueName) throws JMSException {
		// create a Connection Factory
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);

		// create a Connection
		connection = connectionFactory.createConnection();
		connection.setClientID(clientId);

		// create a Session
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		// create the Queue to which messages will be sent
		javax.jms.Queue queue = session.createQueue(queueName);

		// create a MessageProducer for sending messages
		messageProducer = session.createProducer(queue);

		// create a JMS TextMessage
		TextMessage textMessage = session.createTextMessage("ciao");
		messageProducer.send(textMessage);
	}

	public void run() {

		// TODO implement parameter passing with Simulator
		battery = new Battery(10000);
		radio = new Radio(1000, 0.01, 7.0, 5);

		while(clock <= simulationTime){
			ArrayList<Event> events = timeEvents.getOrDefault(clock, new ArrayList<>());
			for (Event e: events) {
				e.process();
			}
			for (Person person : guests.values())
				person.getBracelet().transition(clock);


//            System.out.println(map.AllInBound());

			if(clock % 1000 == 0){
//                Map.clrscr();
//                map.Print();
				for (Person person : guests.values())
					person.UpdatePosition();
			}
			clock++;

//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }


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

			int randomX = ThreadLocalRandom.current().nextInt(-5, 5 + 1);
			int randomY = ThreadLocalRandom.current().nextInt(-5, 5 + 1);
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
