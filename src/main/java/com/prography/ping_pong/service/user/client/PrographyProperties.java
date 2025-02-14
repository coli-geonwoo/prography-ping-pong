package com.prography.ping_pong.service.user.client;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "prography")
public record PrographyProperties(
        @NotBlank String baseUrl,
        @NotBlank String locale
) {

}
