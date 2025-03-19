package com.MiniProject.Drive.dto;

import java.util.Date;

public class Comment {
	private int id, fileId;
	private String writer, comment;
	private Date createDate;

	public Comment() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Comment(int id, int fileId, String writer, String comment, Date createDate) {
		super();
		this.id = id;
		this.fileId = fileId;
		this.writer = writer;
		this.comment = comment;
		this.createDate = createDate;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getFileId() {
		return fileId;
	}
	public void setFileId(int fileId) {
		this.fileId = fileId;
	}
	public String getWriter() {
		return writer;
	}
	public void setWriter(String writer) {
		this.writer = writer;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	@Override
	public String toString() {
		return "Comment [id=" + id + ", fileId=" + fileId + ", writer=" + writer + ", comment=" + comment
				+ ", createDate=" + createDate + "]";
	}
}
