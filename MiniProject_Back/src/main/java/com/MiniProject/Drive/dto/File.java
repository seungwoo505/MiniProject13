package com.MiniProject.Drive.dto;

import java.util.Arrays;
import java.util.Date;

public class File {
	private String userId, fileName, securityName;
	private byte[] security;
	private Date uploadDate, updateDate;
	
	public File() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public File(String userId, String fileName, String securityName, byte[] security, Date uploadDate,
			Date updateDate) {
		super();
		this.userId = userId;
		this.fileName = fileName;
		this.securityName = securityName;
		this.security = security;
		this.uploadDate = uploadDate;
		this.updateDate = updateDate;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getSecurityName() {
		return securityName;
	}
	public void setSecurityName(String securityName) {
		this.securityName = securityName;
	}
	public byte[] getSecurity() {
		return security;
	}
	public void setSecurity(byte[] security) {
		this.security = security;
	}
	public Date getUploadDate() {
		return uploadDate;
	}
	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	@Override
	public String toString() {
		return "File [userId=" + userId + ", fileName=" + fileName + ", securityName=" + securityName + ", security="
				+ Arrays.toString(security) + ", uploadDate=" + uploadDate + ", updateDate=" + updateDate + "]";
	}
}
