package com.prography.ping_pong.service.room;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.prography.ping_pong.common.BaseServiceTest;
import com.prography.ping_pong.domain.room.Room;
import com.prography.ping_pong.domain.room.RoomType;
import com.prography.ping_pong.domain.user.User;
import com.prography.ping_pong.domain.user.UserStatus;
import com.prography.ping_pong.dto.response.room.RoomDetailResponse;
import com.prography.ping_pong.repository.RoomRepository;
import com.prography.ping_pong.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class RoomServiceTest extends BaseServiceTest {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomService roomService;

    @DisplayName("아이디로 방을 찾는다")
    @Test
    void findRoom() {
        User user = new User(1L, "name1", "email1@email.com", UserStatus.ACTIVE);
        User savedUser = userRepository.save(user);

        Room dummy = new Room("room1", savedUser, RoomType.SINGLE);
        Room savedRoom = roomRepository.save(dummy);

        RoomDetailResponse roomDetailResponse = roomService.findRoom(savedRoom.getId());

        assertAll(
                () -> assertThat(roomDetailResponse.id()).isEqualTo(savedRoom.getId()),
                () -> assertThat(roomDetailResponse.roomType()).isEqualTo(savedRoom.getRoomType()),
                () -> assertThat(roomDetailResponse.hostId()).isEqualTo(savedRoom.getHost().getId()),
                () -> assertThat(roomDetailResponse.title()).isEqualTo(savedRoom.getTitle()),
                () -> assertThat(roomDetailResponse.status()).isEqualTo(savedRoom.getStatus()),
                () -> assertThat(roomDetailResponse.createdAt()).isEqualTo(savedRoom.getCreatedAt()),
                () -> assertThat(roomDetailResponse.updatedAt()).isEqualTo(savedRoom.getUpdatedAt())
        );
    }
}
