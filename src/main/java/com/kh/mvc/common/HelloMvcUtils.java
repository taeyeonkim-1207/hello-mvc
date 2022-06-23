package com.kh.mvc.common;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Base64.Encoder;

public class HelloMvcUtils {

	/**
	 * 
	 * 1. 암호화
	 * 2. 인코딩처리
	 * 회원가입,로그인,비밀번호 업데이트시
	 * @param rawPassword
	 * @return
	 */
	public static String getEncryptedPassword(String rawPassword, String salt) {
		String encryptedPassword = null;
		
		try {
			// 1. 암호화
//			암호화처리 주체
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			byte[] input = rawPassword.getBytes("utf-8");
			byte[] saltBytes = salt.getBytes("utf-8");
//			salt가 있어야 같은 비밀번호라도 비문이 달라짐
			md.update(saltBytes); //salt값 전달
			byte[] encryptedBytes = md.digest(input); //암호화
//			System.out.println(new String(encryptedBytes));
			
			// 2. 인코딩처리: 영문자 숫자 + / (=패딩문자)
			Encoder encoder = Base64.getEncoder();
			encryptedPassword = encoder.encodeToString(encryptedBytes);
			
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return encryptedPassword;
	}

}
