package org.jamocha.sample.im;

import java.util.List;

public class BlockList {

	private String userId;
	@SuppressWarnings("rawtypes")
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

	@SuppressWarnings("rawtypes")
	public List getBlocked() {
		return blocked;
	}

	@SuppressWarnings("rawtypes")
	public void setBlocked(List blocked) {
		this.blocked = blocked;
	}

}
