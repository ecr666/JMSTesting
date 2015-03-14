import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;
import javax.jms.ConnectionFactory;

/**
 * Created by eranda on 3/14/15.
 */
public class JMSProducer {

	public static final String NAMING_FACTORY_INITIAL = "java.naming.factory.initial";

	public static final String PROVIDER_URL = "java.naming.provider.url";

	public static final String TOPIC_PREFIX = "topic.";

	private static final String connectionFac = "QueueConnectionFactory";

	private static final String queueName = "JMSTesting";

	private static final String url = "tcp://localhost:61616";

	private static final String broker = "org.apache.activemq.jndi.ActiveMQInitialContextFactory";

	private ConnectionFactory connectionFactory;

	private Destination destination;

	private Context context;

	public static void main(String[] args) {
		JMSProducer prod = new JMSProducer();
		try {
			prod.simplePublish();
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public void classicPublish() throws NamingException, JMSException {
		Properties properties = new Properties();
		properties.put(NAMING_FACTORY_INITIAL,broker);
		properties.put(PROVIDER_URL, url);
		properties.put(TOPIC_PREFIX + queueName, queueName);

		context = new InitialContext(properties);

		destination = (Destination) context.lookup(queueName);

		connectionFactory = (ConnectionFactory) context.lookup(connectionFac);

		Connection connection;
		connection = connectionFactory.createConnection();

		try {
			connection.start();
			Session session = connection.createSession(false,
			                                           Session.AUTO_ACKNOWLEDGE);

			MessageProducer producer = session.createProducer(destination);

			TextMessage message = session.createTextMessage("Hello World!");

			producer.send(message);
			System.out.println("Sent message '" + message.getText() + "'");
		} finally {
			connection.close();
		}
	}

	public void simplePublish() throws NamingException, JMSException {
		Properties properties = new Properties();
		properties.put(NAMING_FACTORY_INITIAL,broker);
		properties.put(PROVIDER_URL, url);
		properties.put(TOPIC_PREFIX + queueName, queueName);

		context = new InitialContext(properties);

		destination = (Destination) context.lookup(queueName);

		connectionFactory = (ConnectionFactory) context.lookup(connectionFac);

		try (JMSContext jmsContext = connectionFactory.createContext()) {

			javax.jms.JMSProducer jmsProducer = jmsContext.createProducer();

			jmsProducer.send(destination, "Hello World!");
			System.out.println("Sent message '" + "Hello World!" + "'");
		}
	}

}
