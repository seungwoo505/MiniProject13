package com.MiniProject.Drive.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.MiniProject.Drive.dto.File;

@Mapper
public interface FileDao {
	
	public void uploadFile(File f) throws Exception;
	
	public File downloadFile(Map<String, String> map) throws Exception;
	
	public File[] selectFile(Map<String, String> map) throws Exception;
	
	public File findFile(Map<String, String> map) throws Exception;
	
	public List<File> selectFilesByIds(List<String> fileIds) throws Exception;
}