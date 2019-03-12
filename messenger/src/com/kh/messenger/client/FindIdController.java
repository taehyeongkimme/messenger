package com.kh.messenger.client;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import com.kh.messenger.common.Command;
import com.kh.messenger.common.DialogUtil;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FindIdController implements Initializable {
	@FXML
	TextField tel;
	@FXML
	DatePicker birth;

	Stage primaryStage, dialog;
	private Protocol protocol;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}

	public void handleFindId(Event e) throws IOException {
		if (tel.getText().trim().equals("")) {
			DialogUtil.dialog(AlertType.ERROR, "알림", "아이디조회", "전화번호를 입력하세요.");
			tel.requestFocus();
			return;
		}

		boolean isTel = Pattern.matches("^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$", tel.getText());
		if (!isTel) {
			DialogUtil.dialog(AlertType.ERROR, "알림", "아이디조회", "전화번호는 '-'과 함께 10-11자리의 숫자만 입력하세요.");
			tel.requestFocus();
			return;
		}

		if (birth.getValue() == null) {
			DialogUtil.dialog(AlertType.ERROR, "알림", "아이디조회", "생년월일을 입력하세요.");
			birth.requestFocus();
			return;
		}
		
		protocol = new Protocol(this);
		protocol.findId(tel.getText(),birth.getValue().toString());
		
	}

	// 아이디조회 수신
	public void findId(Command command) {
		String id = (String)command.getResults().elementAt(0);
		if (id!=null) {
			Platform.runLater(() -> {
				DialogUtil.dialog(AlertType.INFORMATION, "알림", "아이디조회", "아이디는 "+id+" 입니다.");
			});
		} else {
			Platform.runLater(() -> {
				DialogUtil.dialog(AlertType.ERROR, "알림", "아이디조회", "해당하는 정보를 찾을 수 없습니다.");
			});
		}
	}

	
}
