package com.example.chatting.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Message {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    @Id
    Long id;

    String text;

    @JoinColumn(name = "member_id") // 유저한명은 여러개의 메시지를 가질수 있음
    @ManyToOne
    Member member;

    @JoinColumn(name = "chatroom_id") //하나의 채팅방은 여러개의 메시지를 가질수 있음
    @ManyToOne
    Chatroom chatroom;

    LocalDateTime createdAt; //각 메시지들은 생성일짜 가있어야 함

}
