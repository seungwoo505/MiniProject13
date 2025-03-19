package com.MiniProject.Drive.dao;

import org.apache.ibatis.annotations.Mapper;

import com.MiniProject.Drive.dto.SaltInfo;

@Mapper
public interface SaltDao {

	public void insertSalt(SaltInfo saltInfo) throws Exception;

	public SaltInfo selectSalt(String email) throws Exception;

}