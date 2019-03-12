package com.kh.messenger.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.kh.messenger.common.Command;
import com.kh.messenger.common.MemberDTO;

public class MSvr {

	MDAO mdao;

	ServerSocket serverSocket; // 서버소켓
	ExecutorService executorService; // 스레드풀 이용
	List<Client> connections = new Vector<Client>(); // 클라이언트정보 저장
	Hashtable<String, String> connectedIPList = new Hashtable<>(); // 로긴한 friendId, ip정보저장

	final String HOSTNAME = "192.168.0.126";
	final int PORT = 6001;

	private MSvrCtr mSvrCtr;
	
	//로긴 id, client 정보저장
	static Hashtable<String, Client> connectedClientList = new Hashtable<>();

	MSvr(MSvrCtr mSvrCtr) {
		this.mSvrCtr = mSvrCtr;
		this.mdao = new MDAOImpl();
	}

	// 서버소켓 생성 후 클라이언트가 접속하면 클라이언트 소켓을 생성
	void startServer() {

		executorService = Executors.newFixedThreadPool(20);

		try {
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress(HOSTNAME, PORT));
			mSvrCtr.uiUpdate(UiCommand.SERVER_LOG, "[서버소켓 생성됨]");
		} catch (IOException e) {
			if (!serverSocket.isClosed()) {
				stopServer();
			}
			return;
		}

		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				mSvrCtr.uiUpdate(UiCommand.SERVER_START, null);
				while (true) {
					try {
						// 클라이언트 접속할 때 까지 대기상태로 있다가 요청이 오면 socket을 반환함
						Socket socket = serverSocket.accept();

						String message = "[연결 수락 : " + socket.getRemoteSocketAddress() + ": " + Thread.currentThread().getName()
								+ "]";
						mSvrCtr.uiUpdate(UiCommand.SERVER_LOG, message);

						Client client = new Client(socket);

						// 접속한 클라이언트를 Vector에 저장 => client관리용도
						connections.add(client);
						mSvrCtr.uiUpdate(UiCommand.SERVER_LOG, "[연결 개수 :" + connections.size() + "]");

					} catch (IOException e) {
						if (!serverSocket.isClosed()) {
							stopServer();
						}
						break;
					}
				}
			}
		};
		executorService.submit(runnable);
	}

	// 접속한 클라이언트 소켓을 모두 close 하고 서버소켓을 close
	void stopServer() {

		try {
			Iterator<Client> iterator = connections.iterator();
			while (iterator.hasNext()) {
				Client client = iterator.next();
				client.socket.close();
				iterator.remove();
			}
			if (serverSocket != null && !serverSocket.isClosed()) {
				serverSocket.close();
			}
			if (executorService != null && !executorService.isShutdown()) {
				executorService.isShutdown();
			}

			mSvrCtr.uiUpdate(UiCommand.SERVER_STOP, null);
		} catch (Exception e) {
		}

	}

	// 접속을 요청한 클라이언트와 통신하는 기능
	class Client {

		Socket socket;
		ObjectInputStream ois;
		ObjectOutputStream oos;
		Command command;

		String loginId;
		
		public Client(Socket socket) {
			this.socket = socket;

			try {
				oos = new ObjectOutputStream(socket.getOutputStream());
				ois = new ObjectInputStream(socket.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
			receive();
		}

		void receive() {
			Runnable runnable = new Runnable() {

				@Override
				public void run() {

					try {
						while (true) {

							command = (Command) ois.readObject();
							System.out.println(command.getType().name() + "호출점!!");
							
							switch (command.getType()) {
							case ISLOGIN: {
								String[] args = command.getArgs();
								String id = args[0];
								String pw = args[1];
								boolean status = mdao.isLogin(id, pw);
								// 중록로긴인 경우
								if(connectedClientList.containsKey(id)) {
									command.getResults().setStatus(-1);
									command.getResults().addElement(
											connectedClientList.get(id).socket.getInetAddress().getHostAddress());
								}else {
								// 중복로긴아닌 경우
									command.getResults().setStatus(1);
									command.getResults().addElement(new Boolean(status));
								}
								
							}	
								break;
								
							case REGISTUSER: {
								String[] args = command.getArgs();
								String id = args[0];
								
								loginId = id;
								
								//1) 접속 client 정보 보관(key:id, value:client)
								connectedClientList.put(id, Client.this);
								System.out.println("로그인 정보: " + connectedClientList);
								//2) 접속자의 전체 친구목록
								List<MemberDTO> friendList = mdao.getFriends(loginId);
								//3) 접속자의 로그인한 친구목록
								Map<String, String> loginFriendList = new Hashtable<>();
								friendList.stream().forEach(m->{
									if(connectedClientList.containsKey(m.getId())){
										loginFriendList.put(m.getId(), 
												connectedClientList.get(m.getId()).socket.getInetAddress().getHostAddress());
									}
								});
								command.getResults().addElement(friendList); //전체친구목록
								command.getResults().addElement(loginFriendList); //로그온 친구목록
								
								//4) 접속자를 친구로 등록한 사용자에게 접속알림
								notifyLogin(loginId);
							}
								break;

							case GETFRIENDS: {
								System.out.println("GETFRIENDS 호출됨");
								String[] args = command.getArgs();
								String id = args[0];
								List<MemberDTO> list = mdao.getFriends(id);
								command.getResults().addElement(list);
							}
								break;
							case FINDFRIEND: {
								System.out.println("FINDFRIEND 호출됨");
								String[] args = command.getArgs();
								String id = args[0];
								MemberDTO memberDTO = mdao.getMember(id);
								command.getResults().addElement(memberDTO);
							}
								break;

							case ADDFRIEND: {
								System.out.println("ADDFRIEND 호출됨");
								String[] args = command.getArgs();
								String myId = args[0];
								String friendId = args[1];
								//1) 가입된 회원인지 체크
								if (mdao.isMember(friendId)) {

									// db에 친구추가
									boolean status = mdao.addFriend(myId, friendId);
									if (status) {
										// 친구정보 가져오기
										MemberDTO memberDTO = mdao.getMember(friendId);
										
										// 친구가 온라인 상태인 경우
										if(connectedClientList.get(friendId) != null) {
											Map<String,String> loginFriend = new Hashtable<>();
											loginFriend.put(friendId, 
													connectedClientList.get(friendId).socket.getInetAddress().getHostAddress());
											command.getResults().setStatus(1);
											command.getResults().addElement(new Boolean(status));
											command.getResults().addElement(memberDTO);
											command.getResults().addElement(loginFriend);
										}else {
											// 친구가 오프라인 상태인 경우
											command.getResults().setStatus(2);
											command.getResults().addElement(new Boolean(status));
											command.getResults().addElement(memberDTO);
										}
									} else {
										command.getResults().addElement(new Boolean(status));
									}
								} else {
									command.getResults().setStatus(-1);
									command.getResults().addElement(new Boolean(false));
								}
							}
								break;

							case DELFRIEND: {
								System.out.println("DELFRIEND 호출됨");
								String[] args = command.getArgs();
								String myId = args[0];
								String friendId = args[1];
								boolean status = mdao.delFriend(myId, friendId);
								
								// 친구가 온라인 상태인 경우
								if(connectedClientList.get(friendId) != null) {
									Map<String,String> loginFriend = new Hashtable<>();
									loginFriend.put(friendId, 
											connectedClientList.get(friendId).socket.getInetAddress().getHostAddress());
									command.getResults().setStatus(1);
									command.getResults().addElement(new Boolean(status));
									command.getResults().addElement(loginFriend);
								}else {
									// 친구가 오프라인 상태인 경우
									command.getResults().setStatus(2);
									command.getResults().addElement(new Boolean(status));
								}
							}
								break;
							case MEMBERJOIN: {
								System.out.println("MEMBERJOIN 호출됨");
								MemberDTO memberDTO = (MemberDTO) command.getRequests().elementAt(0);
								boolean status = mdao.memberJoin(memberDTO);
								command.getResults().addElement(new Boolean(status));
							}
								break;

							case MEMBEROUT: {
								System.out.println("MEMBEROUT 호출됨");
								String[] args = command.getArgs();
								String id = args[0];
								String pw = args[1];
								if (mdao.isLogin(id, pw)) {
									boolean status = mdao.memberOut(id);
									command.getResults().addElement(new Boolean(status));
								} else {
									command.getResults().addElement(new Boolean(false));
								}
							}
								break;

							case FINDID: {
								System.out.println("FINDID 호출됨");
								String[] args = command.getArgs();
								String tel = args[0];
								String birth = args[1];
								String id = mdao.findId(tel, birth);
								command.getResults().addElement(id);
							}
								break;

							case FINDPW: {
								System.out.println("FINDPW 호출됨");
								String[] args = command.getArgs();
								String id = args[0];
								String tel = args[1];
								String birth = args[2];
								String pw = mdao.findPw(id, tel, birth);
								command.getResults().addElement(pw);
							}
								break;

							default:
								break;
							}

							String message = "[요청 처리 : " + socket.getRemoteSocketAddress() + ": " + Thread.currentThread().getName()
									+ "]";
							mSvrCtr.uiUpdate(UiCommand.SERVER_LOG, message);

							send(command);
						}
					} catch (Exception e) {
						try {
							clientClose();
						} catch (IOException e1) {
						}
					}
				}
			};
			executorService.submit(runnable);
		}

		void send(Command command) {
			Runnable runnable = new Runnable() {

				@Override
				public void run() {
					try {
						oos.writeObject(command);
						oos.flush();
						System.out.println(command.getType().name() + ": 결과 성공적으로 보냄!!");
					} catch (IOException e) {

						try {
							connections.remove(Client.this);
							String message = "[클라이언트 통신 안됨 : " + socket.getRemoteSocketAddress() + ": "
									+ Thread.currentThread().getName() + "]";
							mSvrCtr.uiUpdate(UiCommand.SERVER_LOG, message);
							mSvrCtr.uiUpdate(UiCommand.SERVER_LOG, "[연결 개수: " + connections.size() + "]");

							socket.close();
						} catch (IOException e1) {
						}
					}
				}
			};
			executorService.submit(runnable);
		}
		
		private void clientClose() throws IOException {
			String message = "[클라이언트 통신 안됨 : " + socket.getRemoteSocketAddress() + ": "
					+ Thread.currentThread().getName() + "]";
			mSvrCtr.uiUpdate(UiCommand.SERVER_LOG, message);
			mSvrCtr.uiUpdate(UiCommand.SERVER_LOG, "[연결 개수: " + connections.size() + "]");
			socket.close();
			
			// 로그아웃 알림
			notifyLogOut(loginId);
			connections.remove(Client.this);
			connectedClientList.remove(loginId);
		}
		
		// 내가 친구로/나를 친구로 등록한 자에게 로긴정보를 알림
		protected void notifyLogin(String loginId) {
			List<MemberDTO> friendList = mdao.getFriends(loginId);					// 내가 친구로 등록한 사람 목록
			List<MemberDTO> friendedList = mdao.getFriendedList(loginId);		// 나를 친구로 등록한 사람 목록
			
			List<MemberDTO> finalList = new ArrayList<>();	// 중복제거 목록
			
			// map으로 중복제거
			Map<String,MemberDTO> map = new HashMap<>();
			friendList.stream().forEach(m->{
				map.put(m.getId(), m);
			});
			
			friendedList.stream().forEach(m->{
				map.put(m.getId(), m);
			});
			
			// map에서 중복제거된 value추출
			map.entrySet().stream().forEach(m->{
				finalList.add(m.getValue());
			});

//			// finalList = friendedList - friendList
//			friendedList.stream().forEach(m->{
//				if(!friendList.contains(m.getId())) {
//					finalList.add(m);
//				}
//			});
//			
////			// finalList = friendedList + friendList
////			friendList.addAll(finalList);
			
			Map<String,String> loginIdIp = new Hashtable<>();
			loginIdIp.put(loginId, connectedClientList.get(loginId).socket.getInetAddress().getHostAddress());
			
			finalList.stream().forEach(m->{
				Command command = new Command(Command.CommandType.LOGIN_NOTIFY);
				command.getResults().addElement(loginIdIp);
				if(connectedClientList.containsKey(m.getId())) {
					// 전송시간 간격주기
					try {
						Thread.sleep(50);
					}catch(InterruptedException e) {
						e.printStackTrace();
					}
					connectedClientList.get(m.getId()).send(command);
				}
			});
		}
		
		// 나를 친구로 등록한 자에게 로그아웃정보를 알림
		protected void notifyLogOut(String logOutId) {
			System.out.println("로그아웃 알림 호출됨!!"+logOutId);
			List<MemberDTO> friendedList = mdao.getFriendedList(logOutId);	// 나를 친구로 등록한 사람 목록
			
			Map<String,String> logOutIp= new Hashtable<>();
			logOutIp.put(logOutId, connectedClientList.get(logOutId).socket.getInetAddress().getHostAddress());
			
			System.out.println("friendedList3"+friendedList);
			System.out.println("connectedClientList"+connectedClientList);
			friendedList.stream().forEach(m->{
				Command command = new Command(Command.CommandType.LOGOUT_NOTIFY);
				command.getResults().addElement(logOutIp);
				if(connectedClientList.containsKey(m.getId())) {
					// 전송시간 간격주기
					try {
						Thread.sleep(50);
					}catch(InterruptedException e) {
						e.printStackTrace();
					}
					connectedClientList.get(m.getId()).send(command);
				}
			});
		}
	}
}



























