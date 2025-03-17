package com.MiniProject.Drive.dto;

import java.util.Date;

public class FileShare {
	private String userId, token, fileId, fileName, shareId;
	private boolean shareUser;
	private Date expirationTime;
	
	public FileShare() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public FileShare(String userId, String token, String fileId, String fileName, String shareId, boolean shareUser,
			Date expirationTime) {
		super();
		this.userId = userId;
		this.token = token;
		this.fileId = fileId;
		this.fileName = fileName;
		this.shareId = shareId;
		this.shareUser = shareUser;
		this.expirationTime = expirationTime;
	}
	
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
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getShareId() {
		return shareId;
	}
	public void setShareId(String shareId) {
		this.shareId = shareId;
	}
	public boolean isShareUser() {
		return shareUser;
	}
	public void setShareUser(boolean shareUser) {
		this.shareUser = shareUser;
	}
	public Date getExpirationTime() {
		return expirationTime;
	}
	public void setExpirationTime(Date expirationTime) {
		this.expirationTime = expirationTime;
	}

	@Override
	public String toString() {
		return "FileShare [userId=" + userId + ", token=" + token + ", fileId=" + fileId + ", fileName=" + fileName
				+ ", shareId=" + shareId + ", shareUser=" + shareUser + ", expirationTime=" + expirationTime + "]";
	}
}