package com.example.chatting.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    @Order(2)//이필터가 두번째로 설정이된다
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .authorizeHttpRequests(request -> request.anyRequest().authenticated()) //서버로 들어오는것은 인증이 필요하다
            .oauth2Login(Customizer.withDefaults())  //소셜로그인으로 인증을 할것이다
            .formLogin(Customizer.withDefaults())   //@Order(1)은 로그인 안 되어 있으면 → Redirect to /login -> 그래서 오더2에서도 디폴트 로그인폼을 적용시켜야 상담사 일반 로그인도 처리 가능함
            .csrf(csrf -> csrf.disable());

        return httpSecurity.build();
    }

    @Order(1)//이필터가 먼저 적용이 된다
    @Bean
    public SecurityFilterChain consultantSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity //상담사로 먼저 로그인할경우 유저페이지로 이동해도 오류안나고 유저로 로그인후 상담사 url로 들어가면 디나이 된다 권한체크때문에
            .securityMatcher("/consultants/**") //consultants하위로 들어오는 요청
            .authorizeHttpRequests(request -> request
                .requestMatchers(HttpMethod.POST, "/consultants").permitAll()
                .anyRequest().hasRole("CONSULTANT")) //상담사들만 접근이 가능하다  //hasRole 키워드를 사용하면 ROLE_  << 이거를 안붙혀도 된다
            .formLogin(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable());

        return httpSecurity.build();
    }

    @Bean // 비밀번호 암호화
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}