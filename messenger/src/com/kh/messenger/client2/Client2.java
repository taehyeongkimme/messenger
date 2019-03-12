package com.kh.messenger.client2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import javafx.application.Platform;

public class Client2 {
	
	ReceiveChatWindowController chatWindowController;
	Socket socket;
	
	Client2(ReceiveChatWindowController chatWindowController){
		this.chatWindowController = chatWindowController;
		System.out.println("클라이언트 생성됨");
		startClient();
	}
	
	void startClient() {
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					socket = new Socket();
					socket.connect(new InetSocketAddress("192.168.0.121", 6001));
					Platform.runLater(()->{
						try {
							chatWindowController.display("[연결 완료: "  + socket.getRemoteSocketAddress() + "]");
							chatWindowController.btnSendDisable(false);
						} catch (Exception e) {}
					});
				} catch(Exception e) {
					Platform.runLater(()->chatWindowController.display("[서버 통신 안됨]"));
					if(!socket.isClosed()) { stopClient(); }
					return;
				}
				receive();
			}
		};
		thread.start();
	}
	
	void stopClient() {
		try {
			Platform.runLater(()->{
				chatWindowController.display("[연결 끊음]");
				chatWindowController.btnSendDisable(true);
			});
			if(socket!=null && !socket.isClosed()) {
				socket.close();
			}
		} catch (IOException e) {}
	}		
	
	void receive() {
		while(true) {
			try {
				byte[] byteArr = new byte[100];
				InputStream inputStream = socket.getInputStream();
				
				//서버가 비정상적으로 종료했을 경우 IOException 발생
				int readByteCount = inputStream.read(byteArr);
				
				//서버가 정상적으로 Socket의 close()를 호출했을 경우
				if(readByteCount == -1) { throw new IOException(); }
				
				String data = new String(byteArr, 0, readByteCount, "UTF-8");
				
				Platform.runLater(()->chatWindowController.display("[받기 완료] "  + data));
			} catch (Exception e) {
				Platform.runLater(()->chatWindowController.display("[서버 통신 안됨]"));
				stopClient();
				break;
			}
		}
	}
	
	void send(String data) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {		
					byte[] byteArr = data.getBytes("UTF-8");
					OutputStream outputStream = socket.getOutputStream();
					outputStream.write(byteArr);
					outputStream.flush();
					Platform.runLater(()->chatWindowController.display("[보내기 완료]"));
				} catch(Exception e) {
					Platform.runLater(()->chatWindowController.display("[서버 통신 안됨]"));
					stopClient();
				}				
			}
		};
		thread.start();
	}
}
