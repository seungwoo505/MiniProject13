package com.MiniProject.Drive.dto;

public class Login {
	private String userId, token;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Login() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Login(String userId, String token) {
		super();
		this.userId = userId;
		this.token = token;
	}

	@Override
	public String toString() {
		return "Login [userId=" + userId + ", token=" + token + "]";
	}
	
	

}
