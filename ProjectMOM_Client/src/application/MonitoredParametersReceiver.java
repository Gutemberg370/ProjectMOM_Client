package application;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

// Classe responsável por receber a lista de parâmetros disponíveis para assinatura
public class MonitoredParametersReceiver implements MessageListener {
	
    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;

    private static String topicName = "MonitoredParameters";
    
    public Main main;
    
    public MonitoredParametersReceiver(Main main) {
    	this.main = main;
    }

	public static void updateParametersOptions(Main main) throws JMSException {
    	new MonitoredParametersReceiver(main).go();
    }

    public void go() throws JMSException {

    	try {
    		
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);

            Topic topic = session.createTopic(topicName);

            MessageConsumer consumer = session.createConsumer(topic);

            consumer.setMessageListener(this);
            
    	}catch(Exception e) {
    		e.printStackTrace();
    	}

        
    }

	@Override
	public void onMessage(Message message) {
		// Quando a lista de parâmetros chega, se é atualizado o comboBox de inscrição do cliente com as opções
		if (message instanceof ObjectMessage) {
			Object object;
			try {
				object = ((ObjectMessage) message).getObject();
				String[] request = (String[]) object;
				ObservableList<String> listOfParametersToMonitor = FXCollections.observableArrayList(request);
		        Runnable updateComboBox = () -> {
		            Platform.runLater(() -> {
		            	listOfParametersToMonitor.forEach((item) -> 
    					{
     					   if(!this.main.parametersToMonitor.contains(item)) {
     						  this.main.parametersToMonitor.add(item);
      					   }
     						
     					});
		            	this.main.chooseParameter.setItems(this.main.parametersToMonitor);
		            	//this.main.callMonitoredParametersReceiver();
		            	
		            });
		        };
		        Thread updateComboBoxThread = new Thread(updateComboBox);
		        updateComboBoxThread.setDaemon(true);
		        updateComboBoxThread.start();
				
				
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
	}

}
