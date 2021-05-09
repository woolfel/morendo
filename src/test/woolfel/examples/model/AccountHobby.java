package woolfel.examples.model;

import java.util.Date;

public class AccountHobby {
	private String accountId;
	private String hobbyCode;
	private Date lastUpdate;
	private int rating;
	
	public AccountHobby() {
		super();
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getHobbyCode() {
		return hobbyCode;
	}

	public void setHobbyCode(String hobbyCode) {
		this.hobbyCode = hobbyCode;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}
}
