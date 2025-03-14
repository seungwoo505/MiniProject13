package com.MiniProject.Drive.dto;

import java.util.Date;

public class Member {

	private String id, pwd;
	private Date registDate;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public Member(String id, String pwd, Date registDate) {
		super();
		this.id = id;
		this.pwd = pwd;
		this.registDate = registDate;
	}
	@Override
	public String toString() {
		return "Member [id=" + id + ", pwd=" + pwd + ", registDate=" + registDate + "]";
	}
	
	
}
