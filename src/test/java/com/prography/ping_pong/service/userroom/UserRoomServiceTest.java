package com.prography.ping_pong.service.userroom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.prography.ping_pong.common.BaseServiceTest;
import com.prography.ping_pong.domain.room.Room;
import com.prography.ping_pong.domain.room.RoomType;
import com.prography.ping_pong.domain.user.User;
import com.prography.ping_pong.domain.user.UserStatus;
import com.prography.ping_pong.domain.userroom.Team;
import com.prography.ping_pong.domain.userroom.UserRoom;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class UserRoomServiceTest extends BaseServiceTest {

    @Autowired
    private UserRoomService userRoomService;

    @DisplayName("방의 모든 유저를 퇴장시킨다")
    @Test
    void exitAllRoomUsers() {
        User user1 = new User(1L, "name1", "email1@email.com", UserStatus.ACTIVE);
        User user2 = new User(2L, "name2", "email2@email.com", UserStatus.ACTIVE);
        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);

        Room dummy = new Room("room1", savedUser1, RoomType.SINGLE);
        Room savedRoom = roomRepository.save(dummy);

        UserRoom userRoom1 = new UserRoom(savedUser1, dummy, Team.RED);
        UserRoom userRoom2 = new UserRoom(savedUser2, dummy, Team.RED);
        UserRoom savedUserRoom1 = userRoomRepository.save(userRoom1);
        UserRoom savedUserRoom2 = userRoomRepository.save(userRoom2);

        userRoomService.exitAllRoomUsers(savedRoom.getId());

        List<UserRoom> allRoomUsers = userRoomRepository.findAllByRoomId(savedRoom.getId());
        assertThat(allRoomUsers).isEmpty();
    }

    @DisplayName("방의 특정 유저를 퇴장시킨다")
    @Test
    void exitRoomUser() {
        User user1 = new User(1L, "name1", "email1@email.com", UserStatus.ACTIVE);
        User user2 = new User(2L, "name2", "email2@email.com", UserStatus.ACTIVE);
        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);

        Room dummy = new Room("room1", savedUser1, RoomType.SINGLE);
        Room savedRoom = roomRepository.save(dummy);

        UserRoom userRoom1 = new UserRoom(savedUser1, dummy, Team.RED);
        UserRoom userRoom2 = new UserRoom(savedUser2, dummy, Team.RED);
        UserRoom savedUserRoom1 = userRoomRepository.save(userRoom1);
        UserRoom savedUserRoom2 = userRoomRepository.save(userRoom2);

        userRoomService.exitRoomUser(savedUserRoom1);

        List<UserRoom> allRoomUsers = userRoomRepository.findAllByRoomId(savedRoom.getId());
        assertThat(allRoomUsers).hasSize(1);
    }
}
