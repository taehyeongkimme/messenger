package com.kh.messenger.client;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import com.kh.messenger.common.Command;
import com.kh.messenger.common.ConfirmDialog;
import com.kh.messenger.common.DialogUtil;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class RootController implements Initializable {
	@FXML
	private Button login;
	@FXML
	private TextField id;
	@FXML
	private PasswordField pw;

	Stage primaryStage = null;

	Parent memJoinWindow = null;
	Parent messengerMainWindow = null;
	Protocol protocol = null;
	String loginId;

	public RootController() {

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		login.setOnAction(e->{

			System.out.println("로그인 클릭");
			System.out.println("아이디 : " + id.getText());
			System.out.println("비밀번호 : " + pw.getText());

			login.setDisable(true);
			loginId = id.getText();
			protocol = new Protocol(this);
			protocol.isMember(id.getText(), pw.getText());
			Platform.setImplicitExit(false);

		});

	}

//	public void login(Event e) {
//
//		System.out.println("로그인 클릭");
//		System.out.println("아이디 : " + id.getText());
//		System.out.println("비밀번호 : " + pw.getText());
//
//		loginId = id.getText();
//		protocol = new Protocol(this);
//		protocol.isMember(id.getText(), pw.getText());
//		Platform.setImplicitExit(false);

		// 회원인지판단
		// 1)회원이면 : "로그인 성공" 메세지를 콘솔에 띄운다
//		if (Member.getInstance().containsKey(id.getText())) {
//			memPw = Member.getInstance().get(id.getText()).getPw();
//			
//			if (memPw.equals(pw.getText())) {
//				System.out.println("로그인 성공!!");
//				msg.setText("로그인 성공!!");
//				
//				primaryStage.hide();
//				
//				Stage dialog = new Stage(StageStyle.DECORATED);
//				dialog.initModality(Modality.APPLICATION_MODAL);
//				dialog.initOwner(primaryStage);
//				dialog.setTitle("메신저 메인");
//
//				FXMLLoader loader = new FXMLLoader(getClass().getResource("messengerMain.fxml"));
//				try {
//					messengerMainWindow = loader.load();
//				} catch (IOException e1) {
//				
//				}
//				
//				MessengerMainController controller = (MessengerMainController)(loader.getController());
//				controller.setDialog(primaryStage,dialog);
//				
//				Scene scene = new Scene(messengerMainWindow);
//
//				dialog.setScene(scene);
//				dialog.setResizable(false);
//				dialog.show();
//				
//				// 회원이면서 비밀번호가 맞는지 판단
//				// 3)비밀번호오류 : "비밀번호가 맞지않습니다." 메세지를 콘솔에 띄운다
//			} else {
//				System.out.println("비밀번호가 맞지않습니다.");
//				msg.setText("비밀번호가 맞지않습니다.");
//				pw.clear();
//				pw.requestFocus();
//			}
//			// 2)비회원이면 : "회원정보가 없습니다." 메세지를 콘솔에 띄운다
//		} else {
//			System.out.println("회원정보가 없습니다.");
//			msg.setText("회원정보가 없습니다.");
//			id.clear();
//			id.requestFocus();
//		}

//	}

	public void doJoin(Event e) throws Exception {

		Stage dialog = new Stage(StageStyle.UTILITY);
		dialog.initModality(Modality.WINDOW_MODAL);
		dialog.initOwner(primaryStage.getScene().getWindow());
		dialog.setTitle("회원가입");

		FXMLLoader loader = new FXMLLoader(getClass().getResource("member.fxml"));
		memJoinWindow = loader.load();

		MemberJoinController controller = (MemberJoinController) (loader.getController());
		controller.setDialog(dialog);
		Scene scene = new Scene(memJoinWindow);
		scene.getStylesheets().add(getClass().getResource("messenger.css").toString());

		dialog.setScene(scene);
		dialog.setResizable(false);
		dialog.show();

	}

	public void searchId(Event e) throws Exception {
		Stage dialog = new Stage(StageStyle.TRANSPARENT);
		dialog.initModality(Modality.WINDOW_MODAL);
		dialog.initOwner(primaryStage);
		dialog.setTitle("아이디 조회");

		Parent parent = FXMLLoader.load(getClass().getResource("findid.fxml"));
		Button findIdCloseBtn = (Button) parent.lookup("#findIdCloseBtn");
		findIdCloseBtn.setOnAction(event -> dialog.close());

		Scene scene = new Scene(parent);
		scene.getStylesheets().add(getClass().getResource("messenger.css").toString());

		dialog.setScene(scene);
		dialog.setResizable(false);
		dialog.show();

	}

	public void searchPw(Event e) throws Exception {
		Stage dialog = new Stage(StageStyle.TRANSPARENT);
		dialog.initModality(Modality.WINDOW_MODAL);
		dialog.initOwner(primaryStage.getScene().getWindow());
		dialog.setTitle("비밀번호 조회");

		Parent parent = FXMLLoader.load(getClass().getResource("findpw.fxml"));
		Button findPwCloseBtn = (Button) parent.lookup("#findPwCloseBtn");
		findPwCloseBtn.setOnAction(event -> dialog.close());

		Scene scene = new Scene(parent);
		scene.getStylesheets().add(getClass().getResource("messenger.css").toString());

		dialog.setScene(scene);
		dialog.setResizable(false);
		dialog.show();

	}

	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;

	}

	public void doLogin(Command command) {

		//	로긴버튼 활성화
		Platform.runLater(()->{ login.setDisable(false); });

		int status = command.getResults().getStatus();
		// 중복로긴
		if(status == -1) {
			String ip = (String)command.getResults().elementAt(0);
			Platform.runLater(()->{
			DialogUtil.dialog(AlertType.ERROR, "알림", "중복로그인 (접근ip)"+ip, "현재 접속된 회원입니다.");
			});
			return;
		}
		boolean flag = ((Boolean) command.getResults().elementAt(0)).booleanValue();

		// 정상로그인
		if (flag) {
			Platform.runLater(() -> {
				Stage dialog = new Stage(StageStyle.UNIFIED);
				dialog.initModality(Modality.APPLICATION_MODAL);
				dialog.initOwner(primaryStage);
				dialog.setTitle("메신저 메인");
				dialog.getIcons().add(new Image(getClass().getResource("images/smilemain.png").toString()));

				FXMLLoader loader = new FXMLLoader(getClass().getResource("messengerMain.fxml"));
				try {
					messengerMainWindow = loader.load();
					primaryStage.hide();
				} catch (IOException e1) {

				}

				MessengerMainController controller = (MessengerMainController) (loader.getController());
				controller.setDialog(primaryStage, dialog);
				controller.setInitial(loginId, protocol);
				protocol.setMessengerMainController(controller);

				Scene scene = new Scene(messengerMainWindow);
				scene.getStylesheets().add(getClass().getResource("messenger.css").toString());

				dialog.setScene(scene);
				dialog.setResizable(false);
				dialog.show();

				dialog.setOnCloseRequest(e -> {
					boolean answer = false;
					answer = ConfirmDialog.display("확인", "로그아웃하고 종료하시겠습니까?");
					if (answer) {
						if (protocol != null) {
							protocol.stopClient();
						}
						dialog.close();
						primaryStage.show();
					} else {
						e.consume();
					}
				});
			});

			// 유효한 로그인이 아닌경우
		} else {
			Platform.runLater(() -> {
				if (id.getText().trim().equals("")) {
					DialogUtil.dialog(AlertType.ERROR, "알림", "계정확인", "아이디를 입력하세요.");
					id.requestFocus();
					return;

				} else if (id.getText().trim().length() < 4 || id.getText().trim().length() > 20) {
					DialogUtil.dialog(AlertType.ERROR, "알림", "계정확인", "아이디는 4-20자리 이내로 입력하세요.");
					id.requestFocus();
					return;
				}

				boolean isID = Pattern.matches("\\w+@\\w+\\.\\w+(\\.\\w+)?", id.getText());
				if (!isID) {
					DialogUtil.dialog(AlertType.ERROR, "알림", "계정확인", "아이디가 이메일형식과 맞지 않습니다.");
					id.requestFocus();
					return;
				}

				if (pw.getText().trim().equals("") || pw.getText().trim().length() < 4 || pw.getText().trim().length() > 20) {
					DialogUtil.dialog(AlertType.ERROR, "알림", "계정확인", "비밀번호는 4-20자리 이내로 입력하세요.");
					pw.requestFocus();
					return;
				}
				login.setDisable(false);
				DialogUtil.dialog(AlertType.ERROR, "알림", "계정확인", "등록되지 않은 아이디 혹은\n비밀번호가 올바르지 않습니다.");
			});
		}
	}

	public void loginBtnDisable(Boolean status) {
		login.setDisable(status);
	}

}
