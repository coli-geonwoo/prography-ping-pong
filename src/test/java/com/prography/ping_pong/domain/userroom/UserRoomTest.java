package com.prography.ping_pong.domain.userroom;

import static org.assertj.core.api.Assertions.assertThat;

import com.prography.ping_pong.domain.room.Room;
import com.prography.ping_pong.domain.room.RoomStatus;
import com.prography.ping_pong.domain.room.RoomType;
import com.prography.ping_pong.domain.user.User;
import com.prography.ping_pong.domain.user.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserRoomTest {

    @DisplayName("반대팀으로 변경할 수 있다")
    @Test
    void changeTeam() {
        User user = new User(1L, 1L, "name", "email@email.com", UserStatus.ACTIVE);
        RoomType roomType = RoomType.DOUBLE;
        Team team = Team.RED;
        Room room = new Room(1L, "title", user.getId(), roomType, RoomStatus.WAIT);
        UserRoom userRoom = new UserRoom(user, room, team);

        userRoom.changeTeam();

        assertThat(userRoom.getTeam()).isEqualTo(team.opposite());
    }
}
