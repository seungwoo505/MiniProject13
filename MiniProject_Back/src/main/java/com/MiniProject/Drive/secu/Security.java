package com.MiniProject.Drive.secu;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Security {
	/**
	 * AES 암호화 처리
	 *
	 * 파일 암호화
	 *
	 * 암호화 대상, 암호화 키
	 */
    public byte[] encrypt(byte[] data, byte[] secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKey key = new SecretKeySpec(secretKey, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    /**
     * AES복호화 처리
     *
     * 파일 복호화
     *
     * 복호화 대상, 복호화할 키
     */
    public byte[] decrypt(byte[] data, byte[] secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKey key = new SecretKeySpec(secretKey, "AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    /**
     * AES 암호화 처리
     *
     * 파일 이름 암호화
     *
     * 파일 이름, 암호화 키
     */

    public String encryptFileName(String fileName, byte[] secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKey key = new SecretKeySpec(secretKey, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encryptedBytes = cipher.doFinal(fileName.getBytes());
        return Base64.getUrlEncoder().encodeToString(encryptedBytes); // URL-safe Base64 인코딩
    }

    /**
     * AES복호화 처리
     *
     * 파일 이름 복호화
     *
     * 복호화 대상, 복호화할 키
     */
    public String decryptFileName(String encryptedFileName, byte[] secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec key = new SecretKeySpec(secretKey, "AES");
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] decryptedBytes = cipher.doFinal(Base64.getUrlDecoder().decode(encryptedFileName));
        return new String(decryptedBytes);
    }
}
