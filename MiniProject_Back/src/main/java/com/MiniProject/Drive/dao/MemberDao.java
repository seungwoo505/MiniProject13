package com.MiniProject.Drive.dao;

import org.apache.ibatis.annotations.Mapper;

import com.MiniProject.Drive.dto.Member;

@Mapper
public interface MemberDao {

	public void signup(Member m) throws Exception;

	public Member login(Member m) throws Exception;

}
