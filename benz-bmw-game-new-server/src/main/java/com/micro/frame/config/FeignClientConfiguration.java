package com.micro.frame.config;

import feign.Request;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfiguration {
    @Bean
    public Request.Options options() {
        return new Request.Options(6000, 6000);
    }
}
