package com.prography.pingpong.common.fixture;

import com.prography.pingpong.domain.room.Room;
import com.prography.pingpong.domain.room.RoomStatus;
import com.prography.pingpong.domain.room.RoomType;
import com.prography.pingpong.domain.user.User;
import com.prography.pingpong.repository.RoomRepository;
import org.springframework.stereotype.Component;

@Component
public class RoomGenerator {

    private final RoomRepository roomRepository;

    public RoomGenerator(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public Room generate(User host, RoomType type, RoomStatus status) {
        Room room = new Room(null, "title", host.getId(), type, status);
        return roomRepository.save(room);
    }
}
