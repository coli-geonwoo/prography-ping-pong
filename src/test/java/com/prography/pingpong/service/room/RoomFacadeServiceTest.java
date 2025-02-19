package com.prography.pingpong.service.room;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import com.prography.pingpong.common.BaseServiceTest;
import com.prography.pingpong.domain.room.Room;
import com.prography.pingpong.domain.room.RoomStatus;
import com.prography.pingpong.domain.room.RoomType;
import com.prography.pingpong.domain.user.User;
import com.prography.pingpong.domain.user.UserStatus;
import com.prography.pingpong.domain.userroom.Team;
import com.prography.pingpong.domain.userroom.UserRoom;
import com.prography.pingpong.dto.request.room.RoomCreateRequest;
import com.prography.pingpong.dto.response.room.RoomCreateResponse;
import com.prography.pingpong.dto.response.room.RoomDetailResponse;
import com.prography.pingpong.dto.response.room.RoomPageResponse;
import com.prography.pingpong.exception.custom.PingPongClientErrorException;
import com.prography.pingpong.view.ResponseMessage;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

class RoomFacadeServiceTest extends BaseServiceTest {

    @Autowired
    private RoomFacadeService roomFacadeService;

    @DisplayName("아이디로 방을 찾는다")
    @Test
    void findRoom() {
        User savedUser = userGenerator.generate(1L, UserStatus.ACTIVE);
        Room savedRoom = roomGenerator.generate(savedUser, RoomType.SINGLE, RoomStatus.WAIT);

        RoomDetailResponse roomDetailResponse = roomFacadeService.findRoom(savedRoom.getId());

        assertAll(
                () -> assertThat(roomDetailResponse.id()).isEqualTo(savedRoom.getId()),
                () -> assertThat(roomDetailResponse.roomType()).isEqualTo(savedRoom.getRoomType()),
                () -> assertThat(roomDetailResponse.hostId()).isEqualTo(savedRoom.getHostId()),
                () -> assertThat(roomDetailResponse.title()).isEqualTo(savedRoom.getTitle()),
                () -> assertThat(roomDetailResponse.status()).isEqualTo(savedRoom.getStatus())
        );
    }

    @DisplayName("페이징 된 전체 방을 조회한다")
    @Test
    void findAllRooms() {
        User savedUser1 = userGenerator.generate(1L, UserStatus.ACTIVE);
        User savedUser2 = userGenerator.generate(2L, UserStatus.ACTIVE);
        Room savedRoom1 = roomGenerator.generate(savedUser1, RoomType.SINGLE, RoomStatus.WAIT);
        Room savedRoom2 = roomGenerator.generate(savedUser2, RoomType.SINGLE, RoomStatus.WAIT);

        Pageable pageable = PageRequest.of(1, 1);

        RoomPageResponse roomPageResponse = roomFacadeService.findAllRoom(pageable);

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
        User savedUser = userGenerator.generate(1L, UserStatus.ACTIVE);
        RoomCreateRequest request = new RoomCreateRequest(savedUser.getId(), RoomType.SINGLE, "title");

        roomFacadeService.createRoom(request);

        long roomCount = roomRepository.count();
        boolean exists = userRoomRepository.findByUserId(savedUser.getId()).isPresent();
        assertAll(
                () -> assertThat(roomCount).isOne(),
                () -> assertThat(exists).isTrue()
        );
    }

    @DisplayName("없는 유저를 호스트로 방을 생성할 수 없다")
    @Test
    void canNotCreateRoomWithNonExistingUser() {
        RoomCreateRequest request = new RoomCreateRequest(1L, RoomType.SINGLE, "title");

        assertThatThrownBy(() -> roomFacadeService.createRoom(request))
                .isInstanceOf(PingPongClientErrorException.class)
                .hasMessage(ResponseMessage.CLIENT_ERROR.getValue());
    }

    @DisplayName("유저가 ACTIVE 상태가 아니라면 방 생성에 실패한다.")
    @ParameterizedTest
    @EnumSource(value = UserStatus.class, mode = EnumSource.Mode.EXCLUDE, names = "ACTIVE")
    void canNotCreateRoomWithNonActiveUser(UserStatus nonActiveStatus) {
        User savedUser = userGenerator.generate(1L, nonActiveStatus);
        RoomCreateRequest request = new RoomCreateRequest(savedUser.getId(), RoomType.SINGLE, "title");

        assertThatThrownBy(() -> roomFacadeService.createRoom(request))
                .isInstanceOf(PingPongClientErrorException.class)
                .hasMessage(ResponseMessage.CLIENT_ERROR.getValue());
    }

    @DisplayName("유저가 이미 참여한 방이 있다면 방 생성에 실패한다.")
    @Test
    void canNotCreateRoomWithAlreadyParticipatedUser() {
        User savedUser = userGenerator.generate(1L, UserStatus.ACTIVE);
        Room savedRoom = roomGenerator.generate(savedUser, RoomType.SINGLE, RoomStatus.WAIT);
        UserRoom userRoom = new UserRoom(savedUser, savedRoom, Team.RED);
        userRoomRepository.save(userRoom);

        RoomCreateRequest request = new RoomCreateRequest(savedUser.getId(), RoomType.SINGLE, "title");

        assertThatThrownBy(() -> roomFacadeService.createRoom(request))
                .isInstanceOf(PingPongClientErrorException.class)
                .hasMessage(ResponseMessage.CLIENT_ERROR.getValue());
    }

    @DisplayName("방에 참여할 수 있다")
    @Test
    void attendRoom() {
        User savedUser1 = userGenerator.generate(1L, UserStatus.ACTIVE);
        User savedUser2 = userGenerator.generate(2L, UserStatus.ACTIVE);
        Room savedRoom = roomGenerator.generate(savedUser1, RoomType.SINGLE, RoomStatus.WAIT);
        UserRoom userRoom = new UserRoom(savedUser1, savedRoom, Team.RED);
        userRoomRepository.save(userRoom);

        assertThatCode(() -> roomFacadeService.attendRoom(savedUser2.getId(), savedRoom.getId()))
                .doesNotThrowAnyException();
    }

    @DisplayName("유저가 ACTIVE가 아니라면 방에 참여할 수 없다")
    @ParameterizedTest
    @EnumSource(value = UserStatus.class, mode = EnumSource.Mode.EXCLUDE, names = "ACTIVE")
    void canNotAttendRoomWithNonActiveUser(UserStatus nonActiveStatus) {
        User savedUser1 = userGenerator.generate(1L, UserStatus.ACTIVE);
        User savedUser2 = userGenerator.generate(2L, nonActiveStatus);
        Room savedRoom = roomGenerator.generate(savedUser1, RoomType.SINGLE, RoomStatus.WAIT);
        UserRoom userRoom = new UserRoom(savedUser1, savedRoom, Team.RED);
        userRoomRepository.save(userRoom);

        assertThatThrownBy(() -> roomFacadeService.attendRoom(savedUser2.getId(), savedRoom.getId()))
                .isInstanceOf(PingPongClientErrorException.class)
                .hasMessage(ResponseMessage.CLIENT_ERROR.getValue());
    }

    @DisplayName("유저가 참여중인 다른 방이 있다면 방에 참여할 없다")
    @TestFactory
    Stream<DynamicTest> canNotAttendRoomWhenUserAlreadyAttendedOtherRooms() {
        User savedUser1 = userGenerator.generate(1L, UserStatus.ACTIVE);
        User savedUser2 = userGenerator.generate(2L, UserStatus.ACTIVE);

        AtomicLong createdRoomId = new AtomicLong();

        return Stream.of(
                dynamicTest("유저1이 단식 방을 생성한다", () -> {
                    RoomCreateRequest userOneRequest = new RoomCreateRequest(savedUser1.getId(), RoomType.SINGLE,
                            "title");
                    assertThatCode(() -> {
                        RoomCreateResponse createResponse = roomFacadeService.createRoom(userOneRequest);
                        createdRoomId.set(createResponse.id());
                    }).doesNotThrowAnyException();
                }),
                dynamicTest("유저2가 단식 방을 생성한다.", () -> {
                    RoomCreateRequest userTwoRequest = new RoomCreateRequest(savedUser2.getId(), RoomType.SINGLE,
                            "title");
                    assertThatCode(() -> roomFacadeService.createRoom(userTwoRequest))
                            .doesNotThrowAnyException();
                }),
                dynamicTest("다른 방에 참여중인 유저2가 유저1이 만든 방에 참여할 수 없다", () -> {
                    assertThatThrownBy(() -> roomFacadeService.attendRoom(savedUser2.getId(), createdRoomId.get()))
                            .isInstanceOf(PingPongClientErrorException.class)
                            .hasMessage(ResponseMessage.CLIENT_ERROR.getValue());
                })
        );
    }

    @DisplayName("방이 모두 찼다면 참여할 수 없다")
    @TestFactory
    Stream<DynamicTest> canNotAttendRoomWhenRoomIsFull() {
        User savedUser1 = userGenerator.generate(1L, UserStatus.ACTIVE);
        User savedUser2 = userGenerator.generate(2L, UserStatus.ACTIVE);
        User savedUser3 = userGenerator.generate(3L, UserStatus.ACTIVE);

        AtomicLong createdRoomId = new AtomicLong();

        return Stream.of(
                dynamicTest("유저1이 단식 방을 생성한다", () -> {
                    RoomCreateRequest userOneRequest = new RoomCreateRequest(savedUser1.getId(), RoomType.SINGLE,
                            "title");
                    assertThatCode(() -> {
                        RoomCreateResponse createResponse = roomFacadeService.createRoom(userOneRequest);
                        createdRoomId.set(createResponse.id());
                    }).doesNotThrowAnyException();
                }),
                dynamicTest("유저2가 방에 참여한다.", () -> {
                    assertThatCode(() -> roomFacadeService.attendRoom(savedUser2.getId(), createdRoomId.get()))
                            .doesNotThrowAnyException();
                }),
                dynamicTest("인원이 찬 단식 방에 유저3가 참여할 수 없다", () -> {
                    assertThatThrownBy(() -> roomFacadeService.attendRoom(savedUser3.getId(), createdRoomId.get()))
                            .isInstanceOf(PingPongClientErrorException.class)
                            .hasMessage(ResponseMessage.CLIENT_ERROR.getValue());
                })
        );
    }

    @DisplayName("방장이 나갈 경우, 모든 회원을 퇴장시킨다")
    @Test
    void exitAllUserWhenHostExit() {
        User savedHost = userGenerator.generate(1L, UserStatus.ACTIVE);
        User savedUser = userGenerator.generate(2L, UserStatus.ACTIVE);
        Room savedRoom = roomGenerator.generate(savedHost, RoomType.SINGLE, RoomStatus.WAIT);

        UserRoom userRoom1 = new UserRoom(savedHost, savedRoom, Team.RED);
        UserRoom userRoom2 = new UserRoom(savedUser, savedRoom, Team.BLUE);
        userRoomRepository.save(userRoom1);
        userRoomRepository.save(userRoom2);

        roomFacadeService.exitRoom(savedHost.getId(), savedRoom.getId());

        List<UserRoom> allRoomUsers = userRoomRepository.findAllByRoomId(savedRoom.getId());
        assertThat(allRoomUsers).isEmpty();
    }

    @DisplayName("방을 나갈 수 있다")
    @Test
    void exitUser() {
        User savedHost = userGenerator.generate(1L, UserStatus.ACTIVE);
        User savedUser = userGenerator.generate(2L, UserStatus.ACTIVE);
        Room savedRoom = roomGenerator.generate(savedHost, RoomType.SINGLE, RoomStatus.WAIT);

        UserRoom userRoom1 = new UserRoom(savedHost, savedRoom, Team.RED);
        UserRoom userRoom2 = new UserRoom(savedUser, savedRoom, Team.BLUE);
        userRoomRepository.save(userRoom1);
        userRoomRepository.save(userRoom2);

        roomFacadeService.exitRoom(savedUser.getId(), savedRoom.getId());

        List<UserRoom> allRoomUsers = userRoomRepository.findAllByRoomId(savedRoom.getId());
        assertThat(allRoomUsers).hasSize(1);
    }

    @DisplayName("방이 이미 시작한 상태라면 나갈 수 없다")
    @ParameterizedTest
    @EnumSource(value = RoomStatus.class, mode = EnumSource.Mode.EXCLUDE, names = "WAIT")
    void canNotExitWhenRoomAlreadyStart(RoomStatus alreadyStartStatus) {
        User savedUser1 = userGenerator.generate(1L, UserStatus.ACTIVE);
        Room alreadyStartedRoom = roomGenerator.generate(savedUser1, RoomType.SINGLE, alreadyStartStatus);

        UserRoom userRoom1 = new UserRoom(savedUser1, alreadyStartedRoom, Team.RED);
        userRoomRepository.save(userRoom1);

        assertThatThrownBy(() -> roomFacadeService.exitRoom(savedUser1.getId(), alreadyStartedRoom.getId()))
                .isInstanceOf(PingPongClientErrorException.class)
                .hasMessage(ResponseMessage.CLIENT_ERROR.getValue());
    }

    @DisplayName("존재하지 않는 방에 대한 나가기 요청을 할 수 없다")
    @Test
    void canNotExitNotExistingRoom() {
        User savedUser1 = userGenerator.generate(1L, UserStatus.ACTIVE);

        assertThatThrownBy(() -> roomFacadeService.exitRoom(savedUser1.getId(), 1L))
                .isInstanceOf(PingPongClientErrorException.class)
                .hasMessage(ResponseMessage.CLIENT_ERROR.getValue());
    }

    @DisplayName("방 나가기 시나리오 테스트")
    @TestFactory
    Stream<DynamicTest> exitRoom() {
        User savedUser1 = userGenerator.generate(1L, UserStatus.ACTIVE);
        User savedUser2 = userGenerator.generate(2L, UserStatus.ACTIVE);
        User savedUser3 = userGenerator.generate(3L, UserStatus.ACTIVE);
        User savedUser4 = userGenerator.generate(4L, UserStatus.ACTIVE);

        AtomicLong createdRoomId = new AtomicLong();

        return Stream.of(
                dynamicTest("유저1이 복식 방을 생성한다", () -> {
                    RoomCreateRequest userOneRequest = new RoomCreateRequest(savedUser1.getId(), RoomType.DOUBLE,
                            "title");
                    assertThatCode(() -> {
                        RoomCreateResponse createResponse = roomFacadeService.createRoom(userOneRequest);
                        createdRoomId.set(createResponse.id());
                    }).doesNotThrowAnyException();
                }),
                dynamicTest("유저2,3,4,가 방에 참여한다.", () -> {
                    assertThatCode(() -> {
                        roomFacadeService.attendRoom(savedUser2.getId(), createdRoomId.get());
                        roomFacadeService.attendRoom(savedUser3.getId(), createdRoomId.get());
                        roomFacadeService.attendRoom(savedUser4.getId(), createdRoomId.get());
                    }).doesNotThrowAnyException();
                }),
                dynamicTest("방장이 아닌 유저 2가 방을 나간다", () -> {
                    roomFacadeService.exitRoom(savedUser2.getId(), createdRoomId.get());

                    List<UserRoom> allRoomUsers = userRoomRepository.findAllByRoomId(createdRoomId.get());
                    assertThat(allRoomUsers.size()).isEqualTo(3);
                }),
                dynamicTest("방장인 유저 1이 방을 나가면 모든 유저를 퇴장시킨다", () -> {
                    roomFacadeService.exitRoom(savedUser1.getId(), createdRoomId.get());

                    List<UserRoom> allRoomUsers = userRoomRepository.findAllByRoomId(createdRoomId.get());
                    assertThat(allRoomUsers.size()).isZero();
                }),
                dynamicTest("모든 유저가 나간 방은 FINISH 상태로 변경된다", () -> {
                    Room room = roomRepository.findById(createdRoomId.get()).get();

                    assertThat(room.getStatus()).isEqualTo(RoomStatus.FINISH);
                })
        );
    }

    @DisplayName("게임을 시작할 수 있다")
    @Test
    void startRoom() {
        User savedHost = userGenerator.generate(1L, UserStatus.ACTIVE);
        User savedUser = userGenerator.generate(2L, UserStatus.ACTIVE);

        Room savedRoom = roomGenerator.generate(savedHost, RoomType.SINGLE, RoomStatus.WAIT);

        UserRoom userRoom1 = new UserRoom(savedHost, savedRoom, Team.RED);
        UserRoom userRoom2 = new UserRoom(savedUser, savedRoom, Team.BLUE);
        userRoomRepository.save(userRoom1);
        userRoomRepository.save(userRoom2);

        roomFacadeService.startRoom(savedHost.getId(), savedRoom.getId());

        Room startedRoom = roomRepository.findById(savedRoom.getId()).get();
        assertThat(startedRoom.getStatus()).isEqualTo(RoomStatus.PROGRESS);
    }

    @DisplayName("방장이 아니면 게임을 시작할 수 없다")
    @Test
    void canNotStartRoomWhenUserIsNotHost() {
        User savedHost = userGenerator.generate(1L, UserStatus.ACTIVE);
        User savedUser = userGenerator.generate(2L, UserStatus.ACTIVE);

        Room savedRoom = roomGenerator.generate(savedHost, RoomType.SINGLE, RoomStatus.WAIT);

        UserRoom userRoom1 = new UserRoom(savedHost, savedRoom, Team.RED);
        UserRoom userRoom2 = new UserRoom(savedUser, savedRoom, Team.BLUE);
        userRoomRepository.save(userRoom1);
        userRoomRepository.save(userRoom2);

        assertThatThrownBy(() -> roomFacadeService.startRoom(savedUser.getId(), savedRoom.getId()))
                .isInstanceOf(PingPongClientErrorException.class)
                .hasMessage(ResponseMessage.CLIENT_ERROR.getValue());
    }

    @DisplayName("인원이 모두 차지 않았다면 게임을 시작할 수 없다")
    @Test
    void canNotStartRoomWhenRoomIsNotFull() {
        User savedHost = userGenerator.generate(1L, UserStatus.ACTIVE);
        Room savedRoom = roomGenerator.generate(savedHost, RoomType.SINGLE, RoomStatus.WAIT);

        UserRoom userRoom1 = new UserRoom(savedHost, savedRoom, Team.RED);
        userRoomRepository.save(userRoom1);

        assertThatThrownBy(() -> roomFacadeService.startRoom(savedHost.getId(), savedRoom.getId()))
                .isInstanceOf(PingPongClientErrorException.class)
                .hasMessage(ResponseMessage.CLIENT_ERROR.getValue());
    }

    @DisplayName("이미 시작한 방은 시작할 수 없다")
    @ParameterizedTest
    @EnumSource(value = RoomStatus.class, mode = EnumSource.Mode.EXCLUDE, names = "WAIT")
    void canNotStartRoomWhenRoomIsNotWait(RoomStatus notWaitStatus) {
        User savedHost = userGenerator.generate(1L, UserStatus.ACTIVE);
        User savedUser = userGenerator.generate(2L, UserStatus.ACTIVE);

        Room alreadyStartedRoom = roomGenerator.generate(savedHost, RoomType.SINGLE, notWaitStatus);

        UserRoom userRoom1 = new UserRoom(savedHost, alreadyStartedRoom, Team.RED);
        UserRoom userRoom2 = new UserRoom(savedUser, alreadyStartedRoom, Team.BLUE);
        userRoomRepository.save(userRoom1);
        userRoomRepository.save(userRoom2);

        assertThatThrownBy(() -> roomFacadeService.startRoom(savedHost.getId(), alreadyStartedRoom.getId()))
                .isInstanceOf(PingPongClientErrorException.class)
                .hasMessage(ResponseMessage.CLIENT_ERROR.getValue());
    }

    @DisplayName("존재하지 않는 방을 시작할 수 없다")
    @Test
    void canNotStartRoomWhenNotExistingRoom() {
        User savedHost = userGenerator.generate(1L, UserStatus.ACTIVE);

        assertThatThrownBy(() -> roomFacadeService.startRoom(savedHost.getId(), 100L))
                .isInstanceOf(PingPongClientErrorException.class)
                .hasMessage(ResponseMessage.CLIENT_ERROR.getValue());
    }

    @Disabled
    @DisplayName("방 시작하기 시나리오 테스트")
    @TestFactory
    Stream<DynamicTest> startScenarioTest() {
        User savedUser1 = userGenerator.generate(1L, UserStatus.ACTIVE);
        User savedUser2 = userGenerator.generate(2L, UserStatus.ACTIVE);

        AtomicLong createdRoomId = new AtomicLong();

        return Stream.of(
                dynamicTest("유저1이 단식 방을 생성한다", () -> {
                    RoomCreateRequest userOneRequest = new RoomCreateRequest(savedUser1.getId(), RoomType.SINGLE,
                            "title");
                    assertThatCode(() -> {
                        RoomCreateResponse createResponse = roomFacadeService.createRoom(userOneRequest);
                        createdRoomId.set(createResponse.id());
                    }).doesNotThrowAnyException();
                }),
                dynamicTest("유저2가 방에 참여한다.", () -> {
                    assertThatCode(() -> roomFacadeService.attendRoom(savedUser2.getId(), createdRoomId.get()))
                            .doesNotThrowAnyException();
                }),
                dynamicTest("방장이 방을 시작한다", () -> {
                    roomFacadeService.startRoom(savedUser1.getId(), createdRoomId.get());

                    Room startedRoom = roomRepository.findById(createdRoomId.get()).get();
                    assertThat(startedRoom.getStatus()).isEqualTo(RoomStatus.PROGRESS);
                }),
                dynamicTest("1분 뒤 방의 상태가 FINISH로 바뀐다", () -> {
                    Thread.sleep(1000 * 70);
                    Room finishedRoom = roomRepository.findById(createdRoomId.get()).get();
                    assertThat(finishedRoom.getStatus()).isEqualTo(RoomStatus.FINISH);
                })
        );
    }
}
