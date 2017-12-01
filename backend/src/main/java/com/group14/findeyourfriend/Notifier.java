package com.group14.findeyourfriend;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group14.common_interface.PersonDto;
import com.group14.findeyourfriend.bracelet.Person;
import com.group14.findeyourfriend.simulation.events.ConcertEvent;

@RestController
public class Notifier {

	private DtoConverter converter;
	private List<PersonDto> guests;
	private List<ConcertEvent> concertEvents;

	public Notifier() {
		converter = new DtoConverter();
		guests = new ArrayList<>();
	}

	public void notify(Collection<Person> values) {
		if (values == null) {
			return;
		}

		List<PersonDto> dtos = new ArrayList<>();
		values.forEach(v -> {
			dtos.add(converter.convert(v));
		});

		guests = dtos;
	}

	@RequestMapping("/guests")
	@CrossOrigin
	public List<PersonDto> getGuests() {
		return guests;
	}

	@RequestMapping("/")
	public String index() {
		return "Greetings from Spring Boot!";
	}

	public void notifyConcertEvents(List<ConcertEvent> concertEvents) {
		this.concertEvents = concertEvents;
	}

	@RequestMapping("/concerts")
	@CrossOrigin
	public List<ConcertEvent> getConcertEvents() {
		return concertEvents;
	}
	// private MessageProducer messageProducer;
	// private DtoConverter converter;
	// private Session session;
	// private ObjectMapper mapper;
	//
	//
	// public Notifier(String clientId, String queueName) {
	// this();
	// try {
	// createQueue(clientId, queueName);
	// } catch (JMSException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//
	// public void createQueue(String clientId, String queueName) throws
	// JMSException {
	// // create a Connection Factory
	// ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
	// ActiveMQConnection.DEFAULT_BROKER_URL);
	// connectionFactory.setTrustAllPackages(true);
	//
	// // create a Connection
	// Connection connection = connectionFactory.createConnection();
	// connection.setClientID(clientId);
	//
	// // create a Session
	// session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	//
	// // create the Queue to which messages will be sent
	// javax.jms.Queue queue = session.createQueue(queueName);
	//
	// // create a MessageProducer for sending messages
	// messageProducer = session.createProducer(queue);
	// }

	// public void notify(Collection<Person> values) {
	// if (values == null) {
	// return;
	// }
	//
	// List<PersonDto> dtos = new ArrayList<>();
	// values.forEach(v -> {
	// dtos.add(converter.convert(v));
	// });
	//
	// MessageSimulationPayload payload = new MessageSimulationPayload();
	// payload.setPeople(dtos);
	//
	// try {
	// messageProducer.send(session.createTextMessage(mapper.writeValueAsString(payload)));
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// System.out.println("Notified UI with list of people");
	// }
}
