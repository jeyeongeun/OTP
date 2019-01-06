import java.io.IOException;
import java.util.Collections; 
import java.util.HashSet; 
import java.util.Set; 

import javax.websocket.OnClose; 
import javax.websocket.OnMessage; 
import javax.websocket.OnOpen; 
import javax.websocket.Session; 
import javax.websocket.server.ServerEndpoint; 
 
 /*****************************************
  * 클래스 명 : Broadsocket
  * 역할 : 웹과의 통신을 담당하는 웹소켓을 다루는 클래스이다.
  * http방식이나 ajax 방식도 있지만 웹소켓은 데이터만을 전달하기 때문에 간결하고 빠르다.
  * 웹소켓 전달 과정은 websockt.html 파일로 직접 확인 가능하다.
  *****************************************/
 @ServerEndpoint("/broadcasting") 
 public class BroadSocket { 
 
	 private static Set<Session> clients = Collections 
			 .synchronizedSet(new HashSet<Session>()); 
 	private AndroidSocket sock = new AndroidSocket();
	 
 	public BroadSocket(){
		 sock = new AndroidSocket();
	 }
	 
 	@OnMessage 
 	public void onMessage(String message, Session session) throws IOException { 
 		System.out.println(message);		//여기서 메세지는 클라이언트가 보낸 메세지이다.
 		String result = "";
 		result = sock.getCurrentThread().inmessage_web(message);
 		synchronized (clients) {
 			for (Session client : clients) { 
 				if (client.equals(session)) { 
 					System.out.println("클라이언트 있음.");
 					client.getBasicRemote().sendText(result); 
 				} 
 			} 
 		} 
 	} 
 
 
 	@OnOpen 
 	public void onOpen(Session session) { 
 		// Add session to the connected sessions set 
 		System.out.println(session);
 		clients.add(session); 
 	} 

 
 	@OnClose 
 	public void onClose(Session session) { 
		// Remove session from the connected sessions set
 		clients.remove(session);
 	} 
 	
 } 
