package com.example.chatting.repositories;

import com.example.chatting.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageRepositroy extends JpaRepository<Message,Long> {

    List<Message> findAllByChatroomId(Long chatroomId); //메시지를 가지고 오는

    Boolean existsByChatroomIdAndCreatedAtAfter(Long id, LocalDateTime lastCheckedAt);//채팅방 100에서 2025-05-18 오전 10시 이후에 메시지가 있냐?
}
