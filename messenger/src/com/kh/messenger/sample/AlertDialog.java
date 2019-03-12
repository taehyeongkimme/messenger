package com.kh.messenger.sample;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AlertDialog {
	public static void display(String title, String message) {
		Stage window = new Stage();

		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle(title);
		window.setMinWidth(250);

		Label label = new Label();
		label.setText(message);
		label.setStyle("-fx-font-size: 16px;"
//				+"-fx-font-style:italic;"
//				+"-fx-font-weight:6;"
		);
		Button button = new Button("닫기");
		button.setOnAction(e -> window.close());
		button.setStyle("-fx-background-color:lightgreen;" + "-fx-border-radius:5;" + "-fx-border-insets:5;"
				+ "-fx-border-color:bule;" + "-fx-border-weight:2;");

		VBox layout = new VBox(50);
		layout.getChildren().add(label);
		layout.getChildren().add(button);
		layout.setAlignment(Pos.CENTER);
		layout.setStyle("-fx-background-color:yellow;");

		Scene scene = new Scene(layout, 200, 150);
		window.setScene(scene);
		window.showAndWait();
	}
}
