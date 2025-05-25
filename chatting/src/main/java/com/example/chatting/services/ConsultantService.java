package com.example.chatting.services;

import com.example.chatting.dtos.ChatroomDto;
import com.example.chatting.dtos.MemberDto;
import com.example.chatting.entities.Chatroom;
import com.example.chatting.entities.Member;
import com.example.chatting.enums.Role;
import com.example.chatting.repositories.ChatroomRepository;
import com.example.chatting.repositories.MemberRepository;
import com.example.chatting.vos.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ConsultantService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final ChatroomRepository chatroomRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByName(username).get();

        if (Role.fromCode(member.getRole()) != Role.CONSULTANT) { //fromCode(member.getRole() 조건에 맞는 Role값을 반환
            throw new AccessDeniedException("상담사가 아닙니다.");
        }
        return new CustomUserDetails(member,null);
    }

    public MemberDto saveMember(MemberDto memberDto){ //패스워드는 to() 변환 시 제외되었어도, 클라이언트가 보낸 MemberDto 안에는 여전히 password 값이 들어 있음
        Member member = MemberDto.to(memberDto); //패스워드 컬럼을 제외하고 저장 ->to() 메서드에서는 비밀번호 같은 민감한 데이터는 직접 변환에서 제외
        member.updatePassword(memberDto.password(),memberDto.confirmedPassword(),passwordEncoder);//여기서 메서드로 비밀번호 일치 확인후 인코딩후 저장 ->직접 password를 비교/암호화 후 Member 엔티티의 password 필드에 넣는 작업

        member = memberRepository.save(member); //memberRepository.save(member) 멤버정보를 업데이트해서 정보 업데이트 인코딩된 멤버로

        return MemberDto.from(member); //패스워드를 널값으로 변환해서 리턴 -> 응답 시에는 보안상 패스워드를 포함시키지 않으려고 MemberDto.from()에서 패스워드를 null 처리하거나 아예 포함하지 않고 반환
    }
    public Page<ChatroomDto> getChatroomPage(Pageable pageable){ //GET /consultants/chats?page=0&size=10&sort=id,desc -> Pageable 객체로 바인딩되어 컨트롤러나 서비스로 들어감
        Page<Chatroom> chatroomPage = chatroomRepository.findAll(pageable);//SELECT * FROM chatroomORDER BY id DESC LIMIT 10 OFFSET 0
        return chatroomPage.map(ChatroomDto::from); //map()을 쓰면 각 Chatroom 객체를 ChatroomDto로 변환해줍니다. ->결과적으로 프론트엔드는 필요한 데이터만 받고, 비밀번호나 민감 정보는 제외된 상태!
        //GET /consultants/chats?page=2&size=5&sort=id,desc  < 이렇게 요청이 올경우
        //page = 2 → 3번째 페이지 (0부터 시작)   size = 5 → 한 페이지에 5개씩
        //JPA가 내부적으로 다음 SQL을 생성
        //SELECT * FROM chatroom ORDER BY id DESC LIMIT 5 OFFSET 10
        //이 뜻은 조회한 데이터중에 레코드 10번 이후로 나온 5가지의 데이터를 가져와라 즉 5개씩 잘라서 번호가 몇인지 정한다는뜻
        //LIMIT	= size	5
        //OFFSET = page * size	10
        //✅ 페이지와 OFFSET 관계
        //요청한 page	의미	OFFSET 값 (건너뛸 개수)
        //0	1페이지 (첫 1~5번)	0 * size = 0
        //1	2페이지 (6~10번)	1 * size = 5
        //2	3페이지 (11~15번)	2 * size = 10 ✅
    }
}
