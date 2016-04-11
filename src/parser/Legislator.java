package parser;

import java.util.ArrayList;
import java.util.List;
import parser.Bill;

public class Legislator implements Comparable<Legislator> {
	private String id;
	private String firstName;
	private String lastName;
	private String state;
	private List<Bill> bills = new ArrayList<Bill>();
	
	public Legislator() {
		// TODO Auto-generated constructor stub
	}
	
	// Getters
	public String getId() {
		return id;
	}
	public String getFirstName() {
		return firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public String getState() {
		return state;
	}
	public List<Bill> getBills() {
		return bills;
	}
	
	// Setters
	public void setId(String value) {
		id = value;
	}
	public void setFirstName(String value) {
		firstName = value;
	}
	public void setLastName(String value) {
		lastName = value;
	}
	public void setState(String value) {
		state = value;
	}
	
	public int compareTo(Legislator other) {
		int myId = Integer.parseInt(this.getId());
		String otherIdString = other.getId();
		int otherId = Integer.parseInt(otherIdString);
		return myId - otherId;
	}
	
}
