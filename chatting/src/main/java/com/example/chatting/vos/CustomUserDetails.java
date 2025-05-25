package com.example.chatting.vos;

import com.example.chatting.entities.Member;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Map;

//CustomUserDetails 가 소셜로그인 기능인 걸 상속해 다 사용할수 있게 한다 서버단 방생성 할때 일반 상담사 로그인도 처리 해야해서 이렇게 진행
public class CustomUserDetails extends CustomOAuth2User implements UserDetails {


    public CustomUserDetails(Member member, Map<String, Object> attributeMap) {
        super(member, attributeMap);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(member.getRole()));
    }

    @Override
    public String getPassword() {
        return this.member.getPassword();
    }

    @Override
    public String getUsername() {
        return this.member.getName();
    }
}
