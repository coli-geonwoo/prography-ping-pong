package com.prography.ping_pong.domain.room;

import static org.assertj.core.api.Assertions.assertThat;

import com.prography.ping_pong.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mockito;

class RoomTest {

    @DisplayName("방 생성 시 초기 상태는 WAIT가 된다")
    @Test
    void initializeWithWait() {
        User mockUser = Mockito.mock(User.class);

        Room room = new Room("title", mockUser, RoomType.SINGLE);

        assertThat(room.getStatus()).isEqualTo(RoomStatus.WAIT);
    }

    @DisplayName("방 상태가 WAIT이고 방이 인원 미달이라면 참여가능하다")
    @ParameterizedTest
    @EnumSource(RoomType.class)
    void isAttendAble(RoomType roomType) {
        long participantCount = roomType.getTotalCapacity() - 1;
        User mockUser = Mockito.mock(User.class);
        Room room = new Room("title", mockUser, roomType);

        assertThat(room.isAttendAble(participantCount)).isTrue();
    }

    @DisplayName("방 상태가 WAIT이고 방이 모두 찼다면 참여가 불가능하다")
    @ParameterizedTest
    @EnumSource(RoomType.class)
    void isNotAttendAbleWhenRoomIsFull(RoomType roomType) {
        long participantCount = roomType.getTotalCapacity();
        User mockUser = Mockito.mock(User.class);
        Room room = new Room("title", mockUser, roomType);

        assertThat(room.isAttendAble(participantCount)).isFalse();
    }
}
