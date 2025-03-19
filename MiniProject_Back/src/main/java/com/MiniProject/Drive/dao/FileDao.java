package com.MiniProject.Drive.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.MiniProject.Drive.dto.Comment;
import com.MiniProject.Drive.dto.MyFile;

@Mapper
public interface FileDao {
	
	public void uploadFile(MyFile f) throws Exception;
	
	public MyFile downloadFile(Map<String, String> map) throws Exception;
	
	public void updateFile(MyFile f) throws Exception;
	
	public void deleteFile(Map<String, String> map) throws Exception;
	
	public MyFile[] selectFile(Map<String, String> map) throws Exception;
	
	public MyFile findFile(Map<String, String> map) throws Exception;
	
	public void insertComment(Comment comment) throws Exception;
	
	public void deleteComment(Comment comment) throws Exception;
	
	public Comment[] selectComment(Comment comment) throws Exception;
	
	public List<File> selectFilesByIds(List<String> fileIds) throws Exception;
}