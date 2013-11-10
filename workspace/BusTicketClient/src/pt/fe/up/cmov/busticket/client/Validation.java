package pt.fe.up.cmov.busticket.client;

public class Validation {
	long _time;
	long _validationTime;
	String _busSignature;
	String _busID;
	
	
	public Validation(){
		
	}
	
	public Validation(long time, long validationTime, String busSignature, String busID){
		this._time = time;
		this._validationTime = validationTime;
		this._busSignature = busSignature;
		this._busID = busID;
	}

	public long getTime() {
		return _time;
	}

	public void setTime(long _time) {
		this._time = _time;
	}

	public long getValidationTime() {
		return _validationTime;
	}

	public void setValidationTime(long _validationTime) {
		this._validationTime = _validationTime;
	}

	public String getBusSignature() {
		return _busSignature;
	}

	public void setBusSignature(String _busSignature) {
		this._busSignature = _busSignature;
	}

	public String getBusID() {
		return _busID;
	}

	public void setBusID(String _busID) {
		this._busID = _busID;
	}

	
}
