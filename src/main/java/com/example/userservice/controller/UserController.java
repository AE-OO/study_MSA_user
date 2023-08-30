package com.example.userservice.controller;

import com.example.userservice.dto.UserDto;
import com.example.userservice.jpa.UserEntity;
import com.example.userservice.service.UserService;
import com.example.userservice.vo.Greeting;
import com.example.userservice.vo.RequestUser;
import com.example.userservice.vo.ResponseUser;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
public class UserController {

    /**
     * application.yml 파일에 있는 변수 사용하는 방법
     * 1. Environment 사용
     * 2. @Value 사용
     */
    private Environment env;
    private UserService userService;

    @Autowired
    public UserController(Environment env, UserService userService){
        this.env = env;
        this.userService = userService;
    }

    @Autowired
    private Greeting greeting;

    /**
     * Eureka 서버에 등록 상태를 체크함
     */
    @GetMapping("/health_check")
    public String status(){
//        return String.format("It's Working in User Service on PORT %s", env.getProperty("local.server.port"));
        return String.format("It's Working on User Service"
            + ", port(local.server.port) = " + env.getProperty("local.server.port")
            + ", port(token.secret) = " + env.getProperty("token.secret")
            + ", token secret = " + env.getProperty("token.secret")
            + ", token expiration time = " + env.getProperty("token expiration time"));
    }

    @GetMapping("/welcome")
    public String welcome(){
//        return env.getProperty("greeting.message");
        return greeting.getMessage();
    }

    /**
     * 회원가입
     */
    @PostMapping("/users")
    public ResponseEntity<ResponseUser> createUser(@RequestBody RequestUser user){
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = mapper.map(user, UserDto.class);
        userService.createUser(userDto);

        ResponseUser responseUser = mapper.map(userDto, ResponseUser.class);

//        return new ResponseEntity(HttpStatus.CREATED);  post에 의해서 정상적으로 데이터가 반영이 되었을 떄는 '200 ok' 보다 '201 created' 라는 코드를 반환시키는게 더 알맞음

        // 회원가입 완료 시 회원가입된 정보도 반환하기 위함
        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }

    /**
     * 전체 사용자 목록
     */
    @GetMapping("/users")
    public ResponseEntity<List<ResponseUser>> getUsers(){
        Iterable<UserEntity> userList = userService.getUserByAll();

        List<ResponseUser> result = new ArrayList<>();
        userList.forEach(v -> {
            result.add(new ModelMapper().map(v, ResponseUser.class));
        });

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * 아이디로 사용자 검색
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<ResponseUser> getUser(@PathVariable String userId){
        UserDto userList = userService.getUserByUserId(userId);

        ResponseUser returnValue = new ModelMapper().map(userList, ResponseUser.class);

        return ResponseEntity.status(HttpStatus.OK).body(returnValue);
    }
}
