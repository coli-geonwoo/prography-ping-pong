package com.prography.ping_pong.service;

import com.prography.ping_pong.service.userroom.UserRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserRoomFacadeService {

    private final UserRoomService userRoomService;

    @Transactional
    public void changeTeam(long userId, long roomId) {
        userRoomService.changeTeam(userId, roomId);
    }
}
