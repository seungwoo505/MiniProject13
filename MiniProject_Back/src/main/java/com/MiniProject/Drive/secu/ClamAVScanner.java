package com.MiniProject.Drive.secu;

import org.springframework.web.multipart.MultipartFile;

import fi.solita.clamav.ClamAVClient;

import java.io.IOException;

public class ClamAVScanner {

    private static final String CLAMAV_HOST = "localhost"; // ClamAV 데몬 호스트
    private static final int CLAMAV_PORT = 3310;           // 기본 ClamAV 포트

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
            System.out.println("ClamAV scan result: " + result);
            return result.contains("OK");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
