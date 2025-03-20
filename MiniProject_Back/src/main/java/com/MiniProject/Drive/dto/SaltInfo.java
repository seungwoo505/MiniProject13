package com.MiniProject.Drive.dto;

public class SaltInfo {

	private String userId, salt;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public SaltInfo() {
		super();
	}

	public SaltInfo(String userId, String salt) {
		super();
		this.userId = userId;
		this.salt = salt;
	}

	@Override
	public String toString() {
		return "SaltInfo [userId=" + userId + ", salt=" + salt + "]";
	}



}