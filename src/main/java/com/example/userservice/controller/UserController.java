package com.example.userservice.controller;

import com.example.userservice.vo.Greeting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class UserController {

    /**
     * application.yml 파일에 있는 변수 사용하는 방법
     * 1. Environment 사용
     * 2. @Value 사용
     */
    private Environment env;

    @Autowired
    public UserController(Environment env){
        this.env = env;
    }

    @Autowired
    private Greeting greeting;

    /**
     * Eureka 서버에 등록 상태를 체크함
     */
    @GetMapping("/health_check")
    public String status(){
        return "It's Working in User Service";
    }

    @GetMapping("/welcome")
    public String welcome(){
//        return env.getProperty("greeting.message");
        return greeting.getMessage();
    }
}
