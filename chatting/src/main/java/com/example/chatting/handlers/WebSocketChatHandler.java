package com.example.chatting.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component  //빈으로 등록하는것
public class WebSocketChatHandler extends TextWebSocketHandler { //인터페이스를 임플리먼트 하면 필요없는것을 구현시켜야 한다 근데 TextWebSocketHandler 클래스를 상속하면 필요한것만 가져다 쓸수 있다.

    final Map<String,WebSocketSession> webSocketSessionMap = new ConcurrentHashMap<>();

//핵심 WebSocket 로직.
    @Override //클라이언트 연결 시 실행. 클라이언트 세션을 Map에 저장.
    public void afterConnectionEstablished(WebSocketSession session) throws Exception { //웹소켓 클라이언트가 서버로 연결한 이후에 실행하는 코드들
        log.info("{} connected",session.getId()); // INFO 30588 --- [chatting] [nio-8080-exec-8] c.e.c.handlers.WebSocketChatHandler      : 2a6bdccb-f833-4e41-e692-62d40d92f1eb connected
        this.webSocketSessionMap.put(session.getId(),session); //어떠한 웹소켓클라이언트가 서버로 접근 했을대 해당클라이언트를 저장 함 키session.getId() 는 해당클라이언트에 아이디 로 하고  벨류는 session자체를 넣어줌
    }

    @Override //메시지 수신 시 전체 클라이언트에게 브로드캐스트.
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception { //웹소켓 클라이언트에서 메시지가 왔을때 로직처리
        // handleMessage() 메서드는 내부적으로 자동으로 호출되는 메서드   Spring의 WebSocketHandler 인터페이스에서 정의된 메서드이고, WebSocket 통신 중 메시지가 들어올 때 자동으로 동작
        log.info("{} sent {}",session.getId(),message.getPayload());
        //이메시지를 통해 A라는 클라이언트에서 온 메시지를 다른 클라이언트에게 전달할수 있게 해주는 로직
        this.webSocketSessionMap.values().forEach( // [chatting] [nio-8080-exec-8] c.e.c.handlers.WebSocketChatHandler      : 1e0ef269-7861-70bf-37df-082f97f697c9 sent 안녕?
                webSocketSession -> {
                    try {
                        webSocketSession.sendMessage(message);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Override//	연결 종료 시 세션을 Map에서 제거.
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception { //서버에 접속되있던 웹소켓클라이언트카 연결을 끊었을때 로직처리
        log.info("{} disconnected",session.getId()); // INFO 30588 --- [chatting] [nio-8080-exec-9] c.e.c.handlers.WebSocketChatHandler      : 2a6bdccb-f833-4e41-e692-62d40d92f1eb disconnected
        this.webSocketSessionMap.remove(session.getId()); //클라이언트 지우기
    }
}
