package com.MiniProject.Drive.dto;

import java.util.Arrays;
import java.util.Date;

public class MyFile {
	private String FileId, userId, securityName, securityDetail;
	private byte[] security, security2;
	private Date uploadDate, updateDate;
	
	public MyFile() {
		super();
		// TODO Auto-generated constructor stub
	}
	public MyFile(String fileId, String userId, String securityName, String securityDetail, byte[] security,
			byte[] security2, Date uploadDate, Date updateDate) {
		super();
		FileId = fileId;
		this.userId = userId;
		this.securityName = securityName;
		this.securityDetail = securityDetail;
		this.security = security;
		this.security2 = security2;
		this.uploadDate = uploadDate;
		this.updateDate = updateDate;
	}
	
	public String getFileId() {
		return FileId;
	}
	public void setFileId(String fileId) {
		FileId = fileId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getSecurityName() {
		return securityName;
	}
	public void setSecurityName(String securityName) {
		this.securityName = securityName;
	}
	public String getSecurityDetail() {
		return securityDetail;
	}
	public void setSecurityDetail(String securityDetail) {
		this.securityDetail = securityDetail;
	}
	public byte[] getSecurity() {
		return security;
	}
	public void setSecurity(byte[] security) {
		this.security = security;
	}
	public byte[] getSecurity2() {
		return security2;
	}
	public void setSecurity2(byte[] security2) {
		this.security2 = security2;
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
		return "File [FileId=" + FileId + ", userId=" + userId + ", securityName=" + securityName + ", securityDetail="
				+ securityDetail + ", security=" + Arrays.toString(security) + ", security2="
				+ Arrays.toString(security2) + ", uploadDate=" + uploadDate + ", updateDate=" + updateDate + "]";
	}
}
