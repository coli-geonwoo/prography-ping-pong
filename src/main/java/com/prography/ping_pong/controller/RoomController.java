package com.prography.ping_pong.controller;

import com.prography.ping_pong.dto.request.room.RoomAttendRequest;
import com.prography.ping_pong.dto.request.room.RoomCreateRequest;
import com.prography.ping_pong.dto.response.ApiBodyResponse;
import com.prography.ping_pong.dto.response.ApiResponse;
import com.prography.ping_pong.dto.response.room.RoomDetailResponse;
import com.prography.ping_pong.dto.response.room.RoomPageResponse;
import com.prography.ping_pong.service.room.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping("/room")
    public ResponseEntity<ApiResponse> createRoom(@RequestBody RoomCreateRequest request) {
        roomService.createRoom(request);
        ApiResponse response = ApiResponse.ok();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/room/attention/{roomId}")
    public ResponseEntity<ApiResponse> attendRoom(
            @PathVariable(name = "roomId") long roomId,
            @RequestBody RoomAttendRequest request
    ) {
        roomService.attendRoom(request.userId(), roomId);
        ApiResponse response = ApiResponse.ok();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<ApiBodyResponse<RoomDetailResponse>> findRoom(@PathVariable(name = "roomId") long roomId) {
        RoomDetailResponse room = roomService.findRoom(roomId);
        ApiBodyResponse<RoomDetailResponse> response = ApiBodyResponse.ok(room);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/room")
    public ResponseEntity<ApiBodyResponse<RoomPageResponse>> findAllRooms(
            @RequestParam(name = "size") int size,
            @RequestParam(name = "page") int page
    ) {
        Pageable pageable = PageRequest.of(page, size);
        RoomPageResponse roomPageResponse = roomService.findAll(pageable);
        ApiBodyResponse<RoomPageResponse> response = ApiBodyResponse.ok(roomPageResponse);
        return ResponseEntity.ok(response);
    }
}
