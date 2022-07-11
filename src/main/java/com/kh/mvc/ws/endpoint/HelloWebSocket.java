package com.kh.mvc.ws.endpoint;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.RemoteEndpoint.Basic;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.google.gson.Gson;
import com.kh.mvc.ws.config.HelloWebSocketConfigurator;

@ServerEndpoint(value = "/helloWebSocket", configurator = HelloWebSocketConfigurator.class)
public class HelloWebSocket {

	/**
	 * memberId:String = WebSocketSession
	 */
	private static Map<String, Session> clientMap = Collections.synchronizedMap(new HashMap<>());
	
	/**
	 * chatoomId:String = Set<memberId:String> 
	 */
	private static Map<String, Set<String>> chatroomMap = Collections.synchronizedMap(new HashMap<>()); 
	
	
	@OnOpen
	public void onOpen(EndpointConfig config, Session session) {
		Map<String, Object> userProperties = config.getUserProperties();
		String memberId = (String) userProperties.get("memberId");
		System.out.println("[@OnOpen] memberId = " + memberId);
		
		
		// @OnClose에서 사용하기위해 Session#UserProperties에 저장
		Map<String, Object> sessionUserProperties = session.getUserProperties();
		sessionUserProperties.put("memberId", memberId);
		
		addToClientMap(memberId, session);
		
		String chatroomId = (String) userProperties.get("chatroomId"); 
		if(chatroomId != null) {
			sessionUserProperties.put("chatroomId", chatroomId);			
			addToChatroomMap(chatroomId, memberId);
			
			// 입장메세지 전송
			Map<String, Object> data = new HashMap<>();
			data.put("sender", memberId);
			data.put("chatroomId", chatroomId);
			onMessage(msgToJson(MessageType.CHAT_ENTER, data), session);
		}
		
		log();
		
	}
	





	@OnMessage
	public void onMessage(String msg, Session session) {
		Map<String, Object> msgMap = new Gson().fromJson(msg, Map.class);
		MessageType messageType = MessageType.valueOf((String) msgMap.get("messageType"));
		System.out.println("[@OnMessage] msg = " + msg );

		switch(messageType) {
		case CHAT_ENTER:
		case CHAT_MSG:
		case CHAT_LEAVE:
			// 해당 채팅방 참여자에게만 전송
			Map<String, Object> data = (Map<String, Object>) msgMap.get("data");
			String chatroomId = (String) data.get("chatroomId");
			Set<String> participantSet = chatroomMap.get(chatroomId);
			System.out.println("participantSet = " + participantSet);
			if(participantSet != null) {
				for(String memberId : participantSet) {
					Session sess = clientMap.get(memberId);
					try {
						sess.getBasicRemote().sendText(msg);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
			break;
		}
		
//		Collection<Session> sessions = clientMap.values();
//		for(Session each : sessions) {
//			Basic basic = each.getBasicRemote();
//			try {
//				basic.sendText(msg);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
	}
	
	@OnError
	public void onError(Throwable e) {
		e.printStackTrace();
	}
	
	@OnClose
	public void onClose(Session session) {
		Map<String, Object> sessionUserProperties = session.getUserProperties();
		String memberId = (String) sessionUserProperties.get("memberId");

		removeFromClientMap(memberId);

		String chatroomId = (String) sessionUserProperties.get("chatroomId");
		if(chatroomId != null) {
			removeFromChatroomMap(chatroomId, memberId);
			// 퇴장메세지 전송
			Map<String, Object> data = new HashMap<>();
			data.put("sender", memberId);
			data.put("chatroomId", chatroomId);
			onMessage(msgToJson(MessageType.CHAT_LEAVE, data), session);
		}	
		
		log();
	}
	
	private void addToChatroomMap(String chatroomId, String memberId) {
		Set<String> participantSet = chatroomMap.get(chatroomId); 
		
		if(participantSet == null) {
			// 새로 생성
			participantSet = Collections.synchronizedSet(new HashSet<>());
			chatroomMap.put(chatroomId, participantSet);
		}
		participantSet.add(memberId);
		System.out.println("participantSet = " + participantSet);
	}
	

	private void removeFromChatroomMap(String chatroomId, String memberId) {
		Set<String> participantSet = chatroomMap.get(chatroomId); 
		participantSet.remove(memberId);
		// 마지막 참여자 퇴장시, 채팅방 제거
		if(participantSet.size() == 0) {
			chatroomMap.remove(chatroomId);
		}
	}


	
	private static void addToClientMap(String memberId, Session session) {
		clientMap.put(memberId, session);
	}

	private static void removeFromClientMap(String memberId) {
		clientMap.remove(memberId);
	}
	
	private static void log() {
		System.out.println("[HelloWebSocket Log] 현재 접속자수 : " + clientMap.size() + " " + clientMap.keySet());
		System.out.println("[HelloWebSocket 채팅방 현황] 채팅방수 : " + chatroomMap.size() + " " + chatroomMap);
	}

	public static boolean isConnected(String receiver) {
		return clientMap.containsKey(receiver);
	}

	public static void sendMessage(MessageType messageType, Map<String, Object> data) {
		String receiver = (String) data.get("receiver");
		Session session = clientMap.get(receiver); 
		if(session != null) {
			Basic basic = session.getBasicRemote();
			try {
				basic.sendText(msgToJson(messageType, data));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static String msgToJson(MessageType messageType, Map<String, Object> data) {
		Map<String, Object> map = new HashMap<>();
		map.put("messageType", messageType);
		map.put("data", data);
		map.put("time", System.currentTimeMillis());
		return new Gson().toJson(map);
	}
}





