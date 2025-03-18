package com.MiniProject.Drive.dao;
import org.apache.ibatis.annotations.Mapper;
import com.MiniProject.Drive.dto.*;

@Mapper
public interface LoginDao {

	public void insertToken(Login login) throws Exception;

	public int deleteToken(String userId, String authorization) throws Exception;
	
	public Login logincheck(Login login) throws Exception;
}
