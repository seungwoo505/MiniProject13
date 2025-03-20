package com.MiniProject.Drive.dao;

import org.apache.ibatis.annotations.Mapper;

import com.MiniProject.Drive.dto.Member;

@Mapper
public interface MemberDao {

	public void signup(Member m) throws Exception;

	public Member login(Member m) throws Exception;

    public int getLoginCount(String userId) throws Exception;

    public void incrementLoginCount(String userId) throws Exception;

    public void resetLoginCount(String userId) throws Exception;

    public boolean isTimePassed(String userId) throws Exception;
}
