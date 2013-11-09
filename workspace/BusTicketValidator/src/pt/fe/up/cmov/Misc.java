package pt.fe.up.cmov;

public enum Misc {
	INSTANCE;
	private String devID;
	private Misc(){
		
	}
	
	public String getDevId(){
		 return devID;
	}
	
	public void setDevID(String id){
		devID=id;
	}
}
