package com.kh.messenger.sample;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ObjectInputOutputStream {

	public static void main(String[] args) throws Exception {
		FileOutputStream fos = new FileOutputStream("d:/temp/Login.dat");
		ObjectOutputStream oos =  new ObjectOutputStream(fos);
		
		LoginInfo loginInfo = new LoginInfo();
		loginInfo.setId("test1@test.com");
		loginInfo.setPw("test1");
		
		oos.writeObject(loginInfo);
		oos.flush();
		
		oos.close(); fos.close();
		
		FileInputStream fis = new FileInputStream("d:/temp/Login.dat");
		ObjectInputStream ois = new ObjectInputStream(fis);
		
		LoginInfo info = (LoginInfo)ois.readObject();
		System.out.println(info.getId());
		System.out.println(info.getPw());
		
	}

}
