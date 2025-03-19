package com.MiniProject.Drive.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.MiniProject.Drive.dao.FileDao;
import com.MiniProject.Drive.dao.FileShareDao;
import com.MiniProject.Drive.dto.FileShare;
import com.MiniProject.Drive.dto.MyFile;
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
		
		fileShareDao.deleteShareUser(fs);
		fileShareDao.createShareURL(fs);

		return "http://127.0.0.1:5500/MiniProject_Front/Front/share/index.html?token=" + token;
	}

	public void createShareUser(FileShare fs) throws Exception{
		for(String s : fs.getShareIds()) {
			if(!s.equals("")) {
				fs.setShareId(s);
				fileShareDao.createShareUser(fs);
			}
		}
	}

	public FileShare selectShareURL(String token) throws Exception{
		FileShare fs = fileShareDao.selectShareURL(token);

		Map<String, String> map = new HashMap<>();
		map.put("fileId", fs.getFileId());
		MyFile myFile = fileDao.findFile(map);
		Security security = new Security();

		String originalFileName = security.decryptFileName(myFile.getSecurityName(), myFile.getSecurity());
		fs.setFileName(originalFileName);

		return fs;
	}

	public FileShare selectShareUser(FileShare fs) throws Exception{
		return fileShareDao.selectShareUser(fs);
	}

	public FileShare[] selectShareFile(FileShare fs) throws Exception{
		return fileShareDao.selectShareFile(fs);
	}

    public List<String> getFileIdsByUserId(String userId) throws Exception {
        return fileShareDao.selectFileIdsByUserId(userId);
    }
}