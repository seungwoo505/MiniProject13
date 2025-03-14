package com.MiniProject.Drive.dto;

public class Login {
	private String id, token;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public Login(String id, String token) {
		super();
		this.id = id;
		this.token = token;
	}

	@Override
	public String toString() {
		return "Login [id=" + id + ", token=" + token + "]";
	}
	
	

}
