package com.example.chatting.controller;

import com.example.chatting.dtos.ChatMessage;
import com.example.chatting.dtos.ChatroomDto;
import com.example.chatting.entities.Message;
import com.example.chatting.services.ChatService;
import com.example.chatting.vos.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Controller
public class StompChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate; // 사용자가 온라인 시에 서버에서 스톰프를 통해서 새로운 메시지가 발행되었다라는 알림을 받기 위해서 쓴느것

    @MessageMapping("/chats/{chatroomId}") //MessageMapping 을 통해서 어떤경로로 발행된 메시지를 라우팅할건지 지정해준다 -> pub/chats 라는 곳으로 발행한다면 저렇게 pub는 생략이된다 설정해준것처럼
    @SendTo("/sub/chats/{chatroomId}") // /sub/chats 이라는 경로로 구독한 사람에게 보내주는 역할을 한다 - 채팅방 입장할 때 구독(subscribe) 해야 해.
    public ChatMessage handleMessage(@AuthenticationPrincipal Principal principal, @DestinationVariable Long chatroomId, @Payload Map<String, String> payload){
        // @DestinationVariable
        // 목적: 클라이언트가 보낸 메시지에서 URI 경로에 포함된 변수를 메서드 인자로 받는 데 사용됩니다.
        // payload: 클라이언트가 보낸 메시지 본문입니다. 예를 들어, { "message": "Hello, World!" }와 같은 JSON 형태로 메시지를 보내면, 이 값이 payload로 전달됩니다.
        log.info("{} sent {} in {}", principal.getName(),payload,chatroomId);
        CustomOAuth2User user = (CustomOAuth2User) ((AbstractAuthenticationToken) principal).getPrincipal(); //OAuth2AuthenticationToken은 Principal 인터페이스를 구현하는 클래스이므로, principal을 OAuth2AuthenticationToken으로 타입 캐스팅할 수 있습니다. -> 다운캐스팅   그런데 일반상담사가 로그인할때 는 소셜 인증된 토큰이 아닌 usernamepassword토큰으로 받아야 하는데 요 소셜토큰및 일반사용자 토큰이 상속한 AbstractAuthenticationToken을 써야한다
        Message message = chatService.saveMessage(user.getMember(),chatroomId,payload.get("message")); //멤버정보, 방의고유아이디,메시지내용을 전달해서 디비에 저장
        messagingTemplate.convertAndSend("/sub/chats/updates",chatService.getChatroom(chatroomId)); //messagingTemplate 통해서 새로운 메시지를 발행할것이다 발행을 하는데 발행대상과 ,어떤내용을 발행할건지 정할수 있다 ->  어떤 채팅방의 메시지가 왔을떄 그채팅방의 새로운 메시지가 발행되었다 라고 하는 메시지를 별도로 발행 해주는것이다 알림용 전송 (예: 메시지 알림 뱃지 표시 등) -> 채팅방 정보를 넘겨줌
        return new ChatMessage(principal.getName(),payload.get("message"));   //payload.get("message") 자바스크립트에서 메시지매핑으로 보내는 JSON형식이 메시지 키라 키값을 불러와서 벨류값을 불러옴
    }
}

//다운캐스팅은 부모 클래스 타입의 객체를 자식 클래스 타입으로 변환할 때 필요합니다. 예를 들어, OAuth2User는 인터페이스로, 이 인터페이스를 구현한 구체적인 클래스인 CustomOAuth2User를 사용하려면 다운캐스팅을 해야 합니다.
//소셜 로그인 시 OAuth2AuthenticationToken은 소셜 로그인 인증이 성공하면 **OAuth2 제공자(예: 구글, 네이버 등)**로부터 반환되는 인증 정보를 포함
//전체 흐름:
//사용자가 소셜 로그인 버튼 클릭:
//사용자가 "구글 로그인" 버튼을 클릭하면 애플리케이션은 구글 OAuth2 인증 페이지로 리다이렉트됩니다.
//OAuth2 제공자에서 인증:
//사용자는 구글 로그인 페이지에서 인증을 진행하고, 인증이 성공하면 구글은 액세스 토큰과 함께 사용자의 정보를 반환합니다 (이메일, 이름 등).
//Spring Security에서 인증 처리:
//OAuth2LoginAuthenticationFilter 필터가 실행됩니다. 이 필터는 구글에서 반환된 액세스 토큰을 사용해 인증을 처리합니다.
//반환된 사용자 정보를 기반으로 OAuth2User 객체가 생성됩니다.
//loadUser() 메서드 실행:
//loadUser() 메서드는 OAuth2UserService에서 구현된 메서드로, 구글에서 받은 정보를 사용해 **OAuth2User**를 반환합니다.
//이 **OAuth2User**는 인증된 사용자의 정보를 포함합니다. Spring Security가 이 정보를 사용하여 **OAuth2AuthenticationToken**을 생성합니다.
//OAuth2AuthenticationToken 생성:
//OAuth2AuthenticationToken 객체는 사용자 정보와 권한을 포함합니다. 이 객체는 인증된 사용자의 정보를 Spring Security의 SecurityContext에 저장합니다.
//Spring Security의 SecurityContext:
//OAuth2AuthenticationToken은 SecurityContext에 저장되어, 후속 요청에서 사용자의 인증 상태를 유지하고 사용자의 권한을 관리합니다.
//이 정보는 애플리케이션 내에서 @AuthenticationPrincipal 또는 **SecurityContextHolder.getContext().getAuthentication()**을 사용해 접근할 수 있습니다.



//MessageMapping("/chats/{chatroomId}")
//@SendTo("/sub/chats/{chatroomId}")
//public ChatMessage handleMessage(...) {
//    // 👇 ① 클라이언트가 보낸 메시지를 서버에서 처리
//    chatService.saveMessage(...); // DB 저장
//
//    messagingTemplate.convertAndSend("/sub/chats/news", chatroomId); // 새로운 메시지 알림용 전송
//
//    // 👇 ② 이 리턴값을 @SendTo 경로에 자동으로 브로드캐스트
//    return new ChatMessage(...);
//}



//⚙️ 흐름 순서 정리
//단계	설명
//1️⃣	클라이언트가 /pub/chats/3로 메시지 전송
//2️⃣	서버의 @MessageMapping("/chats/{chatroomId}") 함수 실행
//3️⃣	내부 로직 (예: 메시지 저장, 로그 남기기 등) 실행
//4️⃣	return한 값이 @SendTo("/sub/chats/{chatroomId}") 구독자들에게 자동 전송
//5️⃣	동시에 messagingTemplate.convertAndSend()로 다른 채널에도 알림 보낼 수 있음