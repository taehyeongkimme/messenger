package com.kh.messenger.client2;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import com.kh.messenger.common.Command;
import com.kh.messenger.common.DialogUtil;
import com.kh.messenger.common.MemberDTO;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;


public class MemberJoinController implements Initializable{

	@FXML private TextField id;
	@FXML private PasswordField pw;
	@FXML private PasswordField pwChk;
	@FXML private TextField tel;
	@FXML private TextField nickName;
	@FXML private ToggleGroup sex;
	@FXML private RadioButton sex1;
	@FXML private RadioButton sex2;
	@FXML private ComboBox<String> region;
	@FXML private DatePicker birth;
	
	MemberDTO memberDTO = new MemberDTO();
	Stage stage = null;
	Protocol protocol;

	
	void setDialog(Stage stage) {
		this.stage = stage;
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		sex.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {

			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
				memberDTO.setGender(newValue.getUserData().toString());
				
			}
			
		});
	}
		public void memberJoin(Event e) {
			//유효성체크
			
			//필수입력값 : 아이디,비밀번호,닉네임,성별,생년월일,전화번호
			if(id.getText().trim().equals("")) {
				DialogUtil.dialog(AlertType.ERROR, "알림", "아이디 확인", "아이디를 입력하세요.");
				id.requestFocus();
				return;
				
			}else if(id.getText().trim().length() <4 || id.getText().trim().length()>20) {
				DialogUtil.dialog(AlertType.ERROR, "알림", "아이디 확인", "아이디는 4-20자리 이내로 입력하세요.");
				id.requestFocus();
				return;
			}
//			else if(id.getText().trim().equals()){
//				msg.setText("아이디 중복오류입니다!!");
//				id.requestFocus();
//				return;
//			}
			
			boolean isID = Pattern.matches("\\w+@\\w+\\.\\w+(\\.\\w+)?", id.getText());
			if(!isID) {
				DialogUtil.dialog(AlertType.ERROR, "알림", "아이디 확인", "아이디가 이메일형식과 맞지 않습니다.");
				id.requestFocus();
				return;
			}
			
			if(pw.getText().trim().equals("") || pw.getText().trim().length() < 4 
					||pw.getText().trim().length() > 20) {
				DialogUtil.dialog(AlertType.ERROR, "알림", "비밀번호 확인", "비밀번호는 4-20자리 이내로 입력하세요.");
				pw.requestFocus();
				return;
			}
			
			if(pwChk.getText().trim().equals("")) {
				DialogUtil.dialog(AlertType.ERROR, "알림", "비밀번호 확인", "비밀번호를 한번 더 확인하세요.");
				pwChk.requestFocus();
				return;
			}
			
			if(!pw.getText().trim().equals(pwChk.getText().trim())) {
				DialogUtil.dialog(AlertType.ERROR, "알림", "비밀번호 불일치", "비밀번호를 한번 더 확인하세요.");
				pwChk.requestFocus();
				return;
			}
			
			if(tel.getText().trim().equals("")) {
				DialogUtil.dialog(AlertType.ERROR, "알림", "전화번호 확인", "전화번호 입력하세요.");
				tel.requestFocus();
				return;
			}
			
			boolean isTel = Pattern.matches("^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$",tel.getText());
			if(!isTel) {
				DialogUtil.dialog(AlertType.ERROR, "알림", "전화번호 확인", "전화번호는 '-'과 함께 10-11자리의 숫자만 입력하세요.");
				tel.requestFocus();
				return;
			}
			
			if(nickName.getText().trim().equals("") || nickName.getText().trim().length() <4) {
				DialogUtil.dialog(AlertType.ERROR, "알림", "닉네임 확인", "닉네임은 4자리이상 입력하세요.");
				nickName.requestFocus();
				return;
			}
			
			if(birth.getValue()==null) {
				DialogUtil.dialog(AlertType.ERROR, "알림", "생년월일 확인", "생년월일을 입력하세요.");
				birth.requestFocus();
				return;
			}
			
			memberDTO.setId(id.getText());
			memberDTO.setPw(pw.getText());
			memberDTO.setTel(tel.getText());
			memberDTO.setNickName(nickName.getText());
			memberDTO.setRegion((String)region.getValue());
			memberDTO.setBirth(birth.getValue().toString());
			System.out.println(memberDTO);
			
			protocol = new Protocol(this);
			protocol.memberJoin(memberDTO);
			stage.close();
			
		}
		
		 //회원가입 결과
		public void memberJoin(Command command) {
			boolean flag = ((Boolean) command.getResults().elementAt(0)).booleanValue();
			if (flag) {
				Platform.runLater(() -> {
					DialogUtil.dialog(AlertType.INFORMATION, "알림", "회원가입", "회원가입이 완료되었습니다.");
				});
			} else {
				Platform.runLater(() -> {
					DialogUtil.dialog(AlertType.ERROR, "알림", "회원정보오류", "회원가입에 실패하였습니다.");
				});
			}
		}

		public void memberCancel(Event e) {
			stage.close();
		}
}
