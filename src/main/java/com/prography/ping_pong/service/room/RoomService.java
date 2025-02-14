package com.prography.ping_pong.service.room;

import com.prography.ping_pong.domain.room.Room;
import com.prography.ping_pong.dto.response.room.RoomDetailResponse;
import com.prography.ping_pong.dto.response.room.RoomPageResponse;
import com.prography.ping_pong.exception.custom.PingPongClientErrorException;
import com.prography.ping_pong.exception.errorcode.ClientErrorCode;
import com.prography.ping_pong.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomPageResponse findAll(Pageable pageable) {
        Page<Room> roomPage = roomRepository.findAllByOrderByIdAsc(pageable);
        return new RoomPageResponse(roomPage);
    }

    public RoomDetailResponse findRoom(long roomId) {
        Room room = findById(roomId);
        return new RoomDetailResponse(room);
    }

    private Room findById(long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new PingPongClientErrorException(ClientErrorCode.INVALID_REQUEST));
    }
}
