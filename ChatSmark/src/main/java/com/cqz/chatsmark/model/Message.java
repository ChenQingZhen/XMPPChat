package com.cqz.chatsmark.model;

public class Message {
	public static final int TYPE_RECEIVE=0;
	public static final int TYPE_SEND=1;
	private int type;
	private String content;
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
}
