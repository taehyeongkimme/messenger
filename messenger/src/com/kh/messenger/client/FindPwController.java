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

public class FindPwController implements Initializable {
	@FXML
	TextField id;
	@FXML
	TextField tel;
	@FXML
	DatePicker birth;

	private Protocol protocol;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	public void handleFindPw(Event e) throws IOException {
		if (id.getText().trim().equals("")) {
			DialogUtil.dialog(AlertType.ERROR, "알림", "비밀번호조회", "아이디를 입력하세요.");
			id.requestFocus();
			return;

		} else if (id.getText().trim().length() < 4 || id.getText().trim().length() > 20) {
			DialogUtil.dialog(AlertType.ERROR, "알림", "비밀번호조회", "아이디는 4-20자리 이내로 입력하세요.");
			id.requestFocus();
			return;
		}

		boolean isID = Pattern.matches("\\w+@\\w+\\.\\w+(\\.\\w+)?", id.getText());
		if (!isID) {
			DialogUtil.dialog(AlertType.ERROR, "알림", "비밀번호조회", "아이디가 이메일형식과 맞지 않습니다.");
			id.requestFocus();
			return;
		}

		if (tel.getText().trim().equals("")) {
			DialogUtil.dialog(AlertType.ERROR, "알림", "비밀번호조회", "전화번호를 입력하세요.");
			tel.requestFocus();
			return;
		}

		boolean isTel = Pattern.matches("^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$", tel.getText());
		if (!isTel) {
			DialogUtil.dialog(AlertType.ERROR, "알림", "비밀번호조회", "전화번호는 '-'과 함께 10-11자리의 숫자만 입력하세요.");
			tel.requestFocus();
			return;
		}

		if (birth.getValue() == null) {
			DialogUtil.dialog(AlertType.ERROR, "알림", "비밀번호조회", "생년월일을 입력하세요.");
			birth.requestFocus();
			return;
		}

		protocol = new Protocol(this);
		protocol.findPw(id.getText(), tel.getText(), birth.getValue().toString());

	}

	// 비밀번호조회 수신
	public void findPw(Command command) {
		String pw = (String) command.getResults().elementAt(0);
		if (pw != null) {
			Platform.runLater(() -> {
				DialogUtil.dialog(AlertType.INFORMATION, "알림", "비밀번호조회", id.getText()+" 님의 비밀번호는 " + pw + " 입니다.");
			});
		} else {
			Platform.runLater(() -> {
				DialogUtil.dialog(AlertType.ERROR, "알림", "비밀번호조회", "해당하는 정보를 찾을 수 없습니다.");
			});
		}
	}

}
