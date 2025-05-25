package com.example.chatting.vos;

import com.example.chatting.entities.Member;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;
@AllArgsConstructor
public class CustomOAuth2User implements OAuth2User {  // CustomOAuth2User 서버 측에서 사용되는 객체
   // @AllArgsConstructor <<이걸로인한
    //내부에서 생성
    //public CustomOAuth2User(Member member, Map<String, Object> attributeMap) {
    //    this.member = member;
    //    this.attributeMap = attributeMap;
    //}
//그래서 생성자 생성할때 모든 곳에서 접근이 가능하고 필드에 생성자 생성할떄 주입된것으로 get을 써서 가져오는 방식

    Member member;
    Map<String ,Object> attributeMap;


    public Member getMember(){ //멤버타입의 정보
        return this.member;
    }

    @Override
    public Map<String, Object> getAttributes() { //소셜로그인 정보
       return this.attributeMap;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { //유저의 권한정보담음
        return List.of(()-> this.member.getRole());
    }

    @Override
    public String getName() {//유저의 이름
        return this.member.getNickName();
    }
}
