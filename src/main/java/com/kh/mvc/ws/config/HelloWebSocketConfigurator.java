package com.kh.mvc.ws.config;

import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import javax.websocket.server.ServerEndpointConfig.Configurator;

import com.kh.mvc.member.model.dto.Member;

public class HelloWebSocketConfigurator extends Configurator{
	/**
	 * WebSocket Session을 관리하기 위해 
	 * 로그인한 사용자의 memberId를 UserProperties객체에 미리 담아두고 @OnOpen에서 꺼내 사용한다.
	 */
	@Override
	public void modifyHandshake(
			ServerEndpointConfig sec, 
			HandshakeRequest request, 
			HandshakeResponse response) {
		HttpSession httpSession = (HttpSession) request.getHttpSession();
		Member loginMember = (Member) httpSession.getAttribute("loginMember");
		String memberId = loginMember.getMemberId();
		
		Map<String, Object> userProperties = sec.getUserProperties();
		userProperties.put("memberId", memberId);
		
		System.out.println("[HelloWebSocketConfigurator#modifyHandshake] memberId = " + memberId);
	
		String chatroomId = (String) httpSession.getAttribute("chatroomId");
		if(chatroomId != null) {
			httpSession.removeAttribute("chatroomId"); //1회용으로 매번 새로 발급해서 사용해야함
			userProperties.put("chatroomId", chatroomId);
			System.out.println("[HelloWebSocketConfigurator#modifyHandshake] chatroomId = " + chatroomId);
		}
	}
}
