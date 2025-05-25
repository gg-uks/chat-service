package com.example.chatting.services;

import com.example.chatting.dtos.ChatroomDto;
import com.example.chatting.entities.Chatroom;
import com.example.chatting.entities.Member;
import com.example.chatting.entities.MemberChatroomMapping;
import com.example.chatting.entities.Message;
import com.example.chatting.repositories.ChatroomRepository;
import com.example.chatting.repositories.MemberChatroomMappingRepository;
import com.example.chatting.repositories.MessageRepositroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatroomRepository chatroomRepository;
    private final MemberChatroomMappingRepository memberChatroomMappingRepository;
    private final MessageRepositroy messageRepository;

    public Chatroom createChatroom(Member member , String title){
        Chatroom chatroom = Chatroom.builder()
                .title(title)
                .createdAt(LocalDateTime.now())
                .build();
        chatroom = chatroomRepository.save(chatroom);

        MemberChatroomMapping memberChatroomMapping = chatroom.addMember(member);
        memberChatroomMapping = memberChatroomMappingRepository.save(memberChatroomMapping);

        return chatroom;
    }
    public Boolean joinChatroom(Member member , Long newchatroomId, Long currentChatroomId){  //다른사용자가 해당 채팅방을 들어오는 메서드
        if(currentChatroomId != null){
            updateLastCheckedAt(member,currentChatroomId);//내가 마지막으로 봤던 채팅방에서 어디까지 읽었는지
        }
        if(memberChatroomMappingRepository.existsByMemberIdAndChatroomId(member.getId(),newchatroomId)){ //해당 memberId와 chatroomId 조합으로 된 레코드가 존재하는가
            log.info("이미 참여한 채팅방입니다");
            return false;
        }
        Chatroom chatroom = chatroomRepository.findById(newchatroomId).get(); //옵셔널 타입이라 get() 을 써줘서 데이터를 넣어줘야 한다

        MemberChatroomMapping memberChatroomMapping = MemberChatroomMapping.builder()
                .member(member)
                .chatroom(chatroom)
                .build();
        memberChatroomMapping = memberChatroomMappingRepository.save(memberChatroomMapping);

        return true;
    }

    private void updateLastCheckedAt(Member member, Long currentChatroomId) {
        MemberChatroomMapping memberChatroomMapping = memberChatroomMappingRepository.findByMemberIdAndChatroomId(member.getId(), currentChatroomId)
                .get();
        memberChatroomMapping.updateLastCheckedAt();

        memberChatroomMappingRepository.save(memberChatroomMapping);
    }

    @Transactional
    public Boolean leaveChatroom(Member member,Long chatroomId){ //채팅방 목록 나가기
        if(!memberChatroomMappingRepository.existsByMemberIdAndChatroomId(member.getId(),chatroomId)){ //해당 memberId와 chatroomId 조합으로 된 레코드가 존재하는가
            log.info("참여하지 않은 방입니다");
            return false;
        }
        memberChatroomMappingRepository.deleteByMemberIdAndChatroomId(member.getId(),chatroomId);
        return true;
    }

    public List<Chatroom> getChatroomList(Member member){ //참여한 채팅방 목록을 가져오는 메서드
        List<MemberChatroomMapping> memberChatroomMappingList = memberChatroomMappingRepository.findAllByMemberId(member.getId());
        //MemberChatroomMapping은 Many-to-Many 관계를 위한 중간 테이블(Entity) 역할
        return memberChatroomMappingList.stream()//아이디에 해당되는 방들을 꺼내서 리스트로 리턴한다
                .map(memberChatroomMapping->{
                    Chatroom chatroom = memberChatroomMapping.getChatroom();
                    chatroom.setHasNewMessage(messageRepository.existsByChatroomIdAndCreatedAtAfter(chatroom.getId() , memberChatroomMapping.getLastCheckedAt())); // 채팅방의 아이디와  memberChatroomMapping에 마지막으로 체크했던 시간을주면 / 어떤 특정 채팅방에 마지막으로 체크했던 시간보다 이후에 메시지가 있다면 새로운 메시지가 있다는것이다
                    //해당 채팅방에서 마지막 확인 시간 이후에 새 메시지가 존재하는지 검사
                    //MemberChatroomMapping 객체 안에서 정의된 Chatroom 필드를 리스트로 변환해서 리턴
                    //어떠한 채팅방이 새로운 메시지가 있는지 없는지 알수 있음

                    return chatroom;
                })
                .toList();
    }

    //사용자가 메시지를 작성했을떄 그메시지들을 디비에 저장  어떤 채팅방에 들어갔을때 그채팅방에 작성된 과거의 메시지 불러오기(두가지 기능)

    //메시지 저장

    public Message saveMessage(Member member ,Long chatroomId, String text){
        Chatroom chatroom = chatroomRepository.findById(chatroomId).get();  //현재 채팅방이 존재하는지

        Message message = Message.builder() //빌더는 특정 값이 아닌 객체로 저장해야 JPA 가 저장시킬수 있음
                .text(text)  // 메시지 내용
                .member(member) // 보낸 사람
                .chatroom(chatroom) // 채팅방
                .createdAt(LocalDateTime.now())
                .build(); // 최종 Message 객체 완성

        return messageRepository.save(message);
    }

    //특정 채팅방에서 작성된 모든 메시지를 가지고오는 메서드
    public List<Message> getMessageList(Long chatroomId){
        return messageRepository.findAllByChatroomId(chatroomId);
    }

    @Transactional
    public ChatroomDto getChatroom(Long chatroomId){
        Chatroom chatroom = chatroomRepository.findById(chatroomId).get();
        return ChatroomDto.from(chatroom);
    }
}
