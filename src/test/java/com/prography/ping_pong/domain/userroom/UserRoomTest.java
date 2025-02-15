package com.prography.ping_pong.domain.userroom;

import static org.assertj.core.api.Assertions.assertThat;

import com.prography.ping_pong.domain.room.Room;
import com.prography.ping_pong.domain.room.RoomStatus;
import com.prography.ping_pong.domain.room.RoomType;
import com.prography.ping_pong.domain.user.User;
import com.prography.ping_pong.domain.user.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;

class UserRoomTest {

    @DisplayName("방이 WAIT 상태 + 상대 팀 인원이 만석이 아니라면 변경 가능하다")
    @Test
    void canChangeTeam() {
        User user = new User(1L, 1L, "name", "email@email.com", UserStatus.ACTIVE);
        RoomType roomType = RoomType.DOUBLE;
        Room room = new Room(1L, "title", user, roomType, RoomStatus.WAIT);
        UserRoom userRoom = new UserRoom(user, room, Team.RED);
        long oppositeTeamUserCount = roomType.getTeamCapacity() - 1;

        assertThat(userRoom.canChangeTeam(oppositeTeamUserCount)).isTrue();
    }

    @DisplayName("방이 WAIT 상태가 아니라면 팀 변경이 불가하다")
    @ParameterizedTest
    @EnumSource(value = RoomStatus.class, mode = Mode.EXCLUDE, names = "WAIT")
    void canNotChangeTeamWhenRoomIsNotWait(RoomStatus notWaitStatus) {
        User user = new User(1L, 1L, "name", "email@email.com", UserStatus.ACTIVE);
        RoomType roomType = RoomType.DOUBLE;
        Room room = new Room(1L, "title", user, roomType, notWaitStatus);
        UserRoom userRoom = new UserRoom(user, room, Team.RED);
        long oppositeTeamUserCount = roomType.getTeamCapacity() - 1;

        assertThat(userRoom.canChangeTeam(oppositeTeamUserCount)).isFalse();
    }

    @DisplayName("변경하려는 팀 인원이 모두 차있다면 변경이 불가하다")
    @Test
    void canNotChangeTeamWhenOppositeTeamIsFull() {
        User user = new User(1L, 1L, "name", "email@email.com", UserStatus.ACTIVE);
        RoomType roomType = RoomType.DOUBLE;
        Room room = new Room(1L, "title", user, roomType, RoomStatus.WAIT);
        UserRoom userRoom = new UserRoom(user, room, Team.RED);
        long oppositeTeamUserCount = roomType.getTeamCapacity();

        assertThat(userRoom.canChangeTeam(oppositeTeamUserCount)).isFalse();
    }

    @DisplayName("반대팀으로 변경할 수 있다")
    @Test
    void changeTeam() {
        User user = new User(1L, 1L, "name", "email@email.com", UserStatus.ACTIVE);
        RoomType roomType = RoomType.DOUBLE;
        Team team = Team.RED;
        Room room = new Room(1L, "title", user, roomType, RoomStatus.WAIT);
        UserRoom userRoom = new UserRoom(user, room, team);

        userRoom.changeTeam();

        assertThat(userRoom.getTeam()).isEqualTo(team.opposite());
    }
}
