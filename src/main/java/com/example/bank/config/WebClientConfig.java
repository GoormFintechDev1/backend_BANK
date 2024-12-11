package com.example.bank.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
	
	@Value("${pos.api.url}")
	private String posUrl;
	
    @Bean(name = "webClient8083")
    WebClient webClient8083(WebClient.Builder builder) {
        return builder
                .baseUrl(posUrl) // 기본 URL 설정
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)) // 버퍼 크기 10MB로 확장
                .build();
    }
}
