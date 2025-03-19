package com.MiniProject.Drive.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.MiniProject.Drive.dao.FileDao;
import com.MiniProject.Drive.dto.File;

@Service
public class FileService {
	@Autowired
	FileDao fileDao;
	
	public void uploadFile(File f) throws Exception{
		fileDao.uploadFile(f);
	}
	
	public File downloadFile(Map<String, String> map) throws Exception{
		return fileDao.downloadFile(map);
	}
	
	public File[] selectFile(Map<String, String> map) throws Exception{
		return fileDao.selectFile(map);
	}
	
	public File findFile(Map<String, String> map) throws Exception{
		return fileDao.findFile(map);
	}
	
    public List<File> getFilesByIds(List<String> fileIds) throws Exception {
        return fileDao.selectFilesByIds(fileIds);
    }
}
