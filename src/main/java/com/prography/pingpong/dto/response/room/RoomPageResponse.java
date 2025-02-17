package com.prography.pingpong.dto.response.room;

import com.prography.pingpong.domain.room.Room;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.springframework.data.domain.Page;

public record RoomPageResponse(

        @Schema(description = "전체 방의 개수", example = "100")
        long totalElements,

        @Schema(description = "전체 페이지 개수", example = "10")
        long totalPages,

        @ArraySchema(schema = @Schema(description = "방 조회 정보", implementation = RoomFindResponse.class))
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
