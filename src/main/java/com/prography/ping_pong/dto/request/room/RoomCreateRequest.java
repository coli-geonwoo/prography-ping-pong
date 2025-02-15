package com.prography.ping_pong.dto.request.room;

import com.prography.ping_pong.domain.room.RoomType;

public record RoomCreateRequest(
        long userId,
        RoomType roomType,
        String title
) {

}
