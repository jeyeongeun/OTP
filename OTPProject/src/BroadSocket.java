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
  * Ŭ���� �� : Broadsocket
  * ���� : ������ ����� ����ϴ� �������� �ٷ�� Ŭ�����̴�.
  * http����̳� ajax ��ĵ� ������ �������� �����͸��� �����ϱ� ������ �����ϰ� ������.
  * ������ ���� ������ websockt.html ���Ϸ� ���� Ȯ�� �����ϴ�.
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
 		System.out.println(message);		//���⼭ �޼����� Ŭ���̾�Ʈ�� ���� �޼����̴�.
 		String result = "";
 		result = sock.getCurrentThread().inmessage_web(message);
 		synchronized (clients) {
 			for (Session client : clients) { 
 				if (client.equals(session)) { 
 					System.out.println("Ŭ���̾�Ʈ ����.");
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
