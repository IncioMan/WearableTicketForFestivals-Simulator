package com.group14.frontend;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.group14.common_interface.MessageSimulationPayload;

public class UINotifier {
	private Session session;
	private MessageConsumer messageConsumer;
	private ObjectMapper mapper;
	private List<Consumer<MessageSimulationPayload>> payloadConsumers;

	public UINotifier() {
		mapper = new ObjectMapper();
		payloadConsumers = new ArrayList<>();
	}

	public void createQueue(String clientId, String queueName) throws JMSException {
		System.setProperty("org.apache.activemq.SERIALIZABLE_PACKAGES", "*");
		// create a Connection Factory
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
				ActiveMQConnection.DEFAULT_BROKER_URL);
		connectionFactory.setTrustAllPackages(true);

		// create a Connection
		Connection connection = connectionFactory.createConnection();
		connection.setClientID(clientId);

		// create a Session
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		// create the Queue to which messages will be sent
		Queue queue = session.createQueue(queueName);

		// create a MessageProducer for sending messages
		messageConsumer = session.createConsumer(queue);

		connection.start();
	}

	public void onMessage(MessageListener listener) {
		try {
			messageConsumer.setMessageListener(listener);
		} catch (JMSException e) {

			e.printStackTrace();
		}
	}

	public void connect(String clientId, String queueName, Consumer<MessageSimulationPayload> payloadConsumer) {
		try {
			// create a Connection Factory
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);

			// create a Connection
			Connection connection = connectionFactory.createConnection();
			connection.setClientID(clientId);

			// create a Session
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			// create the Queue to which messages will be sent
			Queue queue = session.createQueue(queueName);

			// create a MessageProducer for sending messages
			messageConsumer = session.createConsumer(queue);

			connection.start();

			messageConsumer.setMessageListener(this::processMessage);

			payloadConsumers.add(payloadConsumer);
		} catch (JMSException e) {

			e.printStackTrace();
		}
	}

	public void processMessage(Message message) {
		if (!(message instanceof TextMessage)) {
			return;
		}

		MessageSimulationPayload payload;
		try {
			payload = mapper.readValue(((TextMessage) message).getText(), MessageSimulationPayload.class);
			payloadConsumers.forEach(c -> {
				c.accept(payload);
			});
			Thread.sleep(100);
		} catch (JsonParseException e) {

			e.printStackTrace();
		} catch (JsonMappingException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		} catch (Exception e) {

			e.printStackTrace();
		}
	}
}
