package com.MiniProject.Drive.service;

import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.MiniProject.Drive.dao.*;
import com.MiniProject.Drive.dto.*;
import com.MiniProject.Drive.secu.OpenCrypt;

@Service
public class MemberService {
	
	@Autowired
	MemberDao memberDao;
	
	@Autowired
	LoginDao loginDao;
	
	@Autowired
	SaltDao saltDao;
	
	public Member login(Member m) throws Exception {
		return memberDao.login(m);
	}
	
	public void signup(Member m) throws Exception {
	    String id = m.getId();
	    if (!isValidId(id)) {
	        throw new Exception("아이디는 5글자 이상, 영어만와 숫자만 사용해주세요");
	    }
	    
	    String pwd = m.getPwd();
	    if (!isValidPassword(pwd)) {
	        throw new Exception("패스워드는 8자리 이상이어야 하며, 특수문자와 숫자를 포함해야 합니다.");
	    }
	    
	    String salt = UUID.randomUUID().toString();

	    byte[] originalHash = OpenCrypt.getSHA256(pwd, salt);
	    String pwdHash = OpenCrypt.byteArrayToHex(originalHash);
	    
	    m.setPwd(pwdHash);
	    
	    saltDao.insertSalt(new SaltInfo(id, salt));
	    
	    memberDao.signup(m);
	}
	
	
	public Login tokenLogin(Member m) throws Exception {
		String id=m.getId();
		SaltInfo saltInfo=saltDao.selectSalt(id);
		String pwd=m.getPwd();
		byte [] pwdHash=OpenCrypt.getSHA256(pwd, saltInfo.getSalt());
		String pwdHashHex=OpenCrypt.byteArrayToHex(pwdHash);
		m.setPwd(pwdHashHex);
		m=memberDao.login(m);
	
		if(m!=null) {
				String salt=UUID.randomUUID().toString();
				byte[] originalHash=OpenCrypt.getSHA256(id, salt);
				String myToken=OpenCrypt.byteArrayToHex(originalHash);
				Login loginInfo=new Login(id, myToken);
				loginDao.insertToken(loginInfo);
				return loginInfo;
			}
	
		
		return null;		 
	}
    
	public void logout(String id, String authorization) throws Exception {
	    int affectedRows = loginDao.deleteToken(id, authorization);
	    if (affectedRows == 0) {
	        throw new Exception("토큰이 존재하지 않습니다.");
	    }
	}
	
	
	private boolean isValidId(String id) {
	    if (id == null || id.trim().isEmpty()) return false;
	    String idPattern = "^[a-zA-Z0-9]{5,}$";
	    return Pattern.matches(idPattern, id);
	}
	

	private boolean isValidPassword(String password) {
	    String passwordPattern = "^(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$";
	    return Pattern.matches(passwordPattern, password);
	}

}
