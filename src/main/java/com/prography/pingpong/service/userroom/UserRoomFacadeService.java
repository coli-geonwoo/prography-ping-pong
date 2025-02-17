package com.prography.pingpong.service.userroom;

import com.prography.pingpong.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserRoomFacadeService {

    private final UserRoomService userRoomService;

    @Transactional
    public ApiResponse changeTeam(long userId, long roomId) {
        userRoomService.changeTeam(userId, roomId);
        return ApiResponse.ok();
    }
}
