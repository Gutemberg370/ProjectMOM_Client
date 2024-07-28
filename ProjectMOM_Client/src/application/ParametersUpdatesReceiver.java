package application;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

// Classe responsável por receber as mensagens enviadas pelo Broker relacionadas aos parâmetros inscritos 
public class ParametersUpdatesReceiver {

	private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
	
	public Main main;
	
	public ParametersUpdatesReceiver(Main main) {
		this.main = main;
	}

	// Função que recebe as mensagens dos parâmetros inscritos
	public void receiveParametersUpdates() throws JMSException {

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        Connection connection = connectionFactory.createConnection();
        connection.start();

        Session session = connection.createSession(false,
                Session.AUTO_ACKNOWLEDGE);

        Destination destination = session.createQueue(this.main.client.getId());

        MessageConsumer consumer = session.createConsumer(destination);
        
        Message message = consumer.receive();
        
        // Recebe a mensagem e a coloca na área de mensagens
        if(message instanceof TextMessage) {
			try {
				this.main.messageArea.appendText(((TextMessage)message).getText() + "\n\n");
				// Criando um novo consumidor 
				this.main.callParametersUpdatesReceiver();
			}catch(Exception e) {
				
			}
		}

        consumer.close();
    }
}
