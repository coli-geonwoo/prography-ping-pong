package com.prography.ping_pong.controller;

import com.prography.ping_pong.dto.request.room.TeamChangeRequest;
import com.prography.ping_pong.dto.response.ApiResponse;
import com.prography.ping_pong.service.userroom.UserRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserRoomController {

    private final UserRoomService userRoomService;

    @PutMapping("/team/{roomId}")
    public ResponseEntity<ApiResponse> changeTeam(
            @RequestBody TeamChangeRequest request,
            @PathVariable(name = "roomId") long roomId
    ) {
        userRoomService.changeTeam(request.userId(), roomId);
        ApiResponse response = ApiResponse.ok();
        return ResponseEntity.ok(response);
    }
}
