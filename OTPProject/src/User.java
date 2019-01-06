import java.security.Key;

import java.util.Vector;

public class User {
	private String id;
	private String password;
	private Key session_key;
	private String key;
	private Vector<Site> siteList = new Vector<Site>();
	
	public String getID(){return id;}
	public String getPassword(){return password;}
	public Key getSessionKey(){return session_key;}
	public Vector<Site> getSiteList(){return siteList;}
	public String getKey(){return key;}
	
	public void setID(String _id){id = _id;}
	public void setPassword(String _password){password = _password;}
	public void setSessionKey(Key _session){session_key = _session;}
	public void setKey(String _key){key = _key;}
	public void setUser(String _id, String _password, Key _session){
		id = _id;
		password = _password;
		session_key = _session;
	}
	
	public void addSite(Site newsite){
		siteList.add(newsite);
	}
}
