package com.prography.ping_pong.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.prography.ping_pong.common.BaseRepositoryTest;
import com.prography.ping_pong.domain.room.Room;
import com.prography.ping_pong.domain.room.RoomType;
import com.prography.ping_pong.domain.user.User;
import com.prography.ping_pong.domain.user.UserStatus;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class UserRoomRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("모든 방 데이터를 배치쿼리로 삭제한다.")
    @Test
    void deleteAllWithFlush() {
        User user = new User(1L, "name1", "email1@email.com", UserStatus.ACTIVE);
        User savedUser = userRepository.save(user);

        Room dummy = new Room("room1", savedUser, RoomType.SINGLE);
        Room savedRoom = roomRepository.save(dummy);

        roomRepository.deleteAllWithFlush(List.of(savedRoom));

        long count = roomRepository.count();
        assertThat(count).isZero();
    }
}
