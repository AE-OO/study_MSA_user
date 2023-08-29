package com.example.userservice.security;

import com.example.userservice.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Configuration // 다른 쪽의 bean들보다 우선순위를 앞에 두고 등록함
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {
    private UserService userService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private Environment env;

    public WebSecurity(Environment env, UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.env = env;
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    /**
     * 권한 처리를 위한 config
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
//        http.authorizeRequests().antMatchers("/users/**").permitAll();
        http.authorizeRequests().antMatchers("/**")    // 모든 주소에 대해서 통과시키지 않음
                .hasIpAddress("127.0.0.1")  // ip를 제한적으로 받음
                .and()
                .addFilter(getAuthenticationFilter()); // 해당 필터를 통과시킨 데이터에 대해서만 권한을 부여하고 작업을 진행함


        http.headers().frameOptions().disable();
    }

    private AuthenticationFilter getAuthenticationFilter() throws Exception {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager(), userService, env);
//        authenticationFilter.setAuthenticationManager(authenticationManager()); 생성자를 통해 set을 해주기 때문에 더이상 필요없음

        return authenticationFilter;
    }

    /**
     * 인증처리를 위한 config
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
        auth.userDetailsService(userService) // userDetailsService() : 사용자가 전달했던 username(email)과 pwd로 로그인 처리를 함
                .passwordEncoder(bCryptPasswordEncoder); // DB의 pwd는 암호화되어 있으니까 사용자한테 받아온 pwd도 암호화처리해서 비교함
    }
}
