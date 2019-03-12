package com.kh.messenger.client;

import com.kh.messenger.common.ConfirmDialog;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class AppMain2 extends Application{

	public static void main(String[] args) {
		Application.launch(args);

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// 레이아웃 파일을 읽어 각 태그를 객체화하고 최상위 루트컨테이너를 참조값으로 반환한다
//		Parent parent = FXMLLoader.load(getClass().getResource("root.fxml"));
		
		
    FXMLLoader loader = new FXMLLoader(getClass().getResource("root.fxml"));
    Parent parent = (Parent) loader.load();
  
		RootController root = loader.getController();
		root.setPrimaryStage(primaryStage);
		 

		Scene scene = new Scene(parent);
		scene.getStylesheets().add(getClass().getResource("messenger.css").toString());
		primaryStage.setScene(scene);
		primaryStage.setTitle("★메신저 로그인★");
		primaryStage.getIcons().add(new Image(getClass().getResource("images/smilemain.png").toString()));
		primaryStage.show();
		
		primaryStage.setOnCloseRequest(e->{
			boolean answer = false;
			answer = ConfirmDialog.display("확인", "메신저를 종료하시겠습니까?");
			if(answer) {
				primaryStage.close();
			}else {
				e.consume();
			}
		});
		
	}

}
