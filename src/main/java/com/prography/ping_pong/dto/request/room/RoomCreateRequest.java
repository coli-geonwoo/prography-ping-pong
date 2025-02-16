package com.prography.ping_pong.dto.request.room;

import com.prography.ping_pong.domain.room.Room;
import com.prography.ping_pong.domain.room.RoomType;
import com.prography.ping_pong.domain.user.User;
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
