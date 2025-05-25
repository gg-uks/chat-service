package com.example.chatting.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class MemberChatroomMapping {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_chatroom_mapping_id")
    @Id
    Long id;

    @JoinColumn(name = "member_id") // member_id 컬럼을 통해사용자와 연관관계를 맺음
    @ManyToOne
    Member member;

    @JoinColumn(name = "chatroom_id") //chatroom_id 라는 컬럼을 통해 채팅방과 연관관계를 맺음
    @ManyToOne
    Chatroom chatroom;

    LocalDateTime lastCheckedAt;

    public void updateLastCheckedAt() {
        this.lastCheckedAt = LocalDateTime.now();
    }
    //그리고 사용자가 어떠한 채팅방을 마지막에 체크했는지 알수 있어야 그 체크된 이후에 생성된 메시지를알수 있다
    //lastCheckedAt 보다 createdAt 더 늦은 메시지가 있을때 신규메시지가 발행된걸로 봐야한다

}
