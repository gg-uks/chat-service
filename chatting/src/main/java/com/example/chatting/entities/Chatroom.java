package com.example.chatting.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Chatroom {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatroom_id")
    @Id
    Long id;

    String title;

    @OneToMany(mappedBy = "chatroom")
    Set<MemberChatroomMapping> memberChatroomMappingSet;

    LocalDateTime createdAt;

    @Transient //우리가 엔티티의 속성들은 테이블의 컬럼으로 매핑되는데 이어노테이션이 붙으면 테이블에는 적용안됨
    Boolean hasNewMessage; //hasNewMessage true나 false로 세팅해줌으로써 새로운 메시지나 아니거나 설정

    public void setHasNewMessage(Boolean hasNewMessage) {
        this.hasNewMessage = hasNewMessage;
    }

    public MemberChatroomMapping addMember(Member member) {
        if (this.getMemberChatroomMappingSet() == null) {
            this.memberChatroomMappingSet = new HashSet<>();
        }

        MemberChatroomMapping memberChatroomMapping = MemberChatroomMapping.builder()
                .member(member)
                .chatroom(this) //addMember메서드 실행하기전에 디비에 저장된 chatroom을 가리킴 그래서 이정보랑 멤버정보를 memberChatroomMapping 에 저장후
                .build();

        this.memberChatroomMappingSet.add(memberChatroomMapping); //memberChatroomMappingSet 에 저장 두가지 엔티티의 정보

        return memberChatroomMapping;
    }
}
