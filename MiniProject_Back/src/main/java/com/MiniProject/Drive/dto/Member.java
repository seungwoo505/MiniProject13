package com.MiniProject.Drive.dto;

import java.util.Date;

public class Member {

	private String userId, pwd;
	private Date registDate;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public Date getRegistDate() {
		return registDate;
	}
	public void setRegistDate(Date registDate) {
		this.registDate = registDate;
	}
	public Member() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Member(String userId, String pwd, Date registDate) {
		super();
		this.userId = userId;
		this.pwd = pwd;
		this.registDate = registDate;
	}
	@Override
	public String toString() {
		return "Member [userId=" + userId + ", pwd=" + pwd + ", registDate=" + registDate + "]";
	}

	
}
