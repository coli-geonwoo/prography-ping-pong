package com.prography.ping_pong.service.room;

import com.prography.ping_pong.domain.room.Room;
import com.prography.ping_pong.domain.user.User;
import com.prography.ping_pong.exception.custom.PingPongClientErrorException;
import com.prography.ping_pong.exception.errorcode.ClientErrorCode;
import com.prography.ping_pong.repository.RoomRepository;
import com.prography.ping_pong.util.TransactionRunner;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final TaskScheduler taskScheduler;
    private final TransactionRunner transactionRunner;

    @Transactional
    public Room createRoom(Room room) {
        if(isAlreadyCreated(room.getHost())){
            throw new PingPongClientErrorException(ClientErrorCode.INVALID_REQUEST);
        }
        return roomRepository.save(room);
    }

    private boolean isAlreadyCreated(User host){
        return roomRepository.existsByHost(host);
    }

    @Transactional(readOnly = true)
    public Room findRoom(long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new PingPongClientErrorException(ClientErrorCode.INVALID_REQUEST));
    }

    @Transactional(readOnly = true)
    public Page<Room> findAll(Pageable pageable) {
        return roomRepository.findAllByOrderByIdAsc(pageable);
    }

    public void startRoom(Room room, long gameDurationMinutes) {
        room.start();
        scheduleFinish(room, gameDurationMinutes);
    }

    private void scheduleFinish(Room room, long gameDurationMinutes) {
        Instant reserveTime = Instant.now().plus(gameDurationMinutes, ChronoUnit.MINUTES);
        taskScheduler.schedule(() -> transactionRunner.runWithTransaction(
                () -> updateRoomToFinish(room.getId())
        ), reserveTime);
    }

    private void updateRoomToFinish(long roomId) {
        Room room = findRoom(roomId);
        room.finished();
    }

    @Transactional
    public void deleteAllRooms() {
        List<Room> allRooms = roomRepository.findAll();
        roomRepository.deleteAllWithFlush(allRooms);
    }
}
