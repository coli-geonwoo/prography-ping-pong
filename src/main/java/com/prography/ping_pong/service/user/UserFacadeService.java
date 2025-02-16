package com.prography.ping_pong.service.user;

import com.prography.ping_pong.client.FakerUserDetail;
import com.prography.ping_pong.client.FakerUserDetails;
import com.prography.ping_pong.client.UserInitializeClient;
import com.prography.ping_pong.domain.user.User;
import com.prography.ping_pong.dto.request.user.FakerRequest;
import com.prography.ping_pong.dto.request.user.UserInitializeRequest;
import com.prography.ping_pong.dto.response.ApiResponse;
import com.prography.ping_pong.dto.response.user.UserPageResponse;
import com.prography.ping_pong.service.room.RoomService;
import com.prography.ping_pong.service.userroom.UserRoomService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserFacadeService {

    private final UserService userService;
    private final RoomService roomService;
    private final UserRoomService userRoomService;

    @Transactional
    public ApiResponse initialize(UserInitializeRequest userInitializeRequest) {
        truncateAllData();
        userService.initialize(userInitializeRequest.seed(), userInitializeRequest.quantity());
        return ApiResponse.ok();
    }

    private void truncateAllData() {
        userRoomService.deleteAllUserRooms();
        roomService.deleteAllRooms();
        userService.deleteAllUsers();
    }

    @Transactional(readOnly = true)
    public UserPageResponse findAll(Pageable pageable) {
        Page<User> foundUsers = userService.findAll(pageable);
        return new UserPageResponse(foundUsers);
    }
}
