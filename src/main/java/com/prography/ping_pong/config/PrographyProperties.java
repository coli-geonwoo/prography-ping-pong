package com.prography.ping_pong.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "prography")
public record PrographyProperties(
        String baseUrl,
        String locale
) {

}
