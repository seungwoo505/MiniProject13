package com.MiniProject.Drive.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.MiniProject.Drive.dto.File;
import com.MiniProject.Drive.dto.Login;
import com.MiniProject.Drive.secu.ClamAVScanner;
import com.MiniProject.Drive.secu.OpenCrypt;
import com.MiniProject.Drive.secu.Security;
import com.MiniProject.Drive.service.FileService;
import com.MiniProject.Drive.service.MemberService;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.file.*;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@CrossOrigin("http://127.0.0.1:5500")
public class FileController {
	
	@Autowired
	FileService fileService;
	
	@Autowired
	MemberService memberService;
	
    private static final String UPLOAD_DIR = "upload/";

    // 파일 업로드 (암호화 후 저장)
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("userId") String userId, @RequestHeader("Authorization") String token) {
        try {
            Login login = new Login(userId, token);
            Login validLogin = memberService.logincheck(login);
            
        	if(!ClamAVScanner.scanFile(file)) {
        		return ResponseEntity.badRequest().body("파일에 악성코드가 포함되어있습니다.");
        	}
        	
            Files.createDirectories(Paths.get(UPLOAD_DIR));
            
            String fileName = file.getOriginalFilename();
            byte[] security_key = OpenCrypt.getSHA256(fileName, UUID.randomUUID().toString());
            
            Security security = new Security();
            
            String encrypteName = security.encryptFileName(fileName, security_key);
            
            Path filePath = Paths.get(UPLOAD_DIR + encrypteName);
            
            byte[] security_key2 = OpenCrypt.getSHA256(fileName, UUID.randomUUID().toString());
            
            // 파일 암호화 후 저장
            byte[] encryptedData = security.encrypt(file.getBytes(), security_key2);
            String securityDetail = OpenCrypt.computeSHA256(file.getBytes());
            File f = new File();
            f.setUserId(userId);
            f.setSecurity(security_key);
            f.setSecurity2(security_key2);
            f.setSecurityName(encrypteName);
            f.setSecurityDetail(securityDetail);
            fileService.uploadFile(f);
            
            Map<String, String> responseData = new HashMap<>();
            responseData.put("message", "파일 업로드 성공 (암호화됨): " + file.getOriginalFilename());
            responseData.put("token", validLogin.getToken());

            return ResponseEntity.ok(responseData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 실패! + " + e);
        }
    }

    // 파일 다운로드 (복호화 후 전송)
    @PostMapping("/download")
    public ResponseEntity<Map<String, Object>> downloadFile(@RequestBody Map<String, String> map) {
    	Map<String, Object> response = new HashMap<>();
    	
    	try {
        	File f = fileService.downloadFile(map);
        	
        	if (f == null) {
        		response.put("msg", "파일을 찾을 수 없습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // 파일을 찾을 수 없으면 404
            }
        	
            Path filePath = Paths.get(UPLOAD_DIR + f.getSecurityName());
            byte[] encryptedData = Files.readAllBytes(filePath);
            
            Security security = new Security();

            // 파일 복호화
            byte[] decryptedData = security.decrypt(encryptedData, f.getSecurity2());
            String originalFileName = security.decryptFileName(f.getSecurityName(), f.getSecurity());
            
            String securityDetail = OpenCrypt.computeSHA256(decryptedData);
            
            if(!securityDetail.equals(f.getSecurityDetail())) {
            	response.put("msg", "파일이 위변조되었습니다.");
            	return ResponseEntity.status(404).body(response);
            }
            
            response.put("fileName", originalFileName);  // 파일명
            response.put("file", decryptedData);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
        	response.put("msg", "파일이 손상되었습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    @PostMapping("/getFiles")
    public ResponseEntity<Map<String, Object>[]> getFiles(@RequestBody Map<String, String> map) {
    	File[] files;
    	
		try {
			files = fileService.selectFile(map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		Map<String, Object>[] response = new Map[files.length];
		
		for(int i = 0; i < response.length; i++) {
			response[i] = new HashMap<>();
		}
		
    	Security security = new Security();
    	
    	for(int i = 0; i < files.length; i++) {
    		try {
				String originalFileName = security.decryptFileName(files[i].getSecurityName(), files[i].getSecurity());
				
				response[i].put("fileId", files[i].getFileId());
				response[i].put("userId", files[i].getUserId());
				response[i].put("fileName", originalFileName);
				response[i].put("uploadDate", files[i].getUploadDate());
				response[i].put("updateDate", files[i].getUpdateDate());
			} catch (Exception e) {
				e.printStackTrace();
				response[i].put("err", "파일이 손상되어 읽을 수 없습니다.");
			}
    	}
    	
    	return ResponseEntity.ok(response);
    }
}
