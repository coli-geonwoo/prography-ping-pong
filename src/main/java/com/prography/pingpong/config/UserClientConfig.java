package com.prography.pingpong.config;

import com.prography.pingpong.exception.handler.FakerApiErrorHandler;
import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class UserClientConfig {

    private static final Duration DEFAULT_CONNECTION_TIMEOUT = Duration.ofSeconds(60);
    private static final Duration DEFAULT_READ_TIMEOUT = Duration.ofSeconds(30);

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder()
                .requestFactory(clientHttpRequestFactory())
                .defaultStatusHandler(new FakerApiErrorHandler());
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT);
        requestFactory.setReadTimeout(DEFAULT_READ_TIMEOUT);
        return requestFactory;
    }
}
