package org.jamocha.sample.im;

import java.util.Calendar;
import java.util.List;

public class Message {
	public static final int QUEUED = 100;
	public static final int SENT = 200;
	public static final int DELAYED = 300;
	public static final int RECEIVED = 400;
	public static final int RETURNED = 500;
	
	private String senderId;
	private String receiverId;
	private String text;
	private List images;
	private List files;
	private Calendar sendTime;
	private int messageStatus;
	
	public Message() {
		super();
	}
	
	public String getSenderId() {
		return senderId;
	}
	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}
	public String getReceiverId() {
		return receiverId;
	}
	public void setReceiverId(String receiverId) {
		this.receiverId = receiverId;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public List getImages() {
		return images;
	}
	public void setImages(List images) {
		this.images = images;
	}
	public List getFiles() {
		return files;
	}
	public void setFiles(List files) {
		this.files = files;
	}
	public Calendar getSendTime() {
		return sendTime;
	}
	public void setSendTime(Calendar sendTime) {
		this.sendTime = sendTime;
	}
	public int getMessageStatus() {
		return messageStatus;
	}
	public void setMessageStatus(int messageStatus) {
		this.messageStatus = messageStatus;
	}
}
