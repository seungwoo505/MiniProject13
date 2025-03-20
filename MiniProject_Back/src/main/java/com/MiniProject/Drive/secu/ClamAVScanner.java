package com.MiniProject.Drive.secu;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import fi.solita.clamav.ClamAVClient;

public class ClamAVScanner {

	/**
	 * ClamAV : 오픈 소스 안티 바이러스 프로그램
	 * 
	 * ClamAV 라는 프로그램 필수 설치
	 * 
	 * 동작을 시켜 3310 포트로 잘 동작하는 것까지 확인
	 * 
	 */
    private static final String CLAMAV_HOST = "localhost";
    private static final int CLAMAV_PORT = 3310;

    /**
     * 업로드된 파일을 ClamAV로 스캔하여, 악성 코드가 있는지 검사
     */
    public static boolean scanFile(MultipartFile file) {
        try {
            ClamAVClient client = new ClamAVClient(CLAMAV_HOST, CLAMAV_PORT);
            byte[] fileBytes = file.getBytes();
            byte[] scanResult = client.scan(fileBytes);
            // scanResult에 "OK"가 포함되어 있으면 안전한 것으로 판단
            String result = new String(scanResult);
            return result.contains("OK");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}