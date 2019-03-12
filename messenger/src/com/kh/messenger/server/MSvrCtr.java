package com.kh.messenger.server;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class MSvrCtr implements Initializable {

	@FXML private Button btnStartStop;
	@FXML private TextArea talog;

	private Stage primaryStage;

	private MSvr msvr = new MSvr(this);

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		btnStartStop.setOnAction(event -> doBtnStartStop(event));
	}

	private void doBtnStartStop(ActionEvent event) {
		if (btnStartStop.getText().equals("서버 시작")) {
			msvr.startServer();
		} else {
			msvr.stopServer();
		}
	}

	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	public void uiUpdate(UiCommand uiCommand, String msg) {

		switch (uiCommand) {
		case SERVER_START:
			Platform.runLater(() -> {
				displayText("[서버 시작]");
				btnStartStop.setText("서버 종료");
			});
			break;
		case SERVER_STOP:
			Platform.runLater(() -> {
				displayText("[서버 종료]");
				btnStartStop.setText("서버 시작");
			});
			break;
		case SERVER_LOG:
			Platform.runLater(() -> displayText(msg));
			break;
		}
	}

	private void displayText(String msg) {
		talog.appendText(msg + "\n");
	}

}
