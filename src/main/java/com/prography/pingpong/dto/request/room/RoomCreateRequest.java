package com.prography.pingpong.dto.request.room;

import com.prography.pingpong.domain.room.Room;
import com.prography.pingpong.domain.room.RoomType;
import jakarta.validation.constraints.NotNull;

public record RoomCreateRequest(
        long userId,
        @NotNull RoomType roomType,
        @NotNull String title
) {

    public Room toRoom() {
        return new Room(title, userId, roomType);
    }
}
