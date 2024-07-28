module ProjectMOM_Client {
	requires javafx.controls;
	requires javafx.base;
	requires javafx.fxml;
	requires activemq.all;
	
	opens application to javafx.graphics, javafx.fxml;
}
