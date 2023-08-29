package com.example.userservice.security;

import com.example.userservice.service.UserService;
import com.example.userservice.vo.RequestLogin;
import com.fasterxml.jackson.databind.ObjectMapper;
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

/**
 * 사용자가 로그인하면 가장 먼저 실행되는 클래스
 */
@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private UserService userService;
    private Environment env;

    public AuthenticationFilter(AuthenticationManager authenticationManager, UserService userService, Environment env) {
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
        String username = ((User) authResult.getPrincipal()).getUsername();
    }
}
