package com.prography.pingpong.service.userroom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.prography.pingpong.common.BaseServiceTest;
import com.prography.pingpong.domain.room.Room;
import com.prography.pingpong.domain.room.RoomStatus;
import com.prography.pingpong.domain.room.RoomType;
import com.prography.pingpong.domain.user.User;
import com.prography.pingpong.domain.user.UserStatus;
import com.prography.pingpong.domain.userroom.Team;
import com.prography.pingpong.domain.userroom.UserRoom;
import com.prography.pingpong.exception.custom.PingPongClientErrorException;
import com.prography.pingpong.view.ResponseMessage;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;
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

        Room dummy = new Room("room1", savedUser1.getId(), RoomType.SINGLE);
        Room savedRoom = roomRepository.save(dummy);

        UserRoom userRoom1 = new UserRoom(savedUser1, dummy, Team.RED);
        UserRoom userRoom2 = new UserRoom(savedUser2, dummy, Team.RED);
        UserRoom savedUserRoom1 = userRoomRepository.save(userRoom1);
        UserRoom savedUserRoom2 = userRoomRepository.save(userRoom2);

        userRoomService.exitAllRoomUsers(savedRoom);

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

        Room dummy = new Room("room1", savedUser1.getId(), RoomType.SINGLE);
        Room savedRoom = roomRepository.save(dummy);

        UserRoom userRoom1 = new UserRoom(savedUser1, dummy, Team.RED);
        UserRoom userRoom2 = new UserRoom(savedUser2, dummy, Team.RED);
        UserRoom savedUserRoom1 = userRoomRepository.save(userRoom1);
        UserRoom savedUserRoom2 = userRoomRepository.save(userRoom2);

        userRoomService.exitRoomUser(savedUserRoom1);

        List<UserRoom> allRoomUsers = userRoomRepository.findAllByRoomId(savedRoom.getId());
        assertThat(allRoomUsers).hasSize(1);
    }

    @DisplayName("특정 유저의 팀을 변경시킨다")
    @Test
    void changeTeam() {
        User user1 = new User(1L, "name1", "email1@email.com", UserStatus.ACTIVE);
        User savedUser1 = userRepository.save(user1);

        Room dummy = new Room("room1", savedUser1.getId(), RoomType.SINGLE);
        Room savedRoom = roomRepository.save(dummy);

        Team team = Team.RED;
        UserRoom userRoom1 = new UserRoom(savedUser1, dummy, team);
        UserRoom savedUserRoom1 = userRoomRepository.save(userRoom1);

        userRoomService.changeTeam(savedUser1.getId(), savedRoom.getId());

        UserRoom userRoom = userRoomRepository.findByUserIdAndRoomId(savedUser1.getId(), savedRoom.getId()).get();
        assertThat(userRoom.getTeam()).isEqualTo(team.opposite());
    }

    @DisplayName("이미 방이 시작되었다면 팀을 변경할 수 없다")
    @ParameterizedTest
    @EnumSource(value = RoomStatus.class, mode = Mode.EXCLUDE, names = "WAIT")
    void canNotChangeTeamWhenRoomStatusIsNotWait(RoomStatus notWaitStatus) {
        User user1 = new User(1L, "name1", "email1@email.com", UserStatus.ACTIVE);
        User savedUser1 = userRepository.save(user1);

        Room dummy = new Room(null, "room1", savedUser1.getId(), RoomType.SINGLE, notWaitStatus);
        Room savedRoom = roomRepository.save(dummy);

        UserRoom userRoom1 = new UserRoom(savedUser1, dummy, Team.RED);
        UserRoom savedUserRoom1 = userRoomRepository.save(userRoom1);

        assertThatThrownBy(() -> userRoomService.changeTeam(savedUser1.getId(), savedRoom.getId()))
                .isInstanceOf(PingPongClientErrorException.class)
                .hasMessage(ResponseMessage.CLIENT_ERROR.getValue());
    }

    @DisplayName("변경하려는 팀 인원이 모두 차있다면 팀을 변경할 수 없다")
    @Test
    void canNotChangeTeamWhenOppositeTeamIsFull() {
        User user1 = new User(1L, "name1", "email1@email.com", UserStatus.ACTIVE);
        User user2 = new User(2L, "name2", "email2@email.com", UserStatus.ACTIVE);
        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);

        Room dummy = new Room("room1", savedUser1.getId(), RoomType.SINGLE);
        Room savedRoom = roomRepository.save(dummy);

        UserRoom userRoom1 = new UserRoom(savedUser1, dummy, Team.RED);
        UserRoom userRoom2 = new UserRoom(savedUser2, dummy, Team.BLUE);
        UserRoom savedUserRoom1 = userRoomRepository.save(userRoom1);
        UserRoom savedUserRoom2 = userRoomRepository.save(userRoom2);

        assertThatThrownBy(() -> userRoomService.changeTeam(savedUser1.getId(), savedRoom.getId()))
                .isInstanceOf(PingPongClientErrorException.class)
                .hasMessage(ResponseMessage.CLIENT_ERROR.getValue());
    }

    @DisplayName("방의 인원이 모두 찼는지 확인할 수 있다")
    @Test
    void isFull() {
        User user1 = new User(1L, "name1", "email1@email.com", UserStatus.ACTIVE);
        User user2 = new User(2L, "name2", "email2@email.com", UserStatus.ACTIVE);
        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);

        Room dummy = new Room("room1", savedUser1.getId(), RoomType.SINGLE);
        Room savedRoom = roomRepository.save(dummy);

        UserRoom userRoom1 = new UserRoom(savedUser1, dummy, Team.RED);
        UserRoom userRoom2 = new UserRoom(savedUser2, dummy, Team.BLUE);
        UserRoom savedUserRoom1 = userRoomRepository.save(userRoom1);
        UserRoom savedUserRoom2 = userRoomRepository.save(userRoom2);

        assertThat(userRoomService.isFull(savedRoom)).isTrue();
    }

    @DisplayName("방의 인원이 모두 차지 않았는지 확인할 수 있다")
    @Test
    void isNotFull() {
        User user1 = new User(1L, "name1", "email1@email.com", UserStatus.ACTIVE);
        User savedUser1 = userRepository.save(user1);

        Room dummy = new Room("room1", savedUser1.getId(), RoomType.SINGLE);
        Room savedRoom = roomRepository.save(dummy);

        UserRoom userRoom1 = new UserRoom(savedUser1, dummy, Team.RED);
        UserRoom savedUserRoom1 = userRoomRepository.save(userRoom1);

        assertThat(userRoomService.isFull(savedRoom)).isFalse();
    }
}
