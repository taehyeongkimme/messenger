package com.kh.messenger.common;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ConfirmDialog {
	static boolean answer;
	public static boolean display(String title, String message) {
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle(title);
		window.getIcons().add(new Image(ConfirmDialog.class.getResource("images/noti.png").toString()));
		window.setMaxWidth(250);
		
		Label label = new Label();
		label.setText(message);
		label.setTextAlignment(TextAlignment.CENTER);
		

		Button yesBtn = new Button("확인");
		Button noBtn = new Button("취소");
		
		yesBtn.setOnAction(e->{
			answer = true;
			window.close();
		});
		
		noBtn.setOnAction(e->{
			answer = false;
			window.close();
		});
		
		
		VBox layout = new VBox(20);
		HBox hbox = new HBox(10);
		
		hbox.getChildren().addAll(yesBtn, noBtn);
		layout.getChildren().addAll(label,hbox);
		layout.setAlignment(Pos.CENTER);
		hbox.setAlignment(Pos.CENTER);
		hbox.setSpacing(30);
		
		
		Scene scene = new Scene(layout,200,150);
		scene.getStylesheets().add(ConfirmDialog.class.getResource("messenger.css").toString());
		window.setScene(scene);
		window.showAndWait();
		
		return answer;
	}
	public static boolean display2(String title, String message) {
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle(title);
		window.getIcons().add(new Image(ConfirmDialog.class.getResource("images/noti.png").toString()));
		window.setMaxWidth(250);
		
		Label label = new Label();
		label.setText(message);
		label.setTextAlignment(TextAlignment.CENTER);
		
		Button yesBtn = new Button("확인");
		
		yesBtn.setOnAction(e->{
			answer = true;
			window.close();
		});
		
		VBox layout = new VBox(20);
		HBox hbox = new HBox(10);
		
		hbox.getChildren().addAll(yesBtn);
		layout.getChildren().addAll(label,hbox);
		layout.setAlignment(Pos.CENTER);
		hbox.setAlignment(Pos.CENTER);
		
		
		Scene scene = new Scene(layout,200,150);
		scene.getStylesheets().add(ConfirmDialog.class.getResource("messenger.css").toString());
		window.setScene(scene);
		window.showAndWait();
		
		return answer;
	}
	
	public static boolean display3(String title, String message, String msg) {
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle(title);
		window.getIcons().add(new Image(ConfirmDialog.class.getResource("images/noti.png").toString()));
		window.setMaxWidth(250);
		
		Label label = new Label();
		label.setAlignment(Pos.CENTER);
		label.setText(message+"\n"+msg);
		label.setTextAlignment(TextAlignment.CENTER);
		
		
		Button yesBtn = new Button("확인");
		
		yesBtn.setOnAction(e->{
			answer = true;
			window.close();
		});
		
		VBox layout = new VBox(20);
		HBox hbox = new HBox(10);
		
		hbox.getChildren().addAll(yesBtn);
		layout.getChildren().addAll(label,hbox);
		layout.setAlignment(Pos.CENTER);
		hbox.setAlignment(Pos.CENTER);
		
		
		Scene scene = new Scene(layout,300,150);
		scene.getStylesheets().add(ConfirmDialog.class.getResource("messenger.css").toString());
		window.setScene(scene);
		window.showAndWait();
		
		return answer;
	}
}
