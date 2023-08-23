package com.example.userservice.vo;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * application.yml 파일의 메세지를 가져오기 위한 클래스
 */
@Component // 용도가 정해지지 않은 클래스를 bean에 등록할 때 사용
@Data
public class Greeting {

    @Value("${greeting.message}")
    private String message;
}
