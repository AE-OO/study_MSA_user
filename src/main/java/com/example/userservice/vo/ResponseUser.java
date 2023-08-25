package com.example.userservice.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * 회원가입 완료 시 사용자에게 회원가입된 정보를 반환하기 위한 클래스
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // json 데이터 중 null 값이 들어오는 것은 버리고 값이 있는 것만 사용
public class ResponseUser {
    private String email;
    private String name;
    private String userId;

    private List<ResponseOrder> orders;
}
