package com.prography.pingpong.service.room;

import com.prography.pingpong.domain.room.Room;
import com.prography.pingpong.domain.user.User;
import com.prography.pingpong.domain.userroom.UserRoom;
import com.prography.pingpong.dto.request.room.RoomCreateRequest;
import com.prography.pingpong.dto.response.room.RoomAttendResponse;
import com.prography.pingpong.dto.response.room.RoomCreateResponse;
import com.prography.pingpong.dto.response.room.RoomDetailResponse;
import com.prography.pingpong.dto.response.room.RoomPageResponse;
import com.prography.pingpong.dto.response.room.RoomStartResponse;
import com.prography.pingpong.exception.custom.PingPongClientErrorException;
import com.prography.pingpong.exception.errorcode.ClientErrorCode;
import com.prography.pingpong.service.user.UserService;
import com.prography.pingpong.service.userroom.UserRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoomFacadeService {

    private static final long PING_PONG_GAME_INTERVAL_MINUTES = 1L;

    private final UserService userService;
    private final RoomService roomService;
    private final UserRoomService userRoomService;

    @Transactional
    public RoomCreateResponse createRoom(RoomCreateRequest roomCreateRequest) {
        long hostId = roomCreateRequest.userId();
        User host = userService.findUser(hostId);
        Room room = roomCreateRequest.toRoom();
        Room savedRoom = roomService.createRoom(room);
        userRoomService.attend(host, room);
        return new RoomCreateResponse(savedRoom);
    }

    @Transactional
    public RoomAttendResponse attendRoom(long userId, long roomId) {
        User user = userService.findUser(userId);
        Room room = roomService.findRoom(roomId);
        userRoomService.attend(user, room);
        return new RoomAttendResponse(userId, roomId);
    }

    @Transactional(readOnly = true)
    public RoomPageResponse findAllRoom(Pageable pageable) {
        Page<Room> roomPage = roomService.findAll(pageable);
        return new RoomPageResponse(roomPage);
    }

    @Transactional(readOnly = true)
    public RoomDetailResponse findRoom(long roomId) {
        Room room = roomService.findRoom(roomId);
        return new RoomDetailResponse(room);
    }

    @Transactional
    public void exitRoom(long userId, long roomId) {
        UserRoom userRoom = userRoomService.findByUserIdAndRoomId(userId, roomId);
        Room room = userRoom.getRoom();

        if (room.isHost(userId)) {
            userRoomService.exitAllRoomUsers(room);
            return;
        }
        userRoomService.exitRoomUser(userRoom);
    }

    @Transactional
    public RoomStartResponse startRoom(long userId, long roomId) {
        Room room = roomService.findRoom(roomId);

        if (!(room.isHost(userId) && room.isWait() && userRoomService.isFull(room))) {
            throw new PingPongClientErrorException(ClientErrorCode.INVALID_REQUEST);
        }

        roomService.startRoom(room, PING_PONG_GAME_INTERVAL_MINUTES);
        return new RoomStartResponse(room);
    }
}
