package com.prography.ping_pong.dto.response.room;

import com.prography.ping_pong.domain.room.Room;
import com.prography.ping_pong.domain.room.RoomStatus;
import com.prography.ping_pong.domain.room.RoomType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record RoomDetailResponse(
        long id,
        @NotBlank String title,
        long hostId,
        @NotNull RoomType roomType,
        @NotNull RoomStatus status,
        @NotNull LocalDateTime createdAt,
        @NotNull LocalDateTime updatedAt
) {

    public RoomDetailResponse(Room room) {
        this(
                room.getId(),
                room.getTitle(),
                room.getHostId(),
                room.getRoomType(),
                room.getStatus(),
                room.getCreatedAt(),
                room.getUpdatedAt()
        );
    }
}
