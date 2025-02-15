package com.prography.ping_pong.service.room;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.prography.ping_pong.common.BaseServiceTest;
import com.prography.ping_pong.domain.Team;
import com.prography.ping_pong.domain.UserRoom;
import com.prography.ping_pong.domain.room.Room;
import com.prography.ping_pong.domain.room.RoomType;
import com.prography.ping_pong.domain.user.User;
import com.prography.ping_pong.domain.user.UserStatus;
import com.prography.ping_pong.dto.request.room.RoomCreateRequest;
import com.prography.ping_pong.dto.response.room.RoomDetailResponse;
import com.prography.ping_pong.dto.response.room.RoomPageResponse;
import com.prography.ping_pong.exception.custom.PingPongClientErrorException;
import com.prography.ping_pong.view.ResponseMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

class RoomServiceTest extends BaseServiceTest {

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
                () -> assertThat(roomDetailResponse.status()).isEqualTo(savedRoom.getStatus())
        );
    }

    @DisplayName("페이징 된 전체 방을 조회한다")
    @Test
    void findAllRooms() {
        User user1 = new User(1L, "name1", "email1@email.com", UserStatus.ACTIVE);
        User user2 = new User(2L, "name2", "email2@email.com", UserStatus.ACTIVE);
        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);

        Room dummy1 = new Room("room1", savedUser1, RoomType.SINGLE);
        Room dummy2 = new Room("room2", savedUser2, RoomType.SINGLE);
        Room savedRoom1 = roomRepository.save(dummy1);
        Room savedRoom2 = roomRepository.save(dummy2);

        Pageable pageable = PageRequest.of(1, 1);

        RoomPageResponse roomPageResponse = roomService.findAll(pageable);

        assertAll(
                () -> assertThat(roomPageResponse.totalPages()).isEqualTo(2L),
                () -> assertThat(roomPageResponse.totalElements()).isEqualTo(2L),
                () -> assertThat(roomPageResponse.roomList()).hasSize(1),
                () -> assertThat(roomPageResponse.roomList().get(0).id()).isEqualTo(savedRoom2.getId())
        );
    }

    @DisplayName("방을 생성함과 동시에 호스트는 방에 자동참여된다")
    @Test
    void createRoom() {
        User user = new User(1L, "name1", "email1@email.com", UserStatus.ACTIVE);
        User savedUser = userRepository.save(user);
        RoomCreateRequest request = new RoomCreateRequest(savedUser.getId(), RoomType.SINGLE, "title");

        roomService.createRoom(request);

        long roomCount = roomRepository.count();
        boolean exists = userRoomRepository.existsByUserId(user.getId());
        assertAll(
                () -> assertThat(roomCount).isOne(),
                () -> assertThat(exists).isTrue()
        );
    }

    @DisplayName("없는 유저를 호스트로 방을 생성할 수 없다")
    @Test
    void canNotCreateRoomWithNonExistingUser() {
        RoomCreateRequest request = new RoomCreateRequest(1L, RoomType.SINGLE, "title");

        assertThatThrownBy(() -> roomService.createRoom(request))
                .isInstanceOf(PingPongClientErrorException.class)
                .hasMessage(ResponseMessage.CLIENT_ERROR.getValue());
    }

    @DisplayName("유저가 ACTIVE 상태가 아니라면 방 생성에 실패한다.")
    @ParameterizedTest
    @EnumSource(value = UserStatus.class, mode = EnumSource.Mode.EXCLUDE, names = "ACTIVE")
    void canNotCreateRoomWithNonActiveUser(UserStatus nonActiveStatus) {
        User user = new User(1L, "name1", "email1@email.com", nonActiveStatus);
        User savedUser = userRepository.save(user);
        RoomCreateRequest request = new RoomCreateRequest(savedUser.getId(), RoomType.SINGLE, "title");

        assertThatThrownBy(() -> roomService.createRoom(request))
                .isInstanceOf(PingPongClientErrorException.class)
                .hasMessage(ResponseMessage.CLIENT_ERROR.getValue());
    }

    @DisplayName("유저가 이미 참여한 방이 있다면 방 생성에 실패한다.")
    @Test
    void canNotCreateRoomWithAlreadyParticipatedUser() {
        User user = new User(1L, "name1", "email1@email.com", UserStatus.ACTIVE);
        User savedUser = userRepository.save(user);
        Room dummy = new Room("room1", savedUser, RoomType.SINGLE);
        Room savedRoom = roomRepository.save(dummy);
        UserRoom userRoom = new UserRoom(user, dummy, Team.RED);
        userRoomRepository.save(userRoom);

        RoomCreateRequest request = new RoomCreateRequest(savedUser.getId(), RoomType.SINGLE, "title");

        assertThatThrownBy(() -> roomService.createRoom(request))
                .isInstanceOf(PingPongClientErrorException.class)
                .hasMessage(ResponseMessage.CLIENT_ERROR.getValue());
    }
}
