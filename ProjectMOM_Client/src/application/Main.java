package application;
	
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.JMSException;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;


public class Main extends Application {
	
	public TextArea messageArea = new TextArea();
	
	public Client client = new Client((Long.toString(System.currentTimeMillis()/1000)),new ArrayList<String>());
	
	public ObservableList<String> parametersToMonitor = FXCollections.observableArrayList();
	
	private MonitoredParametersReceiver monitoredParametersReceiver = new MonitoredParametersReceiver(this);
	
	private SubscriptionRequestSender subscriptionRequestSender = new SubscriptionRequestSender(this);
	
	private ParametersUpdatesReceiver parametersUpdatesReceiver = new ParametersUpdatesReceiver(this);
	
	// Serviço responsável por executar uma thread mais de uma vez
	final ExecutorService service = Executors.newCachedThreadPool();
	
	@FXML ComboBox chooseParameter = new ComboBox();
	
	Label subscribedParameters;
	
	// Thread responsável por receber os parâmetros monitorados em que o cliente pode se inscrever
	final class MonitoredParametersConsumer implements Runnable {
	    @Override
	    public void run() {
        	try {
        		monitoredParametersReceiver.updateParametersOptions(Main.this);
			} catch (JMSException e) {
				e.printStackTrace();
			}	

	    }
	}; 
	
	// Thread responsável por receber as atualizações dos parâmetros que o cliente está inscrito
	final class ParametersUpdatesConsumer implements Runnable {
	    @Override
	    public void run() {
        	try {
        		parametersUpdatesReceiver.receiveParametersUpdates();
			} catch (JMSException e) {
				e.printStackTrace();
			}	

	    }
	};
	
	public void callMonitoredParametersReceiver() {
		service.submit(new MonitoredParametersConsumer());
	}
	
	public void callParametersUpdatesReceiver() {
		service.submit(new ParametersUpdatesConsumer());
	}
	
	// Função que cria a página do cliente
	private Parent createClientPage() {
		
    	Pane root = new Pane();
    	
    	BackgroundFill backgroundFill = new BackgroundFill(Color.valueOf("#ADD8E6"), new CornerRadii(10), new Insets(10));

    	Background background = new Background(backgroundFill);
    	
    	root.setBackground(background);
    	
    	root.setPrefSize(544, 492);
    	           
    	
    	Label client = new Label("CLIENTE");
    	client.setFont(new Font("Monaco",36));
    	client.setLayoutX(210);
    	client.setLayoutY(35);
    	client.setTextAlignment(TextAlignment.CENTER);
    	
    	Label parameterLabel = new Label("Parâmetro a se inscrever  :");
    	parameterLabel.setFont(new Font("Arial",13));
    	parameterLabel.setLayoutX(55);
    	parameterLabel.setLayoutY(110);
    	
    	chooseParameter.setItems(parametersToMonitor);
    	chooseParameter.setLayoutX(220);
    	chooseParameter.setLayoutY(105);
    	chooseParameter.setMinWidth(220);
    	
    	Label subscribedParametersLabel = new Label("Parâmetros inscritos :");
    	subscribedParametersLabel.setFont(new Font("Arial",13));
    	subscribedParametersLabel.setLayoutX(55);
    	subscribedParametersLabel.setLayoutY(150);
    	
    	subscribedParameters = new Label();
    	subscribedParameters.setFont(new Font("Arial",15));
    	subscribedParameters.setLayoutX(190);
    	subscribedParameters.setLayoutY(149);
    	
    	Label receivedMessagesLabel = new Label("Mensagens recebidas do Broker");
    	receivedMessagesLabel.setFont(new Font("Monaco",24));
    	receivedMessagesLabel.setLayoutX(100);
    	receivedMessagesLabel.setLayoutY(180);
    	
    	Button sendSubscriptionButton = new Button("Registrar inscrição");
    	sendSubscriptionButton.setLayoutX(190);
    	sendSubscriptionButton.setLayoutY(440);
    	sendSubscriptionButton.setMinWidth(150);
    	sendSubscriptionButton.setOnAction(event -> {
    		// Se o usuário tiver escolhido um parâmetro para se inscrever, envie a solicitação de inscrição
    		// para o Broker e atualize a label de parâmetros inscritos do cliente
    		if(chooseParameter.getValue() != null && !this.client.getSubscribedParameters().contains(chooseParameter.getValue())) {
        		this.client.addSubscribedParameter((String) chooseParameter.getValue());
        		this.subscribedParameters.setText(String.join(", ", this.client.getSubscribedParameters()));
        		try {
    				subscriptionRequestSender.sendParameterSubscribed();
    			} catch (JMSException e) {
    				e.printStackTrace();
    			}
    		}
        });
    	
    	this.messageArea.setWrapText(true);
    	this.messageArea.setStyle("-fx-font-size: 15;");
        this.messageArea.setPrefHeight(200);
        this.messageArea.setPrefWidth(470);
        this.messageArea.setEditable(false);      
     		
        VBox chatBox = new VBox(0, this.messageArea);
        chatBox.setLayoutX(40);
        chatBox.setLayoutY(220);
  	
    	root.getChildren().addAll(parameterLabel, client, chooseParameter, subscribedParametersLabel, subscribedParameters, receivedMessagesLabel, sendSubscriptionButton, chatBox);
    	
    	return root;
	}
	
	@Override
	public void start(Stage primaryStage) {
		try {
			Scene clientPage = new Scene(createClientPage());
			primaryStage.setTitle("Cliente");;
			primaryStage.setScene(clientPage);
			primaryStage.show();
	    	callMonitoredParametersReceiver();
	    	callParametersUpdatesReceiver();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
