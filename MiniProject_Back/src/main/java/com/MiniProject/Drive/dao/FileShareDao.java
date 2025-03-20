package com.MiniProject.Drive.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.MiniProject.Drive.dto.FileShare;

@Mapper
public interface FileShareDao {
	public void createShareURL(FileShare fs) throws Exception;
	
	public void deleteShareURL(FileShare fs) throws Exception;

	public void createShareUser(FileShare fs) throws Exception;

	public FileShare selectShareURL(String token) throws Exception;

	public FileShare selectShareUser(FileShare fs) throws Exception;

	public FileShare[] selectShareFile(FileShare fs) throws Exception;

	public List<String> selectFileIdsByUserId(String shareId) throws Exception;

	public void deleteShareUser(FileShare fs) throws Exception;
}
