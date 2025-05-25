package com.example.chatting.repositories;

import com.example.chatting.entities.Chatroom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatroomRepository extends JpaRepository<Chatroom,Long> {

}
