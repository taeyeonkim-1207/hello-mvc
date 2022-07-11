package com.kh.mvc.notification.model.service;

import java.util.HashMap;
import java.util.Map;

import com.kh.mvc.board.model.dto.Board;
import com.kh.mvc.ws.endpoint.HelloWebSocket;
import com.kh.mvc.ws.endpoint.MessageType;

public class NotificationService {

	public int notifyNewComment(Board board) {
		System.out.println("> notifyNewComment");
		// 1. db 알림테이블
		
		// 2. 사용자 실시간 알림
		if(HelloWebSocket.isConnected(board.getWriter())) {
			// 메세지 생성
			Map<String, Object> data = new HashMap<>();
			data.put("receiver", board.getWriter());
			data.put("msg", "[" + board.getTitle() + "] 게시글에 새 댓글이 달렸습니다.");
			
			// 메세지 전송
			HelloWebSocket.sendMessage(MessageType.NOTIFY_NEW_COMMENT, data);			
		}
		
		return 0;
	}

}
