package com.example.chatting.services;

import com.example.chatting.entities.Member;
import com.example.chatting.enums.Gender;
import com.example.chatting.repositories.MemberRepository;
import com.example.chatting.vos.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService { //DefaultOAuth2UserService 이거를 사용해 필요한것만 사용한다

    final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

//        Map<String,Object> attributeMap = oAuth2User.getAttribute("kakao_account");
        String email = oAuth2User.getAttribute("email");
        Member member = memberRepository.findByEmail(email)
                .orElseGet(() ->{
                     Member newMember = MemberFactory.create(userRequest,oAuth2User);
                     return memberRepository.save(newMember);
                }); //값이 없는경우에는 등록해서 반환해 주는뜻이다

        return new CustomOAuth2User(member,oAuth2User.getAttributes());
    }
    //새 회원 등록 메서드 ->   // DB에 이메일이 있으면 그 사용자 사용, 없으면 registerMember 호출
//    private Member registerMember(Map<String,Object>attributeMap){
//        Member member = Member.builder()
//                .email((String) attributeMap.get("email"))
//                .nickName((String) ((Map)attributeMap.get("profile")).get("nickname"))
//                .name((String) attributeMap.get("name"))
//                .phoneNumber((String) attributeMap.get("phone_number"))
//                .gender(Gender.valueOf(((String) attributeMap.get("gender")).toUpperCase())) //Enum
//                .birthDay(getBirthDay(attributeMap))
//                .role("ROLE_USER")
//                .build();
//        return memberRepository.save(member);
//    }
        //Java enum은 대소문자 구분함.
        //Gender.valueOf("male") 이러면 IllegalArgumentException 에러가남.
        //그래서 문자열을 모두 대문자로 바꿔서 Gender.valueOf(...)에 넘기는 거야:
        //오해	                                    사실
        //valueOf()는 아무 문자열이나 받아서 처리해준다	❌ 아니다. 정해진 Enum 이름과 완전 일치해야 한다
        //valueOf()는 안전하게 문자열을 enum으로 바꿔준다	❌ 아니고, 틀리면 예외 던진다
        //valueOf("male")도 작동한다	                ❌ 아니다. "MALE"만 작동한다
        //Enum.valueOf()는 전달된 문자열이 해당 Enum 클래스에 _정확히 일치_해야 작동

    //생일 정보 불러오는 메서드
    private LocalDate getBirthDay(Map<String,Object>attributeMap) {
        String birthYear = (String) attributeMap.get("birthyear");
        String birthDay = (String) attributeMap.get("birthday");
        return LocalDate.parse(birthYear + birthDay, DateTimeFormatter.BASIC_ISO_DATE);
    }
}
