package com.kh.messenger.sample;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	public static void main(String[] args) {
		Socket socket = null;
		InputStream is = null;
		OutputStream os = null;
		ServerSocket serverSocket = null;

		try {
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress("192.168.0.126", 9000));
			while (true) {
				System.out.println("[연결기다림]");
				socket = serverSocket.accept();
				InetSocketAddress isa = (InetSocketAddress) socket.getRemoteSocketAddress();
				System.out.println("[연결수락함]" + isa.getHostName());
				System.out.println("[연결수락함]" + isa.getAddress());
				System.out.println("[연결수락함]" + isa.getPort());
				System.out.println("[연결수락함]" + isa.getHostString());

				byte[] bytes = new byte[100];
				is = socket.getInputStream();
				int readByteCount = is.read(bytes);
				String msg = new String(bytes, 0, readByteCount, "UTF-8");
				System.out.println("데이터 수신: " + msg);

				os = socket.getOutputStream();
				msg = "반갑습니다~";
				bytes = msg.getBytes("UTF-8");
				os.write(bytes);
				os.flush();
				System.out.println("[데이터 송신 완료!]");

			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				os.close();
				is.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
