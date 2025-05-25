package com.example.chatting.enums;

import java.util.Arrays;

public enum Role {

    USER("ROLE_USER"),CONSULTANT("ROLE_CONSULTANT");

    String code;

    Role(String code){
        this.code = code;
    }
    public String getCode(){
        return code;
    }

    public static Role fromCode(String code){ //formCode 라는 메서드를 사용해 code라는 스트링이 들어왔을때
        return Arrays.stream(Role.values()) // Role enum에 정의된 모든 값들을 배열로 -> 배열을 스트림(Stream) 으로 변환
                .filter(role -> role.getCode().equals(code)) //조건에 맞는 enum 값만 필터링->파라미터로 받은 코드와 동일하다면 각 role에 대해 getCode() 값이 code와 일치하는지 필터링
                .findFirst()    //	조건을 만족하는 첫 번째 값을 Optional로 반환
                .orElseThrow(); //그렇지 않으면 얘외 발생
    }
}
