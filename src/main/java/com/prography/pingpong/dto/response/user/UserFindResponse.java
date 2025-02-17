package com.prography.pingpong.dto.response.user;

import com.prography.pingpong.domain.user.User;
import com.prography.pingpong.domain.user.UserStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record UserFindResponse(
        long id,
        long fakerId,
        @NotBlank String name,
        @NotBlank String email,
        @NotNull UserStatus status,
        LocalDateTime createdAt,
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
