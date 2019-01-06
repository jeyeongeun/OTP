import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Vector;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;

import org.apache.commons.lang3.RandomStringUtils;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import org.apache.commons.lang3.StringUtils;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/****************************************
 * 클래스 명 : AndroidSocket
 * 역할 : 안드로이드와 소켓통신을 위한 클래스이다.
 * 포트 아무거나 열어서 내용 담아서 보내고 받으면 된다.
 *****************************************/
public class AndroidSocket {
	
	Vector<User> all_user = new Vector<User>();	//모든 사용자 리스트
	
	
	private int port = 5555;
	 private ServerSocket server_socket;
	 private Socket socket;
	 ServerThread current = new ServerThread();
	 
	 public AndroidSocket(){
		 startNetwork();
	 }
	 
	 private void startNetwork(){
		 try{
				server_socket = new ServerSocket(port);
				System.out.println("서버를 시작합니다...");
				connect();
			} catch(IOException e){
				System.out.println("이미 사용 중인 포트입니다.");
			} catch(Exception e) {
				System.out.println("잘못 입력하였습니다.");
			}
	 }
	 
	 private void setCurrentThread(ServerThread th){
		 current = th;
	 }
	 
	 public ServerThread getCurrentThread(){
		 return current;
	 }
	 
	 private void connect(){
			Thread th = new Thread(new Runnable() {
				public void run(){
					while(true){
						try{
							System.out.println("사용자의 접속을 기다립니다..\n");
							socket = server_socket.accept();
							System.out.println("Success!");
							
							ServerThread servThread = new ServerThread(socket);
							setCurrentThread(servThread);
							servThread.start();
						}catch(IOException e){
							System.out.println("에헤이!! 다시 시도하세요.");
						}
					}
				}
			});
			th.start();
		}
	 /******************************************
	  * ServerThread 클래스에서 대부분의 기능이 수행된다.
	  * 편의상 내부클래스로 구현했다.
	  ******************************************/
	 class ServerThread extends Thread{
		 private InputStream is;
			private OutputStream os;
			private DataInputStream dis;
			private DataOutputStream dos;
			private Socket thread_socket;
			Thread thread;
			private StringBuffer buf = new StringBuffer(4096);
			
			
			
			public ServerThread(Socket newsock)
			{
				this.thread_socket = newsock;
				thread = this;
				User newuser = new User();
				newuser.setUser("123", "456", null);
				all_user.add(newuser);
				Site newsite = new Site();
				newsite.setSite("네이버", "www.naver.com", 0);
				newuser.addSite(newsite);
				Site newsite2 = new Site();
				newsite2.setSite("다음", "www.daum.net", 0);
				newuser.addSite(newsite2);
				Site newsite3 = new Site();
				newsite3.setSite("구글", "www.google.co.kr", 0);
				newuser.addSite(newsite3);
				Site newsite4 = new Site();
				newsite4.setSite("유튜브", "www.youtube.com", 0);
				newuser.addSite(newsite4);
				Site newsite5 = new Site();
				newsite5.setSite("MSDN", "msdn.microsoft.com", 0);
				newuser.addSite(newsite5);
				setStream();
			}
			
			public ServerThread(){
				this.thread_socket = new Socket();
				thread = this;
				setStream();
			}
			
			private void setStream(){
				try{
					is = thread_socket.getInputStream();
					dis = new DataInputStream(is);
					os = thread_socket.getOutputStream();
					dos = new DataOutputStream(os);
				} catch(IOException e){
					System.out.println("Stream 설정 에러!\n");
				}
			}
			
			//쓰레드가 시작되면 run함수에서 돈다. 클라이언트가 메세지를 보내기를 기다렸다가 받으면 inmessage()함수에서
			//type별로 기능을 수행한다.
			public void run()
			{/*
				User newuser = new User();
				newuser.setUser("123", "456", null);
				all_user.add(newuser);
				Site newsite = new Site();
				newsite.setSite("네이버", "www.naver.com", 0);
				newuser.addSite(newsite);
				Site newsite2 = new Site();
				newsite2.setSite("다음", "www.daum.net", 0);
				newuser.addSite(newsite2);
				Site newsite3 = new Site();
				newsite3.setSite("구글", "www.google.co.kr", 0);
				newuser.addSite(newsite3);
				Site newsite4 = new Site();
				newsite4.setSite("유튜브", "www.youtube.com", 0);
				newuser.addSite(newsite4);
				Site newsite5 = new Site();
				newsite5.setSite("MSDN", "msdn.microsoft.com", 0);
				newuser.addSite(newsite5);
				*/
				try{
					Thread currentThread = Thread.currentThread();
					
					while(currentThread == thread){
						String msg = dis.readUTF();
						System.out.println("received message : " + msg);
						inmessage(msg);
					}
				}catch(IOException e){
					System.out.println("Fail");
				}catch(NoSuchPaddingException e){}
				catch(NoSuchAlgorithmException e){}
				catch(InvalidKeyException e){}
				catch(BadPaddingException e){}
				catch(IllegalBlockSizeException e){}
				//catch(UnsupportedEncodingException e){}
				catch(InvalidKeySpecException e){}
			}
			
			//로그인 함수.
			//회원가입한 회원 중에 id, password가 일치하는 회원이 있다면 세션키를 만들고 로그인이 완료되었다는 메세지를 보낸다.
			private void login(JSONObject ob) throws InvalidKeyException, InvalidKeySpecException, NoSuchAlgorithmException{
				String id = (String)ob.get("id");
				String password = (String)ob.get("pw");
				System.out.println("Login");
				for(int i = 0; i < all_user.size(); i++){
					User newuser = new User();
					newuser = all_user.elementAt(i);
					System.out.println(newuser.getID());
					if(newuser.getID().equals(id) && newuser.getPassword().equals(password)){
						//success
						Key session_key = makeSessionKey("DES", ByteUtils.toBytes(RandomStringUtils.random(16, "ABCD"), 16));
						//newuser.setSessionKey(session_key);
						all_user.elementAt(i).setSessionKey(session_key);
						System.out.println("/////////////^^");
						System.out.println(all_user.elementAt(i).getSessionKey());
						System.out.println("/////////////");
						print();
						
						byte[] bKey = session_key.getEncoded();
						String plain = ByteUtils.toHexString(bKey);
						newuser.setKey(plain);
						JSONObject newobj = new JSONObject();
						newobj.put("type", "login_RE");
						newobj.put("result", "success");
						newobj.put("session_key", plain);
						
						sendMessage(newobj.toString());
					}
					else{
						//fail
						JSONObject newobj = new JSONObject();
						newobj.put("type", "login_RE");
						newobj.put("result", "fail");
						newobj.put("session_key", "none");
						
						sendMessage(newobj.toString());
					}
				}
			}
			
			//회원가입 함수.
			//새로운 사용자를 추가하고, 가능한 사이트 리스트 역시 추가해준다.
			private void register(JSONObject ob){
				String id = (String)ob.get("id");
				String password = (String)ob.get("pw");
				
				User newuser = new User();
				newuser.setUser(id, password, null);
				
				Site newsite = new Site();
				newsite.setSite("네이버", "www.naver.com", 0);
				newuser.addSite(newsite);
				newsite.setSite("다음", "www.daum.net", 0);
				newuser.addSite(newsite);
				newsite.setSite("구글", "www.google.co.kr", 0);
				newuser.addSite(newsite);
				newsite.setSite("유튜브", "www.youtube.com", 0);
				newuser.addSite(newsite);
				newsite.setSite("MSDN", "msdn.microsoft.com", 0);
				newuser.addSite(newsite);
				
				all_user.add(newuser);
				
				//json 데이터 응답
				
			}
			
			//세션키 생성 함수.
			//대문자, 소문자, 숫자를 무작위로 조합하여 16자리 세션키를 생성하는 함수이다.
			public Key makeSessionKey(String algorithm, byte[] keyData) throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {

			       String upper = StringUtils.upperCase(algorithm);
			       System.out.println("makesession////////////");
			       print();
			       if ("DES".equals(upper)) {
			           KeySpec keySpec = new DESKeySpec(keyData);
			           SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(algorithm);
			           SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
			           return secretKey;

			       } else if ("DESede".equals(upper) || "TripleDES".equals(upper)) {
			           KeySpec keySpec = new DESedeKeySpec(keyData);
			           SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(algorithm);
			           SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
			           return secretKey;
			       } else {
			           SecretKeySpec keySpec = new SecretKeySpec(keyData, algorithm);
			           return keySpec;
			       }
			   }
			
			//가능한 사이트 목록을 보내주는 함수.
			//사용자 각각은 본인만의 사이트 목록을 가지고 있으며, 로그인한 사용자의 사이트 목록을 보내주면 된다.
			private void search(JSONObject ob){
				String id = (String)ob.get("id");
				boolean check = false;
				User current = new User();
				
				for(int i = 0; i < all_user.size(); i++){
					if(all_user.elementAt(i).getID().equals(id)){
						check = true;
						current = all_user.elementAt(i);
						
					}
				}
				
				if(check == false){
					//fail
					
					JSONObject newobj = new JSONObject();
					newobj.put("type", "search_RE");
					newobj.put("content", "fail");
					
					sendMessage(newobj.toString());
				}
				else if(check == true){
					//success
					String content = "5$$";
					Vector<Site> newsite = new Vector<Site>();
					newsite = current.getSiteList();
					
					for(int i = 0; i < newsite.size(); i++){
						content = content + newsite.elementAt(i).getSiteName() + "$$";
						content = content + newsite.elementAt(i).getSiteURL() + "$$";
						content = content+  newsite.elementAt(i).getCheck() + "$$";
					}
					
					JSONObject newobj = new JSONObject();
					newobj.put("type", "search_RE");
					newobj.put("content", content);
					
					sendMessage(newobj.toString());
				}
			}
			
			//OTP 비밀번호 생성하는 함수.
			//보고서에 적힌대로 세션키와 타임을 이용해 암호화 하여 OTP 비밀번호를 생성하고, 클라이언트에게 값을 보낸다.
			private void requestPassword(JSONObject ob) 
					throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException,
						IllegalBlockSizeException, UnsupportedEncodingException
			{
				String id = (String)ob.get("id");
	            boolean check = false;
	            User current = new User();
	            
	            for(int i = 0; i < all_user.size(); i++){
	               if(all_user.elementAt(i).getID().equals(id)){
	                  check = true;
	                  current.setSessionKey(all_user.elementAt(i).getSessionKey());
	                  System.out.println("/////////////");
	                  System.out.println( all_user.elementAt(i).getID());
	                  System.out.println( all_user.elementAt(i).getSessionKey());
	                 
	                  System.out.println("/////////////");
						System.out.println(current.getSessionKey());
						System.out.println("/////////////");
	               }
	            }
	           // System.out.println(current.getID());
	           // System.out.println(current.getSessionKey());
	            print();
	            if(check == false){}
	            else if(check == true){
	               //success
	           //    Key key = current.getSessionKey();
	          //     String a = current.getKey();
	             //  System.out.println("key ::::::: " + a);
	               //암호화 알고리즘(DES)
				//   String transformation = "DES/ECB/NoPadding";
	               //String transformation = "DES/ECB/PKCS5Padding";
				//   Cipher cipher = Cipher.getInstance(transformation);
				 //  cipher.init(Cipher.ENCRYPT_MODE, key);
				   
				   //String str = RandomStringUtils.random(8, "ABCD");
				  // byte[] plain = str.getBytes();
				  // byte[] encrypt = cipher.doFinal(plain);
				 //  System.out.println(encrypt);
				   //String byteToString = new String(encrypt, "UTF-8");
				 //  String temp = encrypt.toString();
	               JSONObject newobj = new JSONObject();
	               newobj.put("type", "password_RE");
	               newobj.put("result", "네이버$$" + "54s28e3faj");
	               
	               sendMessage(newobj.toString());
	            }
				//세션키와 타임을 이용한 암호화
			}
			
			//받은 메세지를 처리하는 함수.
			//클라이언트에게 받은 JSON메세지 중 type으로 기능을 분류하고, 해당 기능에 맞는 함수를 호출하여 실질적인 기능을 수행한다.
			private void inmessage(String str)
				throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException,
				IllegalBlockSizeException, UnsupportedEncodingException, InvalidKeySpecException
			{
				Object obj = JSONValue.parse(str);
				JSONObject object = (JSONObject)obj;
				
				String type = (String) object.get("type");
				
				if(type.equals("login_RQ")){
					login(object);
				}
				else if(type.equals("search_RQ")){
					search(object);
				}
				else if(type.equals("password_RQ")){
					requestPassword(object);
				}
				//추가
			}
			
			public void print(){
				for(int i = 0; i < all_user.size(); i++){
					System.out.println(all_user.elementAt(i).getID() + "  " + all_user.elementAt(i).getPassword() + "   "
							+ all_user.elementAt(i).getSessionKey() + "  " + all_user.elementAt(i).getSessionKey());
				}
			}
			//비번 값만 보내주면 됨.
			public String inmessage_web(String str){
				String result = "0x51387ed3";
				
				return result;
			}
			
			private void sendMessage(String msg){
				try{
					System.out.println("response : " + msg);
					dos.writeUTF(msg);
					dos.flush();
					System.out.println("server send 완료");
				}catch(IOException e){
					e.printStackTrace();
				}
			}
	 }
	public static void main(String[] args) {
		new AndroidSocket();
	}
}
