package com.MiniProject.Drive.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.MiniProject.Drive.dao.FileDao;
import com.MiniProject.Drive.dto.Comment;
import com.MiniProject.Drive.dto.MyFile;

@Service
public class FileService {
	@Autowired
	FileDao fileDao;
	
	public void uploadFile(MyFile f) throws Exception{
		fileDao.uploadFile(f);
	}
	
	public MyFile downloadFile(Map<String, String> map) throws Exception{
		return fileDao.downloadFile(map);
	}
	
	public void deleteFile(Map<String, String> map) throws Exception{
		fileDao.deleteFile(map);
	}
	
	public void updateFile(MyFile f) throws Exception{
		fileDao.updateFile(f);
	}
	
	public MyFile[] selectFile(Map<String, String> map) throws Exception{
		return fileDao.selectFile(map);
	}
	
	public MyFile findFile(Map<String, String> map) throws Exception{
		return fileDao.findFile(map);
	}

	public void insertComment(Comment comment) throws Exception{
		fileDao.insertComment(comment);
	}
	
	public void deleteComment(Comment comment) throws Exception{
		fileDao.deleteComment(comment);
	}
	
	public Comment[] selectComment(Comment comment) throws Exception{
		return fileDao.selectComment(comment);
	}
  
  public List<MyFile> getFilesByIds(List<String> fileIds) throws Exception {
    return fileDao.selectFilesByIds(fileIds);
  }
}
