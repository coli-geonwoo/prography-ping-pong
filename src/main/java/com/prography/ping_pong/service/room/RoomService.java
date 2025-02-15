package com.prography.ping_pong.service.room;

import com.prography.ping_pong.domain.room.Room;
import com.prography.ping_pong.domain.user.User;
import com.prography.ping_pong.domain.userroom.UserRoom;
import com.prography.ping_pong.dto.request.room.RoomCreateRequest;
import com.prography.ping_pong.dto.request.room.RoomStartRequest;
import com.prography.ping_pong.dto.response.room.RoomAttendResponse;
import com.prography.ping_pong.dto.response.room.RoomCreateResponse;
import com.prography.ping_pong.dto.response.room.RoomDetailResponse;
import com.prography.ping_pong.dto.response.room.RoomPageResponse;
import com.prography.ping_pong.dto.response.room.RoomStartResponse;
import com.prography.ping_pong.exception.custom.PingPongClientErrorException;
import com.prography.ping_pong.exception.errorcode.ClientErrorCode;
import com.prography.ping_pong.repository.RoomRepository;
import com.prography.ping_pong.repository.UserRepository;
import com.prography.ping_pong.service.userroom.UserRoomService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Service
@RequiredArgsConstructor
public class RoomService {

    private static final long PING_PONG_GAME_INTERVAL_MINUTES = 1L;

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final UserRoomService userRoomService;
    private final TaskScheduler taskScheduler;
    private final PlatformTransactionManager transactionManager;

    @Transactional
    public RoomCreateResponse createRoom(RoomCreateRequest roomCreateRequest) {
        long hostId = roomCreateRequest.userId();
        User host = findUserById(hostId);
        if (!userRoomService.canParticipate(host)) {
            throw new PingPongClientErrorException(ClientErrorCode.INVALID_REQUEST);
        }

        Room room = roomCreateRequest.toRoom(host);
        Room savedRoom = roomRepository.save(room);
        userRoomService.attend(host, room);
        return new RoomCreateResponse(savedRoom);
    }

    @Transactional
    public RoomAttendResponse attendRoom(long userId, long roomId) {
        User user = findUserById(userId);
        Room room = findRoomById(roomId);
        userRoomService.attend(user, room);
        return new RoomAttendResponse(userId, roomId);
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

    @Transactional
    public void exitRoom(long userId, long roomId) {
        UserRoom userRoom = userRoomService.findByUserIdAndRoomId(userId, roomId);
        Room room = userRoom.getRoom();

        if (!room.canExit()) {
            throw new PingPongClientErrorException(ClientErrorCode.INVALID_REQUEST);
        }

        if (room.isHost(userId)) {
            room.finished();
            userRoomService.exitAllRoomUsers(room.getId());
            return;
        }
        userRoomService.exitRoomUser(userRoom);
    }

    @Transactional
    public RoomStartResponse startRoom(long userId, long roomId) {
        Room room = findRoomById(roomId);

        if (!(room.isHost(userId) && room.isWait() && userRoomService.isFull(room))) {
            throw new PingPongClientErrorException(ClientErrorCode.INVALID_REQUEST);
        }
        room.start();
        scheduleFinish(room);
        return new RoomStartResponse(room);
    }

    private void scheduleFinish(Room room) {
        Instant reserveTime = Instant.now().plus(PING_PONG_GAME_INTERVAL_MINUTES, ChronoUnit.MINUTES);
        taskScheduler.schedule(() -> updateRoomToFinish(room.getId()), reserveTime);
    }

    private void updateRoomToFinish(long roomId) {
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            Room room = findRoomById(roomId);
            room.finished();
            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status);
        }
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
