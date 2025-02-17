package com.prography.pingpong.controller;

import com.prography.pingpong.controller.swagger.RoomControllerSwagger;
import com.prography.pingpong.dto.request.room.RoomAttendRequest;
import com.prography.pingpong.dto.request.room.RoomCreateRequest;
import com.prography.pingpong.dto.request.room.RoomExitRequest;
import com.prography.pingpong.dto.request.room.RoomStartRequest;
import com.prography.pingpong.dto.response.ApiBodyResponse;
import com.prography.pingpong.dto.response.ApiResponse;
import com.prography.pingpong.dto.response.room.RoomDetailResponse;
import com.prography.pingpong.dto.response.room.RoomPageResponse;
import com.prography.pingpong.service.room.RoomFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RoomController implements RoomControllerSwagger {

    private final RoomFacadeService roomFacadeService;

    @PostMapping("/room")
    public ApiResponse createRoom(@RequestBody RoomCreateRequest request) {
        roomFacadeService.createRoom(request);
        return ApiResponse.ok();
    }

    @PostMapping("/room/attention/{roomId}")
    public ApiResponse attendRoom(
            @PathVariable(name = "roomId") long roomId,
            @RequestBody RoomAttendRequest request
    ) {
        roomFacadeService.attendRoom(request.userId(), roomId);
        return ApiResponse.ok();
    }

    @GetMapping("/room/{roomId}")
    public ApiBodyResponse<RoomDetailResponse> findRoom(@PathVariable(name = "roomId") long roomId) {
        RoomDetailResponse room = roomFacadeService.findRoom(roomId);
        return ApiBodyResponse.ok(room);
    }

    @GetMapping("/room")
    public ApiBodyResponse<RoomPageResponse> findAllRooms(
            @RequestParam(name = "size") int size,
            @RequestParam(name = "page") int page
    ) {
        Pageable pageable = PageRequest.of(page, size);
        RoomPageResponse roomPageResponse = roomFacadeService.findAllRoom(pageable);
        return ApiBodyResponse.ok(roomPageResponse);
    }

    @PutMapping("/room/start/{roomId}")
    public ApiResponse startRoom(
            @RequestBody RoomStartRequest request,
            @PathVariable(name = "roomId") long roomId
    ) {
        roomFacadeService.startRoom(request.userId(), roomId);
        return ApiResponse.ok();
    }

    @PostMapping("/room/out/{roomId}")
    public ApiResponse exitRoom(
            @RequestBody RoomExitRequest request,
            @PathVariable(name = "roomId") long roomId
    ) {
        roomFacadeService.exitRoom(request.userId(), roomId);
        return ApiResponse.ok();
    }
}
