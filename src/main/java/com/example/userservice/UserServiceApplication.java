package com.example.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

    /**
     * UserServiceImpl 에서 생성자에 BCryptPasswordEncoder 를 사용하기 위해 
     * 프로젝트를 구동시킬 떄 가장 먼저 실행되는 application에 BCryptPasswordEncoder를 Bean에 등록한다는 내용을 작성함
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder(){ 
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    @LoadBalanced  // url을 호출해 올 때 load balancer에 등록된 이름으로 호출해 올 수 있음
    // ex) 127.0.0.1:8000/order-service/orders => order-service/order-serivce/orders
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }
}
