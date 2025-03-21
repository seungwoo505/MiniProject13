package com.MiniProject.Drive.Controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.MiniProject.Drive.dto.FileShare;
import com.MiniProject.Drive.dto.Login;
import com.MiniProject.Drive.dto.MyFile;
import com.MiniProject.Drive.secu.Security;
import com.MiniProject.Drive.service.FileService;
import com.MiniProject.Drive.service.FileShareService;
import com.MiniProject.Drive.service.MemberService;


@RestController
@RequestMapping("/share")
@CrossOrigin("http://127.0.0.1:5500")
public class FileShareController {

	@Autowired
	private FileShareService fileShareService;

	@Autowired
	FileService fileService;

	@Autowired
	MemberService memberService;

	@PostMapping("/create")
	public ResponseEntity<Map<String, String>> createShareURL(@RequestBody FileShare fs, @RequestHeader("Authorization") String token){
		Map<String, String> response = new HashMap<>();
		try {
			String userId = fs.getUserId();
			Login login = new Login(userId, token);
            Login validLogin = memberService.logincheck(login);
			
			String shareUrl = fileShareService.createShareURL(fs);

			if(fs.isShareUser()) {
				fileShareService.createShareUser(fs);
			}
			
	        response.put("shareUrl", shareUrl);
	        response.put("token", validLogin.getToken());
	        response.put("message", "공유 URL 생성에 성공하였습니다.");
	        
	        return ResponseEntity.ok(response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	        response.put("message", "공유를 생성하지 못했습니다.");
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
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

	@PostMapping("/shareFile")
	public ResponseEntity<List<String>> selectShareFile(@RequestBody FileShare RequestFs){
		try {
			FileShare[] fs = fileShareService.selectShareFile(RequestFs);
			List<String> list = new ArrayList<>();

			for(FileShare f : fs) {
				list.add(f.getShareId());
			}
			return ResponseEntity.ok(list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			List<String> list = new ArrayList<>();
			list.add("정보를 불러오는 중 오류가 발생했습니다.");
			return ResponseEntity.status(404).body(list);
		}


	}

	@PostMapping("/download")
	public ResponseEntity<Map<String, Object>> downloadShareURL(@RequestBody Map<String, String> map, @RequestHeader(value = "Authorization", required = false)String token){
		try {
			FileShare fs = new FileShare();
			if(map.get("token") != null) {
				fs = fileShareService.selectShareURL(map.get("token"));
				
				if(fs == null) {
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
				}
				
				map.put("fileId", fs.getFileId());
				map.put("userId", fs.getUserId());
			}
			
			boolean shareUser = map.get("shareUser").toString().equals("true");

			if(fs.isShareUser() || shareUser) {
				fs = new FileShare();
				fs.setShareId(map.get("shareId"));
				fs.setShareUser(true);
				fs.setFileId(map.get("fileId"));
				fs.setUserId(map.get("userId"));
				FileShare fs2 = fileShareService.selectShareUser(fs);

				if(fs2 == null) {
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
				}
			}

			MyFile f = fileService.downloadFile(map);

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
            
            if (fs.isShareUser() || shareUser) {
                if (token == null || token.trim().isEmpty()) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
                }
                
                Login login = new Login(fs.getShareId(), token);
                Login validLogin = memberService.logincheck(login);
                response.put("token", validLogin.getToken());
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
	}

    @PostMapping("/getShareFiles")
    public ResponseEntity<Map<String, Object>[]> getShareFiles(@RequestBody Map<String, String> map, @RequestHeader("Authorization") String token) {
        String userId = map.get("userId");

        Login login = new Login(userId, token);
        Login validLogin = null;
        try {
            validLogin = memberService.logincheck(login);
            if (validLogin == null) {
                Map<String, Object>[] emptyResponse = new HashMap[1];
                emptyResponse[0] = new HashMap<>();
                emptyResponse[0].put("token", null);
                return ResponseEntity.status(404).body(emptyResponse);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object>[] emptyResponse = new HashMap[1];
            emptyResponse[0] = new HashMap<>();
            emptyResponse[0].put("token", null);
            return ResponseEntity.status(404).body(emptyResponse);
        }


        try {
            List<String> fileIds = fileShareService.getFileIdsByUserId(userId);

            if (fileIds.isEmpty()) {
                Map<String, Object>[] emptyResponse = new HashMap[1];
                emptyResponse[0] = new HashMap<>();
                emptyResponse[0].put("token", validLogin.getToken());
                return ResponseEntity.status(203).body(emptyResponse);
            }

            List<MyFile> files = fileService.getFilesByIds(fileIds);

            Map<String, Object>[] response = new HashMap[files.size()];


            Security security = new Security();

            for (int i = 0; i < files.size(); i++) {
                MyFile f = files.get(i);
                response[i] = new HashMap<>();
                try {
                    String originalFileName = security.decryptFileName(f.getSecurityName(), f.getSecurity());
                    response[i].put("fileId", f.getFileId());
                    response[i].put("userId", f.getUserId());
                    response[i].put("fileName", originalFileName);
                    response[i].put("uploadDate", f.getUploadDate());
                    response[i].put("updateDate", f.getUpdateDate());
                } catch (Exception e) {
                    e.printStackTrace();
                    response[i].put("err", "파일이 손상되어 읽을 수 없습니다.");
                }
            }

            if (validLogin != null) {
                response[0].put("token", validLogin.getToken());
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
