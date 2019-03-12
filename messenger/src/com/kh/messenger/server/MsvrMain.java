package com.kh.messenger.server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MsvrMain extends Application{


	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		FXMLLoader loader = 
				new FXMLLoader(getClass().getResource("MSvrScr.fxml"));
		Parent parent = loader.load();
		
		MSvrCtr controller = loader.getController();
		controller.setPrimaryStage(primaryStage);
		
		Scene scene = new Scene(parent);
		
		primaryStage.setScene(scene);
		primaryStage.setTitle("[메신저 서버 모니터링]");
		primaryStage.show();
		
	}

}
