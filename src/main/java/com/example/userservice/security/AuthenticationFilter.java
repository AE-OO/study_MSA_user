package com.example.userservice.security;

import com.example.userservice.dto.UserDto;
import com.example.userservice.service.UserService;
import com.example.userservice.vo.RequestLogin;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * 사용자가 로그인하면 가장 먼저 실행되는 클래스
 */
@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private UserService userService;
    private Environment env; // 토큰의 만료기간, 토큰의 키워드 등을 yml 파일에 저장해 가져오기 위함

    public AuthenticationFilter(AuthenticationManager authenticationManager,
                                UserService userService, Environment env) {
        super.setAuthenticationManager(authenticationManager);
        this.userService = userService;
        this.env = env;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        try{
            // 전달시켜주는 데이터가 post 방식으로 들어오는데 post 방식은 requestParameter로 받을 수 없어서
            // inputStream을 사용해 어떤 데이터가 들어왔는지 확인해 볼 수 있도록 함
            RequestLogin creds = new ObjectMapper().readValue(request.getInputStream(), RequestLogin.class);

            // 사용자가 입력한 값을 토큰으로 변환시키고 인증 처리해주는 manager로 넘기면 manager가 아이디와 패스워드를 비교해 줌
            return getAuthenticationManager()
                    .authenticate(
                    // 사용자가 입력한 아이디와 이메일 값을 spring security의 값으로 변환하기 위해 userNameAuthenticationToken으로 변환
                    new UsernamePasswordAuthenticationToken(creds.getEmail(), creds.getPassword(), new ArrayList<>())
                    );
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 로그인에 성공했을 때 어떤 처리를 할 지
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        String userName = ((User) authResult.getPrincipal()).getUsername();
        UserDto userDetails = userService.getUserDetailsByEmail(userName);

        // 토큰 생성
        String token = Jwts.builder()
                .setSubject(userDetails.getUserId())
                .setExpiration(new Date(System.currentTimeMillis() +
                        Long.parseLong(env.getProperty("token.expiration_time")))) // 현재 시간과 env의 토큰 만료시간을 더해서 언제 토큰이 만료될지도 토큰에 포함함
                .signWith(SignatureAlgorithm.HS512, env.getProperty("token.secret")) // 암호화
                .compact();

        // 만들어진 토큰을 헤더에 담음
        response.addHeader("token", token);

        // 중간에 토큰이 변경되지 않음을 확인하기 위한 userId
        response.addHeader("userId", userDetails.getUserId());
    }
}
