package com.MiniProject.Drive.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.MiniProject.Drive.dao.FileDao;
import com.MiniProject.Drive.dao.FileShareDao;
import com.MiniProject.Drive.dto.File;
import com.MiniProject.Drive.dto.FileShare;
import com.MiniProject.Drive.secu.Security;

@Service
public class FileShareService {
	@Autowired
	FileShareDao fileShareDao;
	
	@Autowired
	FileDao fileDao;
	
	public String createShareURL(FileShare fs) throws Exception{
		String token = UUID.randomUUID().toString();
		
		fs.setToken(token);
		
		fileShareDao.createShareURL(fs);
		
		return "http://127.0.0.1:5500/MiniPoject_Front/share/select.html?token=" + token;
	}
	
	public void createShareUser(FileShare fs) throws Exception{
		fileShareDao.createShareUser(fs);
	}
	
	public FileShare selectShareURL(String token) throws Exception{
		FileShare fs = fileShareDao.selectShareURL(token);
		
		Map<String, String> map = new HashMap<>();
		map.put("fileId", fs.getFileId());
		File file = fileDao.findFile(map);
		Security security = new Security();
		
		String originalFileName = security.decryptFileName(file.getSecurityName(), file.getSecurity());
		fs.setFileName(originalFileName);
		
		return fs;
	}
	
	public FileShare selectShareUser(FileShare fs) throws Exception{
		return fileShareDao.selectShareUser(fs);
	}
	
	public FileShare[] selectShareFile(FileShare fs) throws Exception{
		return fileShareDao.selectShareFile(fs);
	}
}