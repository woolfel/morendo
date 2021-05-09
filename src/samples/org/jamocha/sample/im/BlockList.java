package org.jamocha.sample.im;

import java.util.List;

public class BlockList {

	private String userId;
	private List blocked;
	
	public BlockList() {
		super();
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public List getBlocked() {
		return blocked;
	}

	public void setBlocked(List blocked) {
		this.blocked = blocked;
	}

}
