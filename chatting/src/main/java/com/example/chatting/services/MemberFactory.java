package com.example.chatting.services;

import com.example.chatting.entities.Member;
import com.example.chatting.enums.Gender;
import com.example.chatting.enums.Role;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class MemberFactory {

    public static Member create(OAuth2UserRequest userRequest, OAuth2User oAuth2User){ //어떠한 서비스에서 넘어온 정보에따라서 가진정보를 활용해서 멤버를 반환해 줘야 한다

        return switch (userRequest.getClientRegistration().getRegistrationId()){ //userRequest에 클라이언트 레지스트레이션에 어떤 서비스에 값이 넘어왔는지 확인할수 있어서 메서드 인자로 받음
            case "kakao" -> {
                //기존에 만들어논 커스텀 소셜 유저 서비스 클래스
                Map<String,Object> attributeMap = oAuth2User.getAttribute("kakao_account");
                yield  Member.builder() //yield -> 스위치 문에서 값을 반환하기 위한
                        .email((String) attributeMap.get("email"))
                        .nickName((String) ((Map)attributeMap.get("profile")).get("nickname"))
                        .name((String) attributeMap.get("name"))
                        .phoneNumber((String) attributeMap.get("phone_number"))
                        .gender(Gender.valueOf(((String) attributeMap.get("gender")).toUpperCase())) //Enum
                        .birthDay(getBirthDay(attributeMap))
                        .role(Role.USER.getCode())
                        .build();
            }
            case "google" -> {
                Map<String,Object> attributeMap = oAuth2User.getAttributes();
                //builder 패턴처럼 긴 객체 생성 로직을 case 안에서 처리할 수 있고
                //복수 줄 코드 후 yield로 결과만 넘기면 깔끔 ->	switch 블록 안에서 여러 줄 로직 쓸 경우엔 yield 필수
                yield  Member.builder()
                        .email((String) attributeMap.get("email")) //이메일
                        .nickName((String)attributeMap.get("given_name")) //성빼고 이름
                        .name((String) attributeMap.get("name")) //이름
                        .role(Role.USER.getCode())
                        .build();
            }
            default -> throw new IllegalStateException("연동되지 않은 서비스 입니다: " ); //그이외에 다른 서비스를 통해 메서드를 호출하게되면 연동 X
        };
    }
    //생일 정보 불러오는 메서드
    private static LocalDate getBirthDay(Map<String, Object> attributeMap) {
        String birthYear = (String) attributeMap.get("birthyear");
        String birthDay = (String) attributeMap.get("birthday");
        return LocalDate.parse(birthYear + birthDay, DateTimeFormatter.BASIC_ISO_DATE);
    }
}
