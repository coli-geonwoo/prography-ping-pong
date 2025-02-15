package com.prography.ping_pong.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.prography.ping_pong.common.BaseRepositoryTest;
import com.prography.ping_pong.domain.room.Room;
import com.prography.ping_pong.domain.room.RoomStatus;
import com.prography.ping_pong.domain.room.RoomType;
import com.prography.ping_pong.domain.user.User;
import com.prography.ping_pong.domain.user.UserStatus;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

class RoomRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("페이징된 방 목록을 조회한다")
    @Test
    void findAllByOrderByIdAsc() {
        User user1 = new User(1L, "name1", "email1@email.com", UserStatus.ACTIVE);
        User user2 = new User(2L, "name2", "email2@email.com", UserStatus.ACTIVE);
        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);

        Room dummy1 = new Room("room1", savedUser1, RoomType.SINGLE);
        Room dummy2 = new Room("room2", savedUser2, RoomType.SINGLE);
        Room savedRoom1 = roomRepository.save(dummy1);
        Room savedRoom2 = roomRepository.save(dummy2);

        Pageable pageable = PageRequest.of(1, 1);

        Page<Room> roomPages = roomRepository.findAllByOrderByIdAsc(pageable);

        assertAll(
                () -> assertThat(roomPages.getTotalPages()).isEqualTo(2L),
                () -> assertThat(roomPages.getTotalElements()).isEqualTo(2L),
                () -> assertThat(roomPages.toList()).hasSize(1),
                () -> assertThat(roomPages.toList().get(0).getId()).isEqualTo(savedRoom2.getId())
        );
    }

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

    @DisplayName("방의 상태를 FINISH로 바꾼다")
    @Test
    void updateRoomStatusToFinish() {
        User user = new User(1L, "name1", "email1@email.com", UserStatus.ACTIVE);
        User savedUser = userRepository.save(user);

        Room dummy = new Room("room1", savedUser, RoomType.SINGLE);
        Room savedRoom = roomRepository.save(dummy);

        roomRepository.updateRoomStatusToFinish(savedRoom.getId());

        Room foundRoom = roomRepository.findById(savedRoom.getId()).get();
        assertThat(foundRoom.getStatus()).isEqualTo(RoomStatus.FINISH);
    }
}
