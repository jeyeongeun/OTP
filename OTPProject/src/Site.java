
public class Site {
	private String site_name;
	private String site_url;
	private int check;
	
	public String getSiteName(){return site_name;}
	public String getSiteURL(){return site_url;}
	public int getCheck(){return check;}
	
	public void setSiteName(String _name){site_name = _name;}
	public void setSiteURL(String _url){site_url = _url;}
	public void setCheck(int _check){check = _check;}
	public void setSite(String _name, String _url, int _check){
		site_name = _name;
		site_url = _url;
		check = _check;
	}
}
