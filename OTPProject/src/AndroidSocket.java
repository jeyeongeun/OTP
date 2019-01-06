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
 * Ŭ���� �� : AndroidSocket
 * ���� : �ȵ���̵�� ��������� ���� Ŭ�����̴�.
 * ��Ʈ �ƹ��ų� ��� ���� ��Ƽ� ������ ������ �ȴ�.
 *****************************************/
public class AndroidSocket {
	
	Vector<User> all_user = new Vector<User>();	//��� ����� ����Ʈ
	
	
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
				System.out.println("������ �����մϴ�...");
				connect();
			} catch(IOException e){
				System.out.println("�̹� ��� ���� ��Ʈ�Դϴ�.");
			} catch(Exception e) {
				System.out.println("�߸� �Է��Ͽ����ϴ�.");
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
							System.out.println("������� ������ ��ٸ��ϴ�..\n");
							socket = server_socket.accept();
							System.out.println("Success!");
							
							ServerThread servThread = new ServerThread(socket);
							setCurrentThread(servThread);
							servThread.start();
						}catch(IOException e){
							System.out.println("������!! �ٽ� �õ��ϼ���.");
						}
					}
				}
			});
			th.start();
		}
	 /******************************************
	  * ServerThread Ŭ�������� ��κ��� ����� ����ȴ�.
	  * ���ǻ� ����Ŭ������ �����ߴ�.
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
				newsite.setSite("���̹�", "www.naver.com", 0);
				newuser.addSite(newsite);
				Site newsite2 = new Site();
				newsite2.setSite("����", "www.daum.net", 0);
				newuser.addSite(newsite2);
				Site newsite3 = new Site();
				newsite3.setSite("����", "www.google.co.kr", 0);
				newuser.addSite(newsite3);
				Site newsite4 = new Site();
				newsite4.setSite("��Ʃ��", "www.youtube.com", 0);
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
					System.out.println("Stream ���� ����!\n");
				}
			}
			
			//�����尡 ���۵Ǹ� run�Լ����� ����. Ŭ���̾�Ʈ�� �޼����� �����⸦ ��ٷȴٰ� ������ inmessage()�Լ�����
			//type���� ����� �����Ѵ�.
			public void run()
			{/*
				User newuser = new User();
				newuser.setUser("123", "456", null);
				all_user.add(newuser);
				Site newsite = new Site();
				newsite.setSite("���̹�", "www.naver.com", 0);
				newuser.addSite(newsite);
				Site newsite2 = new Site();
				newsite2.setSite("����", "www.daum.net", 0);
				newuser.addSite(newsite2);
				Site newsite3 = new Site();
				newsite3.setSite("����", "www.google.co.kr", 0);
				newuser.addSite(newsite3);
				Site newsite4 = new Site();
				newsite4.setSite("��Ʃ��", "www.youtube.com", 0);
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
			
			//�α��� �Լ�.
			//ȸ�������� ȸ�� �߿� id, password�� ��ġ�ϴ� ȸ���� �ִٸ� ����Ű�� ����� �α����� �Ϸ�Ǿ��ٴ� �޼����� ������.
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
			
			//ȸ������ �Լ�.
			//���ο� ����ڸ� �߰��ϰ�, ������ ����Ʈ ����Ʈ ���� �߰����ش�.
			private void register(JSONObject ob){
				String id = (String)ob.get("id");
				String password = (String)ob.get("pw");
				
				User newuser = new User();
				newuser.setUser(id, password, null);
				
				Site newsite = new Site();
				newsite.setSite("���̹�", "www.naver.com", 0);
				newuser.addSite(newsite);
				newsite.setSite("����", "www.daum.net", 0);
				newuser.addSite(newsite);
				newsite.setSite("����", "www.google.co.kr", 0);
				newuser.addSite(newsite);
				newsite.setSite("��Ʃ��", "www.youtube.com", 0);
				newuser.addSite(newsite);
				newsite.setSite("MSDN", "msdn.microsoft.com", 0);
				newuser.addSite(newsite);
				
				all_user.add(newuser);
				
				//json ������ ����
				
			}
			
			//����Ű ���� �Լ�.
			//�빮��, �ҹ���, ���ڸ� �������� �����Ͽ� 16�ڸ� ����Ű�� �����ϴ� �Լ��̴�.
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
			
			//������ ����Ʈ ����� �����ִ� �Լ�.
			//����� ������ ���θ��� ����Ʈ ����� ������ ������, �α����� ������� ����Ʈ ����� �����ָ� �ȴ�.
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
			
			//OTP ��й�ȣ �����ϴ� �Լ�.
			//������ ������� ����Ű�� Ÿ���� �̿��� ��ȣȭ �Ͽ� OTP ��й�ȣ�� �����ϰ�, Ŭ���̾�Ʈ���� ���� ������.
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
	               //��ȣȭ �˰���(DES)
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
	               newobj.put("result", "���̹�$$" + "54s28e3faj");
	               
	               sendMessage(newobj.toString());
	            }
				//����Ű�� Ÿ���� �̿��� ��ȣȭ
			}
			
			//���� �޼����� ó���ϴ� �Լ�.
			//Ŭ���̾�Ʈ���� ���� JSON�޼��� �� type���� ����� �з��ϰ�, �ش� ��ɿ� �´� �Լ��� ȣ���Ͽ� �������� ����� �����Ѵ�.
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
				//�߰�
			}
			
			public void print(){
				for(int i = 0; i < all_user.size(); i++){
					System.out.println(all_user.elementAt(i).getID() + "  " + all_user.elementAt(i).getPassword() + "   "
							+ all_user.elementAt(i).getSessionKey() + "  " + all_user.elementAt(i).getSessionKey());
				}
			}
			//��� ���� �����ָ� ��.
			public String inmessage_web(String str){
				String result = "0x51387ed3";
				
				return result;
			}
			
			private void sendMessage(String msg){
				try{
					System.out.println("response : " + msg);
					dos.writeUTF(msg);
					dos.flush();
					System.out.println("server send �Ϸ�");
				}catch(IOException e){
					e.printStackTrace();
				}
			}
	 }
	public static void main(String[] args) {
		new AndroidSocket();
	}
}
