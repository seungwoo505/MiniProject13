package com.MiniProject.Drive.Controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.MiniProject.Drive.dto.File;
import com.MiniProject.Drive.dto.FileShare;
import com.MiniProject.Drive.secu.Security;
import com.MiniProject.Drive.service.FileService;
import com.MiniProject.Drive.service.FileShareService;


@RestController
@RequestMapping("/share")
@CrossOrigin("http://127.0.0.1:5500")
public class FileShareController {
	
	@Autowired
	private FileShareService fileShareService;
	
	@Autowired
	FileService fileService;
	
	@PostMapping("/create")
	public ResponseEntity<String> createShareURL(@RequestBody FileShare fs){
		try {
			
			System.out.println(fs.toString());
			String shareUrl = fileShareService.createShareURL(fs);
			
			if(fs.isShareUser()) {
				fileShareService.createShareUser(fs);
			}
			
			return ResponseEntity.ok(shareUrl);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("공유를 생성하지못습니다.");
		}
	}
	
	@GetMapping("/{token}")
	public ResponseEntity<FileShare> selectShareURL(@PathVariable String token){
		
		try {
			FileShare fs = fileShareService.selectShareURL(token);
			
			if(fs == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
			
			if(fs.isShareUser()) {
				FileShare fs2 = new FileShare();
				fs2.setShareUser(fs.isShareUser());
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(fs2);
			}
			
			return ResponseEntity.ok(fs);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}
	
	@PostMapping("/shareUser")
	public ResponseEntity<FileShare> selectShareUser(@RequestBody FileShare RequestFs){
		
		try {
			FileShare fs = fileShareService.selectShareURL(RequestFs.getToken());
			
			if(fs == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
			
			if(fs.isShareUser()) {
				fs.setShareId(RequestFs.getShareId());
				FileShare fs2 = fileShareService.selectShareUser(fs);
				
				if(fs2 == null) {
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
				}
			}
			
			return ResponseEntity.ok(fs);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}
	
	@PostMapping("/download")
	public ResponseEntity<Map<String, Object>> downloadShareURL(@RequestBody Map<String, String> map){
		try {
			FileShare fs = fileShareService.selectShareURL(map.get("token"));
			
			if(fs == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
			
			if(fs.isShareUser()) {
				fs.setShareId(map.get("shareId"));
				FileShare fs2 = fileShareService.selectShareUser(fs);
				
				if(fs2 == null) {
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
				}
			}
			map.put("fileId", fs.getFileId());
			map.put("userId", fs.getUserId());
			
        	File f = fileService.downloadFile(map);
        	
        	if (f == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // 파일을 찾을 수 없으면 404
            }
        	
        	
            Path filePath = Paths.get("upload/" + f.getSecurityName());
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
}
