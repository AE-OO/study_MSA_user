package com.example.userservice.security;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration // 다른 쪽의 bean들보다 우선순위를 앞에 두고 등록함
@EnableWebSecurity
public class WebSecurity{

    @Bean
    protected SecurityFilterChain config(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);
        return http.csrf(csrf -> csrf.disable())
                .headers(authorize -> authorize.frameOptions(frameOptions -> frameOptions.disable()))
                .authorizeHttpRequests(authorize -> authorize.requestMatchers(mvcMatcherBuilder.pattern("/**")).permitAll())
                .getOrBuild();
    }
}
