package com.prography.pingpong.dto.response.room;

import com.prography.pingpong.domain.room.Room;
import java.util.List;
import org.springframework.data.domain.Page;

public record RoomPageResponse(
        long totalElements,
        long totalPages,
        List<RoomFindResponse> roomList
) {

    public RoomPageResponse(Page<Room> roomPage) {
        this(
                roomPage.getTotalElements(),
                roomPage.getTotalPages(),
                mapToRoomFindResponses(roomPage.toList())
        );
    }

    private static List<RoomFindResponse> mapToRoomFindResponses(List<Room> rooms) {
        return rooms.stream()
                .map(RoomFindResponse::new)
                .toList();
    }
}
