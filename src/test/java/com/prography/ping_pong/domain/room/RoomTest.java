package com.prography.ping_pong.domain.room;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.prography.ping_pong.domain.user.User;
import com.prography.ping_pong.domain.user.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class RoomTest {

    @DisplayName("방 생성 시 초기 상태는 WAIT가 된다")
    @Test
    void initializeWithWait() {
        User host = new User(1L, 1L, "name", "email@email.com", UserStatus.ACTIVE);

        Room room = new Room("title", host, RoomType.SINGLE);

        assertThat(room.getStatus()).isEqualTo(RoomStatus.WAIT);
    }

    @DisplayName("방의 상태가 WAIT이 아니라면 참여가 불가하다")
    @ParameterizedTest
    @EnumSource(value = RoomStatus.class, mode = EnumSource.Mode.EXCLUDE, names = "WAIT")
    void canNotAttendAbleWithoutWait(RoomStatus notWaitStatus) {
        User host = new User(1L, 1L, "name", "email@email.com", UserStatus.ACTIVE);
        RoomType roomType = RoomType.SINGLE;
        long participantCount = roomType.getTotalCapacity() - 1;
        Room room = new Room(1L, "title", host, roomType, notWaitStatus);

        assertThat(room.isAttendAble(participantCount)).isFalse();
    }

    @DisplayName("방의 인원이 미달이라면 참여가능하다")
    @ParameterizedTest
    @EnumSource(RoomType.class)
    void isAttendAble(RoomType roomType) {
        long participantCount = roomType.getTotalCapacity() - 1;
        User host = new User(1L, 1L, "name", "email@email.com", UserStatus.ACTIVE);
        Room room = new Room("title", host, roomType);

        assertThat(room.isAttendAble(participantCount)).isTrue();
    }

    @DisplayName("방이 모두 찼다면 참여가 불가능하다")
    @ParameterizedTest
    @EnumSource(RoomType.class)
    void isNotAttendAbleWhenRoomIsFull(RoomType roomType) {
        long participantCount = roomType.getTotalCapacity();
        User host = new User(1L, 1L, "name", "email@email.com", UserStatus.ACTIVE);
        Room room = new Room("title", host, roomType);

        assertThat(room.isAttendAble(participantCount)).isFalse();
    }

    @DisplayName("방 상태를 FINISH로 변경한다")
    @Test
    void finishRoom() {
        User host = new User(1L, 1L, "name", "email@email.com", UserStatus.ACTIVE);
        Room room = new Room("title", host, RoomType.SINGLE);
        room.finished();

        assertThat(room.getStatus()).isEqualTo(RoomStatus.FINISH);
    }

    @DisplayName("방의 상태가 WAIT이면 나갈 수 있다")
    @Test
    void canExitWithWait() {
        User host = new User(1L, 1L, "name", "email@email.com", UserStatus.ACTIVE);
        Room room = new Room("title", host, RoomType.SINGLE);

        assertThat(room.canExit()).isTrue();
    }

    @DisplayName("방의 상태가 WAIT이 아니라면 나갈 수 없다")
    @ParameterizedTest
    @EnumSource(value = RoomStatus.class, mode = EnumSource.Mode.EXCLUDE, names = "WAIT")
    void canNotExitWithoutWait(RoomStatus notWaitStatus) {
        User host = new User(1L, 1L, "name", "email@email.com", UserStatus.ACTIVE);
        Room room = new Room(1L, "title", host, RoomType.SINGLE, notWaitStatus);

        assertThat(room.canExit()).isFalse();
    }

    @DisplayName("방장을 판별할 수 있다")
    @Test
    void isHost() {
        User host = new User(1L, 1L, "name", "email@email.com", UserStatus.ACTIVE);
        long hostId = host.getId();
        long notHostId = hostId + 1;
        Room room = new Room("title", host, RoomType.SINGLE);

        assertAll(
                () -> assertThat(room.isHost(hostId)).isTrue(),
                () -> assertThat(room.isHost(notHostId)).isFalse()
        );
    }

    @DisplayName("방이 모두 찼는지 판별할 수 있다")
    @ParameterizedTest
    @EnumSource(RoomType.class)
    void isFull(RoomType roomType) {
        User host = new User(1L, 1L, "name", "email@email.com", UserStatus.ACTIVE);
        Room room = new Room("title", host, roomType);
        long firstTeamCount = roomType.getTeamCapacity();
        long secondTeamCount = roomType.getTeamCapacity();

        assertThat(room.isFull(firstTeamCount, secondTeamCount)).isTrue();
    }

    @DisplayName("방이 차지 않았는지 판별할 수 있다")
    @ParameterizedTest
    @EnumSource(RoomType.class)
    void isNotFull(RoomType roomType) {
        User host = new User(1L, 1L, "name", "email@email.com", UserStatus.ACTIVE);
        Room room = new Room("title", host, roomType);
        long firstTeamCount = roomType.getTeamCapacity();
        long secondTeamCount = roomType.getTeamCapacity() - 1;

        assertThat(room.isFull(firstTeamCount, secondTeamCount)).isFalse();
    }
}
