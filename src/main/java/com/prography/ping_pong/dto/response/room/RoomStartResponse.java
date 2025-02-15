package com.prography.ping_pong.dto.response.room;

import com.prography.ping_pong.domain.room.Room;
import com.prography.ping_pong.domain.room.RoomStatus;
import com.prography.ping_pong.domain.room.RoomType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RoomStartResponse(
        long id,
        @NotBlank String title,
        long userId,
        @NotNull RoomType roomType,
        @NotNull RoomStatus status
) {
    public RoomStartResponse(Room room) {
        this(room.getId(), room.getTitle(), room.getHost().getId(), room.getRoomType(), room.getStatus());
    }
}
