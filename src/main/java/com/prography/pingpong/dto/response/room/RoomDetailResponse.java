package com.prography.pingpong.dto.response.room;

import com.prography.pingpong.domain.room.Room;
import com.prography.pingpong.domain.room.RoomStatus;
import com.prography.pingpong.domain.room.RoomType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record RoomDetailResponse(
        @Schema(description = "방 아이디", example = "1")
        long id,

        @Schema(description = "방 제목", example = "단식하실 분")
        @NotBlank
        String title,

        @Schema(description = "방장 아이디", example = "1")
        long hostId,

        @NotNull
        @Schema(description = "방 타입", example = "SINGLE")
        RoomType roomType,

        @NotNull
        @Schema(description = "방 상태", example = "WAIT")
        RoomStatus status,

        @NotNull
        @Schema(description = "방 생성시기", example = "2024-01-14 02:22:45")
        LocalDateTime createdAt,

        @NotNull
        @Schema(description = "방 정보 수정시기", example = "2024-01-14 02:22:45")
        LocalDateTime updatedAt
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
