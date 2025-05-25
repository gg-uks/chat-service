package com.example.chatting.controller;


import java.util.List;

import com.example.chatting.dtos.ChatMessage;
import com.example.chatting.dtos.ChatroomDto;
import com.example.chatting.entities.Chatroom;
import com.example.chatting.entities.Member;
import com.example.chatting.entities.Message;
import com.example.chatting.services.ChatService;
import com.example.chatting.vos.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/chats")
@RestController
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ChatroomDto createChatroom(@AuthenticationPrincipal CustomOAuth2User user, @RequestParam String title) {
        Chatroom chatroom = chatService.createChatroom(user.getMember(), title);

        return ChatroomDto.from(chatroom);
    }

    @PostMapping("/{chatroomId}")
    public Boolean joinChatroom(@AuthenticationPrincipal CustomOAuth2User user, @PathVariable Long chatroomId, @RequestParam(required = false) Long currentChatroomId) {
        return chatService.joinChatroom(user.getMember(),chatroomId,currentChatroomId);
    }

    @DeleteMapping("/{chatroomId}")
    public Boolean leaveChatroom(@AuthenticationPrincipal CustomOAuth2User user, @PathVariable Long chatroomId) {
        return chatService.leaveChatroom(user.getMember(), chatroomId);
    }

    @GetMapping
    public List<ChatroomDto> getChatroomList(@AuthenticationPrincipal CustomOAuth2User user) {
        List<Chatroom> chatroomList = chatService.getChatroomList(user.getMember());

        return chatroomList.stream()
                .map(ChatroomDto::from)
                .toList();
    }

    @GetMapping("/{chatroomId}/messages") //메시지를 저장한것을 받아오는 메서드
    public List<ChatMessage> getMessageList(@PathVariable Long chatroomId) {
        List<Message> messageList = chatService.getMessageList(chatroomId);
        return messageList.stream()
                .map(message -> new ChatMessage(message.getMember().getNickName(), message.getText()))  // Member의 nickName을 추출 // Message의 텍스트를 추출  // List로 변환
                .toList(); //DTO로 변환함
        //엔티티와 DTO의 역할 분리: Message 엔티티는 서버에서 데이터를 저장하고 처리하는 역할을 하며, ChatMessage는 클라이언트에게 필요한 최소한의 데이터만을 전달하는 역할을 합니다.
        //보안과 효율성: 클라이언트에 불필요한 데이터를 전달하지 않기 위해 엔티티 객체와 DTO 객체를 분리하여 필요한 정보만 전달하는 방식이 권장됩니다.
    }
//
//    public List<Message> getMessageList(Long chatroomId) {
//        return messageRepository.findAllByChatroomId(chatroomId);
//    }
}