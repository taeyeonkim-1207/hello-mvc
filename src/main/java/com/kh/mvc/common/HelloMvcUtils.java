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

	/**쥰비물
	 * @param cPage
	 * @param numPerPage
	 * @param totalContent
	 * @param url
	 * @return
	 * 
	 * totalPage 전체페이지수 
	 * pagebarSize 한페이지에 표시할 페이지 번호개수 
	 * pagebarStart ~ pagebarEnd
	 * pageNo 증감변수 
	 * 
	 * 1. 이전영역
	 * 2. pageNo 영역
	 * 3. 다음영역
	 * 으로 나눠서 html 제작
	 * */
	public static String getPagebar(int cPage, int numPerPage, int totalContent, String url) {
		
		//계속 append할거니까 
		StringBuilder pagebar = new StringBuilder();
		
		url += (url.indexOf("?") < 0) ? "?cPage=" : "&cPage=";
		
		//이전다음, 실제페이지보다 오바되는거 방지
		int totalPage = (int) Math.ceil((double)totalContent / numPerPage);
		int pagebarSize = 5;
		int pagebarStart = ((cPage - 1) / pagebarSize * pagebarSize) + 1;
		int pagebarEnd = pagebarStart + pagebarSize - 1;
		int pageNo = pagebarStart;
		
		//이전영역
		if(pageNo == 1) {
			
		}else {
			pagebar.append("<a href='" + url + (pageNo - 1) + "'>이전</a>\n");
		}

		//pageNo영역
		while(pageNo <= pagebarEnd && pageNo <= totalPage) { //(1부터)5,12
			// 현재페이지
			if(pageNo == cPage) {
				pagebar.append("<span class='cPage'>" + pageNo + "</span>\n");
			}
			// 현재페이지가 아닌 경우
			else {
				pagebar.append("<a href='" + url + pageNo + "'>" + pageNo + "</a>\n");
			}
			pageNo++;
		}
		//다음영역
		if(pageNo > totalPage) {
			
		}else {
			pagebar.append("<a href='" + url + pageNo + "'>다음</a>\n");
		}
		return pagebar.toString();
	}

//	페이지소스보기 하면 br추가되어있음
	public static String convertLineFeedToBr(String str) {
		return str.replaceAll("\\n", "<br/>");
	}

	public static String escapeXml(String str) {
		return str .replaceAll("&", "&amp;")
				   .replaceAll("<", "&lt;")
				   .replaceAll(">", "&gt;");

	}

}
