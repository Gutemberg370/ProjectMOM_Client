package application;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

// Classe responsável por enviar a requisição de inscrição à um parâmetro
public class SubscriptionRequestSender {
	
	private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    
	private static String queueName = "SubscriptionLine";
	
	private String[] informationToSubscription = new String[2];
	
	public Main main;
	
	public SubscriptionRequestSender(Main main) {
		this.main = main;
	}

	// Enviar a solicitação de inscrição
	public void sendParameterSubscribed() throws JMSException {

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        Connection connection = connectionFactory.createConnection();
        connection.start();

        Session session = connection.createSession(false,
                Session.AUTO_ACKNOWLEDGE);

        Destination destination = session.createQueue(queueName);

        MessageProducer producer = session.createProducer(destination);
        
        informationToSubscription[0] = this.main.client.getId();
        informationToSubscription[1] = (String) this.main.chooseParameter.getValue();
        
        ObjectMessage objectMessage = session.createObjectMessage();
        objectMessage.setObject(informationToSubscription);

        producer.send(objectMessage);
      
        //System.out.println("Sent message from SUBSCRITIONREQUESTSENDER");

        connection.close();
    }

}
