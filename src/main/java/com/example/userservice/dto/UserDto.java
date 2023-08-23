package com.example.userservice.dto;

import lombok.Data;

import java.util.Date;

/**
 * 중간단계 클래스로 이동할 떄 사용하는 클래스
 */
@Data
public class UserDto {
    private String email;
    private String name;
    private String pwd;
    private String userId;
    private Date createdAt;

    private String encryptedPwd;
}
