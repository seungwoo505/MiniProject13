package com.MiniProject.Drive.Controller;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.MiniProject.Drive.dto.Comment;
import com.MiniProject.Drive.dto.Login;
import com.MiniProject.Drive.dto.MyFile;
import com.MiniProject.Drive.secu.ClamAVScanner;
import com.MiniProject.Drive.secu.OpenCrypt;
import com.MiniProject.Drive.secu.Security;
import com.MiniProject.Drive.service.FileService;
import com.MiniProject.Drive.service.MemberService;

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
            MyFile f = new MyFile();
            f.setUserId(userId);
            f.setSecurity(security_key);
            f.setSecurity2(security_key2);
            f.setSecurityName(encrypteName);
            f.setSecurityDetail(securityDetail);
            fileService.uploadFile(f);

            Files.write(filePath, encryptedData);

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
    public ResponseEntity<Map<String, Object>> downloadFile(@RequestBody Map<String, String> map,@RequestHeader("Authorization") String token) {
    	Map<String, Object> response = new HashMap<>();

    	try {
    		String userId = map.get("userId");
            Login login = new Login(userId, token);
            Login validLogin = memberService.logincheck(login);

        	MyFile f = fileService.downloadFile(map);

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
            response.put("token", validLogin.getToken());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
        	response.put("msg", "파일이 손상되었습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PostMapping("updateFile")
    public ResponseEntity<?> updateFile(@RequestParam("file") MultipartFile file, @RequestParam("userId") String userId, @RequestParam("fileId") String fileId){

    	try {

    		Map<String, String> map = new HashMap<>();
    		map.put("userId", userId);
    		map.put("fileId", fileId);

    		MyFile mf = fileService.downloadFile(map);

    		if(mf == null) {
    			return ResponseEntity.badRequest().body("파일이 손실되었습니다.");
    		}
    		if(!ClamAVScanner.scanFile(file)) {
        		return ResponseEntity.badRequest().body("파일에 악성코드가 포함되어있습니다.");
        	}

    		String fileName = file.getOriginalFilename();
            byte[] security_key = OpenCrypt.getSHA256(fileName, UUID.randomUUID().toString());

            Security security = new Security();

            String encrypteName = security.encryptFileName(fileName, security_key);

            byte[] security_key2 = OpenCrypt.getSHA256(fileName, UUID.randomUUID().toString());

            // 파일 암호화 후 저장
            byte[] encryptedData = security.encrypt(file.getBytes(), security_key2);
            String securityDetail = OpenCrypt.computeSHA256(file.getBytes());

            File oldFile = new File(UPLOAD_DIR + mf.getSecurityName());

            System.out.println(mf.getSecurityName());

            if(oldFile.exists()) {
            	try(FileOutputStream fos = new FileOutputStream(oldFile)){
            		fos.write(encryptedData);
            	}
            }else {
            	return ResponseEntity.badRequest().body("파일이 손실되었습니다.");
            }

            Path source = Paths.get(UPLOAD_DIR + mf.getSecurityName());
            Path target = Paths.get(UPLOAD_DIR + encrypteName);
            Files.move(source, target);

            mf.setSecurity(security_key);
            mf.setSecurity2(security_key2);
            mf.setSecurityName(encrypteName);
            mf.setSecurityDetail(securityDetail);
            fileService.updateFile(mf);

            return ResponseEntity.ok("파일 업데이트 완료");
    	}catch(Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("업데이트에 실패했습니다.");
    	}
    }

    @PostMapping("/deleteFile")
    public ResponseEntity<String> deleteFile(@RequestBody Map<String, String> map){
    	try {
    		MyFile f = fileService.findFile(map);

    		System.out.println(f.toString());

			fileService.deleteFile(map);

			File file = new File(UPLOAD_DIR + f.getSecurityName());

			if(file.exists()) {
				if(!file.delete()) {
					return ResponseEntity.status(404).body("파일 삭제 실패");
				}
			}


			return ResponseEntity.ok("성공적으로 삭제되었습니다.");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ResponseEntity.status(404).body("파일을 찾을 수 없습니다.");
		}
    }

    @PostMapping("/getFiles")
    public ResponseEntity<Map<String, Object>[]> getFiles(@RequestBody Map<String, String> map, @RequestHeader("Authorization") String token) {
    	MyFile[] files;

        String userId = map.get("userId");
        Login login = new Login(userId, token);
        Login validLogin = null;
        try {
            validLogin = memberService.logincheck(login);
            if (validLogin == null) {
                Map<String, Object>[] emptyResponse = new Map[1];
                emptyResponse[0] = new HashMap<>();
                emptyResponse[0].put("token", null);
                return ResponseEntity.ok(emptyResponse);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object>[] emptyResponse = new Map[1];
            emptyResponse[0] = new HashMap<>();
            emptyResponse[0].put("token", null);
            return ResponseEntity.ok(emptyResponse);
        }


		try {
			files = fileService.selectFile(map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}


	    if (files.length == 0) {
	        Map<String, Object>[] emptyResponse = new Map[1];
	        emptyResponse[0] = new HashMap<>();
	        emptyResponse[0].put("token", validLogin.getToken());
	        return ResponseEntity.ok(emptyResponse);
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

        if (validLogin != null) {
            response[0].put("token", validLogin.getToken());
        }

    	return ResponseEntity.ok(response);
    }

    @PostMapping("/preview")
    public ResponseEntity<Map<String, Object>> previewFile(@RequestBody Map<String, String> map){

    		Map<String, Object> response = new HashMap<>();
    	try {
    		MyFile file = fileService.downloadFile(map);

        	if (file == null) {
        		response.put("msg", "파일을 찾을 수 없습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // 파일을 찾을 수 없으면 404
            }

            Path filePath = Paths.get(UPLOAD_DIR + file.getSecurityName());
            byte[] encryptedData = Files.readAllBytes(filePath);

            Security security = new Security();

            // 파일 복호화
            byte[] decryptedData = security.decrypt(encryptedData, file.getSecurity2());
            String originalFileName = security.decryptFileName(file.getSecurityName(), file.getSecurity());

            Preview preview = new Preview();

            if(map.get("type").equals("text")) {
            	String text = preview.textPreview(decryptedData);

            	response.put("fileName", originalFileName);
                response.put("file", text);
            }else if(map.get("type").equals("img")){
                byte[] thumbnail = preview.imagePreview(decryptedData, 200);

                response.put("fileName", originalFileName);
                response.put("file", thumbnail);
            }else {
            	response.put("msg", "지원하지않는 형식입니다.");
            }

            return ResponseEntity.ok(response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.put("msg", "오류가 발생했습니다.");
			return ResponseEntity.status(404).body(response);
		}
    }

    @PostMapping("/insertComment")
    public ResponseEntity<String> insertComment(@RequestBody Comment comment){
    	try {
			fileService.insertComment(comment);
			return ResponseEntity.ok("정상적으로 등록되었습니다.");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ResponseEntity.status(408).body("오류가 발생하여 등록에 실패했습니다.n잠시후 다시 시도해주세요.");
		}
    }

    @PostMapping("/deleteComment")
    public ResponseEntity<String> deleteComment(@RequestBody Comment comment){
    	try {
			fileService.deleteComment(comment);
			return ResponseEntity.ok("정상적으로 삭제되었습니다.");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ResponseEntity.status(408).body("오류가 발생하여 삭제를 실패했습니다.n잠시후 다시 시도해주세요.");
		}
    }

    @PostMapping("/selectComment")
    public ResponseEntity<Comment[]> selectComment(@RequestBody Comment comment){
    	try {
			Comment[] c = fileService.selectComment(comment);

			return ResponseEntity.ok(c);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ResponseEntity.status(404).body(null);
		}
    }
}
