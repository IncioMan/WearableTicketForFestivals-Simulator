package com.group14.frontend;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class UINotifier {
	private Session session;
	private MessageConsumer messageConsumer;

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void connect(String clientId, String queueName, MessageListener listener) {
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
			MessageConsumer messageConsumer = session.createConsumer(queue);

			connection.start();

			messageConsumer.setMessageListener(listener);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
