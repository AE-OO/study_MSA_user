package com.example.userservice.security;

import com.example.userservice.service.UserService;
import jakarta.servlet.Filter;
import org.hibernate.cfg.Environment;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration // 다른 쪽의 bean들보다 우선순위를 앞에 두고 등록함
@EnableWebSecurity
public class WebSecurity{

    private UserService userService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private Environment env;

    public WebSecurity(UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder, Environment env){
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.env = env;
    }

    /**
     * 권한 처리를 위한 config
     */
    @Bean
    protected SecurityFilterChain configure(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);
        return http.csrf(csrf -> csrf.disable())
                .headers(authorize -> authorize.frameOptions(frameOptions -> frameOptions.disable()))
                .authorizeRequests(authorize -> {
                    try {
                        authorize.requestMatchers(mvcMatcherBuilder.pattern("/**"))
                                .hasIpAddress("192.168.0.8").and()
                                .addFilter(getAuthenticationFilter(http));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .getOrBuild();
    }

    private AuthenticationFilter getAuthenticationFilter(HttpSecurity http) throws Exception {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(http.getSharedObject(AuthenticationConfiguration.class).getAuthenticationManager());
        return authenticationFilter;
    }

    /**
     * 인증처리를 위한 config
     */
    @Bean
    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
        auth.userDetailsService(userService) // userDetailsService() : 사용자가 전달했던 username(email)과 pwd로 로그인 처리를 함
                .passwordEncoder(bCryptPasswordEncoder); // DB의 pwd는 암호화되어 있으니까 사용자한테 받아온 pwd도 암호화처리해서 비교함
    }
}
