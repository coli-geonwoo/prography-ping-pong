package com.prography.pingpong.service.room;

import com.prography.pingpong.domain.room.Room;
import com.prography.pingpong.exception.custom.PingPongClientErrorException;
import com.prography.pingpong.exception.errorcode.ClientErrorCode;
import com.prography.pingpong.repository.RoomRepository;
import com.prography.pingpong.util.TransactionRunner;
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
        return roomRepository.save(room);
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
