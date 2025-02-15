package com.prography.ping_pong.service.room;

import com.prography.ping_pong.domain.room.Room;
import com.prography.ping_pong.domain.user.User;
import com.prography.ping_pong.dto.request.room.RoomCreateRequest;
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

    @Transactional
    public RoomCreateResponse createRoom(RoomCreateRequest roomCreateRequest) {
        long userId = roomCreateRequest.userId();
        User user = findUserById(userId);
        if (!canCreate(user)) {
            throw new PingPongClientErrorException(ClientErrorCode.INVALID_REQUEST);
        }

        Room room = roomCreateRequest.toRoom(user);
        Room savedRoom = roomRepository.save(room);
        return new RoomCreateResponse(savedRoom);
    }

    private boolean canCreate(User user) {
        boolean alreadyAttended = userRoomRepository.existsByUserId(user.getId());
        return user.isActive() && !alreadyAttended;
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
