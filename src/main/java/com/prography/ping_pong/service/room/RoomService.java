package com.prography.ping_pong.service.room;

import com.prography.ping_pong.domain.Team;
import com.prography.ping_pong.domain.UserRoom;
import com.prography.ping_pong.domain.room.Room;
import com.prography.ping_pong.domain.user.User;
import com.prography.ping_pong.dto.request.room.RoomCreateRequest;
import com.prography.ping_pong.dto.response.room.RoomAttendResponse;
import com.prography.ping_pong.dto.response.room.RoomCreateResponse;
import com.prography.ping_pong.dto.response.room.RoomDetailResponse;
import com.prography.ping_pong.dto.response.room.RoomPageResponse;
import com.prography.ping_pong.exception.custom.PingPongClientErrorException;
import com.prography.ping_pong.exception.errorcode.ClientErrorCode;
import com.prography.ping_pong.repository.RoomRepository;
import com.prography.ping_pong.repository.UserRepository;
import com.prography.ping_pong.repository.UserRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final UserRoomRepository userRoomRepository;
    private final TeamOrganizer teamOrganizer;

    @Transactional
    public RoomCreateResponse createRoom(RoomCreateRequest roomCreateRequest) {
        long hostId = roomCreateRequest.userId();
        User host = findUserById(hostId);
        if (!canParticipate(host)) {
            throw new PingPongClientErrorException(ClientErrorCode.INVALID_REQUEST);
        }

        Room room = roomCreateRequest.toRoom(host);
        Room savedRoom = roomRepository.save(room);
        attend(host, room);
        return new RoomCreateResponse(savedRoom);
    }

    @Transactional
    public RoomAttendResponse attendRoom(long userId, long roomId) {
        User user = findUserById(userId);
        Room room = findRoomById(roomId);
        attend(user, room);
        return new RoomAttendResponse(userId, roomId);
    }

    private void attend(User user, Room room) {
        long participateCount = userRoomRepository.countByRoomId(room.getId());
        if (!(canParticipate(user) && room.isAttendAble(participateCount))) {
            throw new PingPongClientErrorException(ClientErrorCode.INVALID_REQUEST);
        }

        Team team = teamOrganizer.organize(room.getRoomType(), participateCount);
        UserRoom userRoom = new UserRoom(user, room, team);
        userRoomRepository.save(userRoom);
    }

    private boolean canParticipate(User user) {
        boolean alreadyParticipated = userRoomRepository.existsByUserId(user.getId());
        return user.isActive() && !alreadyParticipated;
    }

    @Transactional(readOnly = true)
    public RoomPageResponse findAll(Pageable pageable) {
        Page<Room> roomPage = roomRepository.findAllByOrderByIdAsc(pageable);
        return new RoomPageResponse(roomPage);
    }

    @Transactional(readOnly = true)
    public RoomDetailResponse findRoom(long roomId) {
        Room room = findRoomById(roomId);
        return new RoomDetailResponse(room);
    }

    private Room findRoomById(long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new PingPongClientErrorException(ClientErrorCode.INVALID_REQUEST));
    }

    private User findUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new PingPongClientErrorException(ClientErrorCode.INVALID_REQUEST));
    }
}
