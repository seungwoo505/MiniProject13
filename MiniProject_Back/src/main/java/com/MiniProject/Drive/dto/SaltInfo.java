package com.MiniProject.Drive.dto;

public class SaltInfo {
	
	private String id, salt;

	public String getEmail() {
		return id;
	}

	public void setEmail(String id) {
		this.id = id;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public SaltInfo(String id, String salt) {
		super();
		this.id = id;
		this.salt = salt;
	}

	public SaltInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "SaltInfo [id=" + id + ", salt=" + salt + "]";
	}
	
	

}