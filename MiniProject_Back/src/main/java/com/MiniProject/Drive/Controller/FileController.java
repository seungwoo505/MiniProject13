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
            
            String encrypteName = encryptFileName(fileName, security_key);
            
            Path filePath = Paths.get(UPLOAD_DIR + encrypteName);
            
            
            // 파일 암호화 후 저장
            byte[] encryptedData = encrypt(file.getBytes(), security_key);
            File f = new File();
            f.setUserId(userId);
            f.setSecurity(security_key);
            f.setSecurityName(encrypteName);
            f.setFileName(fileName);
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

            // 파일 복호화
            byte[] decryptedData = decrypt(encryptedData, f.getSecurity());
            String originalFileName = decryptFileName(f.getSecurityName(), f.getSecurity());

            Map<String, Object> response = new HashMap<>();
            response.put("fileName", originalFileName);  // 파일명
            response.put("file", decryptedData);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
        	System.out.println(e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    
    // AES 암호화
    private static byte[] encrypt(byte[] data, byte[] secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKey key = new SecretKeySpec(secretKey, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    // AES 복호화
    private static byte[] decrypt(byte[] data, byte[] secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKey key = new SecretKeySpec(secretKey, "AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(data);
    }
    
    public static String encryptFileName(String fileName, byte[] secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKey key = new SecretKeySpec(secretKey, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encryptedBytes = cipher.doFinal(fileName.getBytes());
        return Base64.getUrlEncoder().encodeToString(encryptedBytes); // URL-safe Base64 인코딩
    }

    // 파일 이름 복호화
    public static String decryptFileName(String encryptedFileName, byte[] secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec key = new SecretKeySpec(secretKey, "AES");
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] decryptedBytes = cipher.doFinal(Base64.getUrlDecoder().decode(encryptedFileName));
        return new String(decryptedBytes);
    }
}
