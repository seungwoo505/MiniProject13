package com.MiniProject.Drive.Controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.MiniProject.Drive.service.MemberService;
import com.MiniProject.Drive.dto.*;

@RestController
@CrossOrigin("http://127.0.0.1:5500/")
public class MemberController {

	@Autowired
	MemberService memberService;
	
	@PostMapping("signup")
	public String signup(@RequestBody Member m) throws Exception {
		try {
			memberService.signup(m);
			return m.getUserId()+"님 가입을 환영합니다";
		} catch (DuplicateKeyException e) {
			return "이미 존재하는 ID입니다."; 
		}
		catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}
	
	@PostMapping("tokenlogin")
	public Map<String,String> tokenLogin(@RequestBody Member m) {
		Map<String,String> responseMap=new HashMap<>();
		
		try {
			Login loginInfo=memberService.tokenLogin(m);
			
			if(loginInfo!=null && loginInfo.getUserId()!=null && loginInfo.getToken()!=null) {
				responseMap.put("userId", loginInfo.getUserId());
				responseMap.put("Authorization", loginInfo.getToken());
			}else {
				responseMap.put("msg", "다시 로그인 해주세요");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMap.put("msg", "다시 로그인 해주세요");
		}
		return responseMap;
	}
	
	
	@PostMapping("logout") 
	public String logout(@RequestHeader String authorization, @RequestBody Map<String, String> requestBody ) {
		String userId = requestBody.get("userId");
	    try {
	        memberService.logout(userId, authorization);
	        return "로그아웃 성공";
	    } catch (Exception e) {
	        e.printStackTrace();
	        return "로그아웃 오류";
	    }
	}
	
	
}