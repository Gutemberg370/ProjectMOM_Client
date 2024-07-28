package application;

import java.util.ArrayList;

// Classe que representa o cliente da aplicação
public class Client {

	private String id;
	
	private ArrayList<String> subscribedParameters;

	public Client(String id, ArrayList<String> subscribedParameters) {
		super();
		this.id = id;
		this.subscribedParameters = subscribedParameters;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ArrayList<String> getSubscribedParameters() {
		return subscribedParameters;
	}

	public void addSubscribedParameter(String subscribedParameter) {
		if(!this.subscribedParameters.contains(subscribedParameter)) {
			this.subscribedParameters.add(subscribedParameter);
		}
	}
}
