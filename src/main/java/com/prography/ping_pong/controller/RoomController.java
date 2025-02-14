package com.prography.ping_pong.controller;

import com.prography.ping_pong.dto.response.ApiBodyResponse;
import com.prography.ping_pong.dto.response.room.RoomDetailResponse;
import com.prography.ping_pong.service.room.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping("/room/{roomId}")
    public ResponseEntity<ApiBodyResponse<RoomDetailResponse>> findRoom(@PathVariable(name = "roomId") long roomId) {
        RoomDetailResponse room = roomService.findRoom(roomId);
        ApiBodyResponse<RoomDetailResponse> response = ApiBodyResponse.ok(room);
        return ResponseEntity.ok(response);
    }
}
