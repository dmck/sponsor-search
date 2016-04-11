package parser;

public class Bill {
	private String sponsor;
	private String session;
	private String type;
	private String number;
	private boolean enacted;
	private String enactedNumber;
	
	public Bill() {
		// TODO Auto-generated constructor stub
	}
	
	public Bill(String sponsorValue, String sessionValue, String typeValue, String numberValue, boolean enactedValue) {
		sponsor = sponsorValue;
		session = sessionValue;
		type = typeValue;
		number = numberValue;
		enacted = enactedValue;
	}
	
	// Getters
	public String getSponsor() {
		return sponsor;
	}
	public String getSession() {
		return session;
	}
	public String getType() {
		return type;
	}
	public String getNumber() {
		return number;
	}
	public boolean getEnacted() {
		return enacted;
	}
	public String getEnactedNumber() {
		return enactedNumber;
	}
	
	//Setters
	public void setSponsor(String value) {
		this.sponsor = value;
	}
	public void setSession(String value) {
		this.session = value;
	}
	public void setType(String value) {
		this.type = value;
	}
	public void setNumber(String value) {
		this.number = value;
	}
	public void setEnacted(boolean value) {
		enacted = value;
	}
	public void setEnactedNumber(String value) {
		this.enactedNumber = value;
	}

}
