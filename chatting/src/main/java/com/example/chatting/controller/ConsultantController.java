package com.example.chatting.controller;

import com.example.chatting.dtos.ChatroomDto;
import com.example.chatting.dtos.MemberDto;
import com.example.chatting.services.ConsultantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/consultants")
@Controller
public class ConsultantController {

    private final ConsultantService consultantService; //커스텀된 유저 디테일 서비스를 사용하여 정보 가져오기

    @ResponseBody //해당컨트롤러가 REST컨트롤러가 아니라서 뷰네임으로 인식이 된다  실제 데이터를 내려주는 메서드이기때문에 리스폰스바디를 추가해줘야 한다
    @PostMapping
    public MemberDto saveMember(@RequestBody MemberDto memberDto){
        return consultantService.saveMember(memberDto);
    }

    @GetMapping
    public String index(){
        return "consultants/index.html";
    }

    @ResponseBody
    @GetMapping("/chats")
    public Page<ChatroomDto> getChatroomPage(Pageable pageable){ // JPA에서 제공되는 페이징 처리 기능
        return consultantService.getChatroomPage(pageable);
    }
}
