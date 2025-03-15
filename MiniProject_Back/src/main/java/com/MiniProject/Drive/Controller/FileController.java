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
import com.MiniProject.Drive.secu.OpenCrypt;
import com.MiniProject.Drive.secu.Security;
import com.MiniProject.Drive.service.FileService;

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
	
    private static final String UPLOAD_DIR = "upload/";

    // 파일 업로드 (암호화 후 저장)
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("userId") String userId) {
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
            
            String fileName = file.getOriginalFilename();
            byte[] security_key = OpenCrypt.getSHA256(fileName, UUID.randomUUID().toString());
            
            Security security = new Security();
            
            String encrypteName = security.encryptFileName(fileName, security_key);
            
            Path filePath = Paths.get(UPLOAD_DIR + encrypteName);
            
            byte[] security_key2 = OpenCrypt.getSHA256(fileName, UUID.randomUUID().toString());
            
            // 파일 암호화 후 저장
            byte[] encryptedData = security.encrypt(file.getBytes(), security_key2);
            File f = new File();
            f.setUserId(userId);
            f.setSecurity(security_key);
            f.setSecurity2(security_key2);
            f.setSecurityName(encrypteName);
            fileService.uploadFile(f);
            
            System.out.println(f.getSecurity().toString());
            
            Files.write(filePath, encryptedData);

            return ResponseEntity.ok("파일 업로드 성공 (암호화됨): " + file.getOriginalFilename());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 실패! + " + e);
        }
    }

    // 파일 다운로드 (복호화 후 전송)
    @PostMapping("/download")
    public ResponseEntity<Map<String, Object>> downloadFile(@RequestBody Map<String, String> map) {
        try {
        	
        	File f = fileService.downloadFile(map);
        	
        	if (f == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // 파일을 찾을 수 없으면 404
            }
        	
        	
            Path filePath = Paths.get(UPLOAD_DIR + f.getSecurityName());
            byte[] encryptedData = Files.readAllBytes(filePath);
            
            Security security = new Security();

            // 파일 복호화
            byte[] decryptedData = security.decrypt(encryptedData, f.getSecurity2());
            String originalFileName = security.decryptFileName(f.getSecurityName(), f.getSecurity());

            Map<String, Object> response = new HashMap<>();
            response.put("fileName", originalFileName);  // 파일명
            response.put("file", decryptedData);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
        	System.out.println(e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
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
