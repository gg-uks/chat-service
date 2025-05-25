package com.example.chatting.dtos;

import com.example.chatting.entities.Member;
import com.example.chatting.enums.Gender;

import java.time.LocalDate;

public record MemberDto(
    Long id,
    String email,
    String nickName,
    String name,
    String password,
    String confirmedPassword, //두번째 입력받은 패스워드
    Gender gender,
    String phoneNumber,
    LocalDate birthday,
    String role) {

    public static MemberDto from(Member member) {
        return new MemberDto(
            member.getId(),
            member.getEmail(),
            member.getNickName(),
            member.getName(),
            null,
            null,
            member.getGender(),
            member.getPhoneNumber(),
            member.getBirthDay(),
            member.getRole()
        );
    }

    public static Member to(MemberDto memberDto) {
        return Member.builder()
            .id(memberDto.id())
            .email(memberDto.email())
            .nickName(memberDto.nickName())
            .name(memberDto.name())
            .gender(memberDto.gender())
            .phoneNumber(memberDto.phoneNumber())
            .birthDay(memberDto.birthday())
            .role(memberDto.role())
            .build();
    }
}


//2. from/to 변환 메서드로 엔티티 ↔ DTO 전환 분리
//✅ from(Member member):
//엔티티 → DTO 변환 시 사용
//민감 정보(password, confirmedPassword)는 null로 설정해 보안 유지
//✅ to(MemberDto dto):
//DTO → 엔티티 변환 시 사용
//예를 들어, 클라이언트에서 받은 데이터로 Member 객체를 생성할 때 사용합니다.


//사용자가 회원가입 요청 → MemberDto로 받음
//to(MemberDto) 호출해 Member 엔티티로 변환
//DB에 저장
//사용자 정보 조회 → Member 엔티티로 가져옴
//from(Member)로 DTO로 변환 후 프론트에 반환