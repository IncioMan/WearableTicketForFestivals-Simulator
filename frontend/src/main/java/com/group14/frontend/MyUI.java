package com.group14.frontend;

import java.util.Random;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import javax.servlet.annotation.WebServlet;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * This UI is the application entry point. A UI may either represent a browser
 * window (or tab) or some part of a html page where a Vaadin application is
 * embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is
 * intended to be overridden to add component to the user interface and
 * initialize non-component functionality.
 */
@Theme("mytheme")
@Push
public class MyUI extends UI implements MessageListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int GRID_WIDTH = 120;
	private static final int GRID_HEIGHT = 60;
	private GridLayout gridLayout;
	private PersonDetailLayout detailLayout;
	private Connection connection;
	private Session session;
	private MessageConsumer messageConsumer;

	@Override
	protected void init(VaadinRequest request) {
		VerticalLayout content = new VerticalLayout();
		content.setMargin(true);
		setContent(content);

		content.addComponent(buildFirstLayer());
		content.setSizeFull();

//		try {
//			listenToEvents("consumer", "pointtopoint.q");
//		} catch (JMSException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	private void listenToEvents(String clientId, String queueName) throws JMSException {
		// create a Connection Factory
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);

		// create a Connection
		connection = connectionFactory.createConnection();
		connection.setClientID(clientId);

		// create a Session
		session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

		// create the Queue from which messages will be received
		Queue queue = session.createQueue(queueName);

		// create a MessageConsumer for receiving messages
		messageConsumer = session.createConsumer(queue);
		messageConsumer.setMessageListener(this);
		// start the connection in order to receive messages
		connection.start();
	}

	private Component buildFirstLayer() {
		HorizontalLayout layout = new HorizontalLayout();
		// Instantiate the component and add it to your UI
		layout.addComponent(gridLayout = new GridLayout(GRID_WIDTH, GRID_HEIGHT));
		gridLayout.addStyleName(ICustomStyles.BORDER);
		gridLayout.addStyleName(ICustomStyles.FIELD);
		gridLayout.setWidth(GRID_WIDTH * 10 + "px");
		gridLayout.setHeight(GRID_HEIGHT * 10 + "px");
		// layout.addStyleName(ValoTheme.BORDE);

		layout.addComponent(detailLayout = new PersonDetailLayout());

		layout.setExpandRatio(gridLayout, 3.0f);
		layout.setExpandRatio(detailLayout, 1.0f);

		// Draw a 20x20 filled rectangle with the upper left corner
		// in coordinate 10,10. It will be filled with the default
		// color which is black.
		new Thread(() -> {
			while (true) {
				gridLayout.removeAllComponents();
				UI.getCurrent().access(() -> {
					for (int i = 0; i < 10; i++) {
						int x = new Random().nextInt(GRID_WIDTH);
						int y = new Random().nextInt(GRID_HEIGHT);
						Person person = new Person();
						person.setPersonId(i + "");
						person.setSearching(new Random().nextBoolean());
						gridLayout.addComponent(person, x, y);
						person.addLayoutClickListener(this::personClicked);
					}
				});
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();

		layout.setSizeFull();
		return layout;
	}

	private void personClicked(LayoutClickEvent event) {
		if (event.getComponent() instanceof Person) {
			detailLayout.updateDetails((Person) event.getComponent());
		}
	}

	@WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
	@VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
	public static class MyUIServlet extends VaadinServlet {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
	}

	@Override
	public void onMessage(Message message) {
		System.out.println(message);
	}
}
