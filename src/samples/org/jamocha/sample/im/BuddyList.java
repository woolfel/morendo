package org.jamocha.sample.im;

import java.util.List;

public class BuddyList {

	private String userId;
	private List buddies;
	
	public BuddyList() {
		super();
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public List getBuddies() {
		return buddies;
	}

	public void setBuddies(List buddies) {
		this.buddies = buddies;
	}

	public void addBuddy(String userid) {
		this.buddies.add(userid);
	}
}
