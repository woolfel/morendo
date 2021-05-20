package org.jamocha.sample.im;

import java.util.List;

public class BuddyList {

	private String userId;
	@SuppressWarnings("rawtypes")
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

	@SuppressWarnings("rawtypes")
	public List getBuddies() {
		return buddies;
	}

	@SuppressWarnings("rawtypes")
	public void setBuddies(List buddies) {
		this.buddies = buddies;
	}

	@SuppressWarnings("unchecked")
	public void addBuddy(String userid) {
		this.buddies.add(userid);
	}
}
