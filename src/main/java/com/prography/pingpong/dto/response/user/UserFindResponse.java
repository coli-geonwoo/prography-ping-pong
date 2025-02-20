package com.prography.pingpong.dto.response.user;

import com.prography.pingpong.domain.user.User;
import com.prography.pingpong.domain.user.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record UserFindResponse(
        @Schema(description = "유저 아이디", example = "1")
        long id,

        @Schema(description = "유저 초기화 아이디", example = "1")
        long fakerId,

        @NotBlank
        @Schema(description = "유저 이름", example = "김건우")
        String name,

        @NotBlank
        @Schema(description = "유저 이메일", example = "email@email.com")
        String email,

        @NotNull
        @Schema(description = "유저 상태", example = "ACTIVe")
        UserStatus status,

        @Schema(description = "유저 생성시기", example = "2024-01-14 02:22:45")
        LocalDateTime createdAt,

        @Schema(description = "유저 정보 수정시기", example = "2024-01-14 02:22:45")
        LocalDateTime updatedAt
) {

    public UserFindResponse(User user) {
        this(
                user.getId(),
                user.getFakerId(),
                user.getName(),
                user.getEmail(),
                user.getStatus(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
