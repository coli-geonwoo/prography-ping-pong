package com.prography.pingpong.dto.response.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FakerUserResponse(
        @NotNull long id,
        @NotBlank String username,
        @NotBlank String email
) {

}
