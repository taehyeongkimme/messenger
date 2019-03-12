package com.kh.messenger.common;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AlertDialog {
	public static void display(String title, String message) {
		Stage window = new Stage();

		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle(title);
		window.setMinWidth(250);
		window.getIcons().add(new Image(AlertDialog.class.getResource("images/noti.png").toString()));

		Label label = new Label();
		label.setText(message);
		label.setTextAlignment(TextAlignment.CENTER);
		label.setStyle("-fx-font-size: 14px;"
				+"-fx-font-family:'나눔고딕';-fx-font-weight:bold;"
		);
		Button button = new Button("닫기");
		button.setOnAction(e -> window.close());

		VBox layout = new VBox(50);
		layout.getChildren().add(label);
		layout.getChildren().add(button);
		layout.setAlignment(Pos.CENTER);
		layout.setStyle("-fx-background-color: #d9e1e8;");

		Scene scene = new Scene(layout, 300, 150);
		scene.getStylesheets().add(AlertDialog.class.getResource("messenger.css").toString());
		window.setScene(scene);
		window.showAndWait();
	}
}
