package com.example.userservice.vo;

import lombok.Data;

/**
 * 회원가입 완료 시 사용자에게 회원가입된 정보를 반환하기 위한 클래스
 */
@Data
public class ResponseUser {
    private String email;
    private String name;
    private String userId;
}
