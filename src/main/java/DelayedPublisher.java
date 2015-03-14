import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

/**
 * Created by eranda on 3/14/15.
 */
public class DelayedPublisher {
	public static final String NAMING_FACTORY_INITIAL = "java.naming.factory.initial";

	public static final String PROVIDER_URL = "java.naming.provider.url";

	public static final String TOPIC_PREFIX = "topic.";

	private static final String connectionFac = "QueueConnectionFactory";

	private static final String queueName = "JMSDelayTesting";

	private static final String url = "tcp://localhost:61616";

	//activeMQ
	//private static final String broker = "org.apache.activemq.jndi.ActiveMQInitialContextFactory";

	//hornetQ
	private static final String broker = "org.jboss.naming.remote.client.InitialContextFactory";

	private ConnectionFactory connectionFactory;

	private Destination destination;

	private Context context;

	public static void main(String[] args) {
		DelayedPublisher publisher = new DelayedPublisher();

		try {
			publisher.setup();
		} catch (NamingException e) {
			System.out.println("Error in setting up connection" + e);
		}

		try {
			publisher.classicPublish();
		} catch (JMSException e) {
			System.out.println("Error when publishing" + e);
		}
	}

	public void setup() throws NamingException {
		Properties properties = new Properties();
		properties.put(NAMING_FACTORY_INITIAL,broker);
		properties.put(PROVIDER_URL, url);
		properties.put(TOPIC_PREFIX + queueName, queueName);
		context = new InitialContext(properties);
		destination = (Destination) context.lookup(queueName);
		connectionFactory = (ConnectionFactory) context.lookup(connectionFac);
	}

	public void simplePublish(){
			// send a message with a delivery delay of 5 seconds
			try (JMSContext jmsContext = connectionFactory.createContext()){
				javax.jms.JMSProducer jmsProducer = jmsContext.createProducer();
				jmsProducer.setDeliveryDelay(5000);
				jmsProducer.send(destination, "Delayed Hello world");
			}

	}

	public void classicPublish() throws JMSException {
		Connection connection = connectionFactory.createConnection();

		try {
			connection.start();
			Session session = connection.createSession(false,
			                                           Session.AUTO_ACKNOWLEDGE);

			MessageProducer producer = session.createProducer(destination);

			//setting delivery delay
			producer.setDeliveryDelay(5000);

			TextMessage message = session.createTextMessage("Delayed Hello World!");

			producer.send(message);
			System.out.println("Sent message '" + message.getText() + "'");
		} finally {
			connection.close();
		}
	}


}
