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
        User savedUser1 = userGenerator.generate(1L, UserStatus.ACTIVE);
        User savedUser2 = userGenerator.generate(2L, UserStatus.ACTIVE);
        Room savedRoom = roomGenerator.generate(savedUser1, RoomType.SINGLE, RoomStatus.WAIT);
        userRoomGenerator.generate(savedUser1, savedRoom, Team.RED);
        userRoomGenerator.generate(savedUser2, savedRoom, Team.BLUE);

        userRoomService.exitAllRoomUsers(savedRoom);

        List<UserRoom> allRoomUsers = userRoomRepository.findAllByRoomId(savedRoom.getId());
        assertThat(allRoomUsers).isEmpty();
    }

    @DisplayName("방의 특정 유저를 퇴장시킨다")
    @Test
    void exitRoomUser() {
        User savedUser1 = userGenerator.generate(1L, UserStatus.ACTIVE);
        User savedUser2 = userGenerator.generate(2L, UserStatus.ACTIVE);
        Room savedRoom = roomGenerator.generate(savedUser1, RoomType.SINGLE, RoomStatus.WAIT);
        UserRoom savedUserRoom1 = userRoomGenerator.generate(savedUser1, savedRoom, Team.RED);
        UserRoom savedUserRoom2 = userRoomGenerator.generate(savedUser2, savedRoom, Team.BLUE);

        userRoomService.exitRoomUser(savedUserRoom1);

        List<UserRoom> allRoomUsers = userRoomRepository.findAllByRoomId(savedRoom.getId());
        assertThat(allRoomUsers).hasSize(1);
    }

    @DisplayName("특정 유저의 팀을 변경시킨다")
    @Test
    void changeTeam() {
        User savedUser1 = userGenerator.generate(1L, UserStatus.ACTIVE);
        Room savedRoom = roomGenerator.generate(savedUser1, RoomType.SINGLE, RoomStatus.WAIT);
        Team team = Team.RED;
        userRoomGenerator.generate(savedUser1, savedRoom, team);

        userRoomService.changeTeam(savedUser1.getId(), savedRoom.getId());

        UserRoom userRoom = userRoomRepository.findByUserIdAndRoomId(savedUser1.getId(), savedRoom.getId()).get();
        assertThat(userRoom.getTeam()).isEqualTo(team.opposite());
    }

    @DisplayName("이미 방이 시작되었다면 팀을 변경할 수 없다")
    @ParameterizedTest
    @EnumSource(value = RoomStatus.class, mode = Mode.EXCLUDE, names = "WAIT")
    void canNotChangeTeamWhenRoomStatusIsNotWait(RoomStatus notWaitStatus) {
        User savedUser1 = userGenerator.generate(1L, UserStatus.ACTIVE);
        Room alreadyStartedRoom = roomGenerator.generate(savedUser1, RoomType.SINGLE, notWaitStatus);
        userRoomGenerator.generate(savedUser1, alreadyStartedRoom, Team.RED);

        assertThatThrownBy(() -> userRoomService.changeTeam(savedUser1.getId(), alreadyStartedRoom.getId()))
                .isInstanceOf(PingPongClientErrorException.class)
                .hasMessage(ResponseMessage.CLIENT_ERROR.getValue());
    }

    @DisplayName("변경하려는 팀 인원이 모두 차있다면 팀을 변경할 수 없다")
    @Test
    void canNotChangeTeamWhenOppositeTeamIsFull() {
        User savedUser1 = userGenerator.generate(1L, UserStatus.ACTIVE);
        User savedUser2 = userGenerator.generate(2L, UserStatus.ACTIVE);
        Room savedRoom = roomGenerator.generate(savedUser1, RoomType.SINGLE, RoomStatus.WAIT);
        userRoomGenerator.generate(savedUser1, savedRoom, Team.RED);
        userRoomGenerator.generate(savedUser2, savedRoom, Team.BLUE);

        assertThatThrownBy(() -> userRoomService.changeTeam(savedUser1.getId(), savedRoom.getId()))
                .isInstanceOf(PingPongClientErrorException.class)
                .hasMessage(ResponseMessage.CLIENT_ERROR.getValue());
    }

    @DisplayName("방의 인원이 모두 찼는지 확인할 수 있다")
    @Test
    void isFull() {
        User savedUser1 = userGenerator.generate(1L, UserStatus.ACTIVE);
        User savedUser2 = userGenerator.generate(2L, UserStatus.ACTIVE);
        Room savedRoom = roomGenerator.generate(savedUser1, RoomType.SINGLE, RoomStatus.WAIT);
        userRoomGenerator.generate(savedUser1, savedRoom, Team.RED);
        userRoomGenerator.generate(savedUser2, savedRoom, Team.BLUE);

        assertThat(userRoomService.isFull(savedRoom)).isTrue();
    }

    @DisplayName("방의 인원이 모두 차지 않았는지 확인할 수 있다")
    @Test
    void isNotFull() {
        User savedUser1 = userGenerator.generate(1L, UserStatus.ACTIVE);
        Room savedRoom = roomGenerator.generate(savedUser1, RoomType.SINGLE, RoomStatus.WAIT);
        userRoomGenerator.generate(savedUser1, savedRoom, Team.RED);

        assertThat(userRoomService.isFull(savedRoom)).isFalse();
    }
}
