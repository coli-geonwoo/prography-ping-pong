package com.prography.pingpong.controller;

import com.prography.pingpong.controller.swagger.UserRoomControllerSwagger;
import com.prography.pingpong.dto.request.room.TeamChangeRequest;
import com.prography.pingpong.dto.response.ApiResponse;
import com.prography.pingpong.service.userroom.UserRoomFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserRoomController implements UserRoomControllerSwagger {

    private final UserRoomFacadeService userRoomFacadeService;

    @PutMapping("/team/{roomId}")
    public ApiResponse changeTeam(
            @RequestBody TeamChangeRequest request,
            @PathVariable(name = "roomId") long roomId
    ) {
        return userRoomFacadeService.changeTeam(request.userId(), roomId);
    }
}
