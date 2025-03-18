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
	    String userId = m.getUserId();
	    if (!isValidId(userId)) {
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
	    
	    saltDao.insertSalt(new SaltInfo(userId, salt));
	    
	    memberDao.signup(m);
	}
	
	
	public Login tokenLogin(Member m) throws Exception {
		String userId=m.getUserId();
		SaltInfo saltInfo=saltDao.selectSalt(userId);
		if(saltInfo == null) {
			return null;
		}
		String pwd=m.getPwd();
		byte [] pwdHash=OpenCrypt.getSHA256(pwd, saltInfo.getSalt());
		String pwdHashHex=OpenCrypt.byteArrayToHex(pwdHash);
		m.setPwd(pwdHashHex);
		m=memberDao.login(m);
	
		if(m!=null) {
				String salt=UUID.randomUUID().toString();
				byte[] originalHash=OpenCrypt.getSHA256(userId, salt);
				String myToken=OpenCrypt.byteArrayToHex(originalHash);
				Login loginInfo=new Login(userId, myToken);
				loginDao.insertToken(loginInfo);
				return loginInfo;
			}
		
		return null;		 
	}
    
	public void logout(String userId, String authorization) throws Exception {
	    int affectedRows = loginDao.deleteToken(userId, authorization);
	    if (affectedRows == 0) {
	        throw new Exception("토큰이 존재하지 않습니다.");
	    }
	}
	
	
	private boolean isValidId(String userId) {
	    if (userId == null || userId.trim().isEmpty()) return false;
	    String idPattern = "^[a-zA-Z0-9]{5,}$";
	    return Pattern.matches(idPattern, userId);
	}
	

	private boolean isValidPassword(String password) {
	    String passwordPattern = "^(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$";
	    return Pattern.matches(passwordPattern, password);
	}
	
	
	public Login logincheck(Login login) throws Exception {
	    // DB에서 30분 이내의 유효한 토큰 정보 조회
	    Login Login = loginDao.logincheck(login);
	    
	    // 유효한 토큰 정보가 없으면, 토큰이 만료된 것으로 간주
	    if (Login == null) {
	        // 만료 토큰 삭제 (추가로 처리할 필요가 있다면)
	        loginDao.deleteToken(login.getUserId(), login.getToken());
	        throw new Exception("토큰이 만료되었습니다.");
	    }
	    
	    // 유효한 토큰이 존재하면, 새로운 토큰 생성 후 갱신
	    String userId = login.getUserId();
	    String salt = UUID.randomUUID().toString();
	    byte[] originalHash = OpenCrypt.getSHA256(userId, salt);
	    String newToken = OpenCrypt.byteArrayToHex(originalHash);
	    
	    // 새 토큰 정보 생성: logintime은 insertToken 구문에서 CURRENT_TIMESTAMP로 갱신됨
	    Login newLogin = new Login(userId, newToken);
	    loginDao.insertToken(newLogin);
	    
	    return newLogin;
	}


}
