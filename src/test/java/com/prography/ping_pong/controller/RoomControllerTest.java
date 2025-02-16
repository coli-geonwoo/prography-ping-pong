package com.prography.ping_pong.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.prography.ping_pong.common.BaseControllerTest;
import com.prography.ping_pong.domain.room.RoomStatus;
import com.prography.ping_pong.domain.userroom.Team;
import com.prography.ping_pong.domain.userroom.UserRoom;
import com.prography.ping_pong.domain.room.Room;
import com.prography.ping_pong.domain.room.RoomType;
import com.prography.ping_pong.domain.user.User;
import com.prography.ping_pong.domain.user.UserStatus;
import com.prography.ping_pong.dto.request.room.RoomAttendRequest;
import com.prography.ping_pong.dto.request.room.RoomCreateRequest;
import com.prography.ping_pong.dto.request.room.RoomExitRequest;
import com.prography.ping_pong.dto.request.room.RoomStartRequest;
import com.prography.ping_pong.dto.response.ApiResponse;
import com.prography.ping_pong.dto.response.room.RoomStartResponse;
import com.prography.ping_pong.exception.custom.PingPongClientErrorException;
import com.prography.ping_pong.view.ResponseMessage;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.http.HttpStatus;

class RoomControllerTest extends BaseControllerTest {

    @DisplayName("방을 찾을 수 있다")
    @Test
    void findRoom() {
        User user = new User(1L, "name1", "email1@email.com", UserStatus.ACTIVE);
        User savedUser = userRepository.save(user);

        Room dummy = new Room("room1", savedUser.getId(), RoomType.SINGLE);
        Room savedRoom = roomRepository.save(dummy);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .pathParam("roomId", savedRoom.getId())
                .when().get("/room/{roomId}")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @DisplayName("없는 방을 조회할 경우 에러가 반환된다")
    @Test
    void findNonExistingRoom() {
        ApiResponse response = RestAssured.given()
                .contentType(ContentType.JSON)
                .pathParam("roomId", 1000L)
                .when().get("/room/{roomId}")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(ApiResponse.class);

        assertAll(
                () -> assertThat(response.code()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.message()).isEqualTo(ResponseMessage.CLIENT_ERROR.getValue())
        );
    }

    @DisplayName("페이징된 전체 방들을 조회한다")
    @Test
    void findAllRooms() {
        User user1 = new User(1L, "name1", "email1@email.com", UserStatus.ACTIVE);
        User user2 = new User(2L, "name2", "email2@email.com", UserStatus.ACTIVE);
        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);

        Room dummy1 = new Room("room1", savedUser1.getId(), RoomType.SINGLE);
        Room dummy2 = new Room("room2", savedUser2.getId(), RoomType.SINGLE);
        Room savedRoom1 = roomRepository.save(dummy1);
        Room savedRoom2 = roomRepository.save(dummy2);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .queryParam("size", 1)
                .queryParam("page", 1)
                .when().get("/room")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @DisplayName("방을 생성할 수 있다")
    @Test
    void createRoom() {
        User user = new User(1L, "name1", "email1@email.com", UserStatus.ACTIVE);
        User savedUser = userRepository.save(user);
        RoomCreateRequest request = new RoomCreateRequest(savedUser.getId(), RoomType.SINGLE, "title");

        ApiResponse response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/room")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(ApiResponse.class);

        long count = roomRepository.count();

        assertAll(
                () -> assertThat(response.code()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.message()).isEqualTo(ResponseMessage.SUCCESS.getValue()),
                () -> assertThat(count).isOne()
        );
    }

    @DisplayName("없는 유저를 호스트로 방을 생성할 수 없다")
    @Test
    void canNotCreateRoomWithNonExistingUser() {
        RoomCreateRequest request = new RoomCreateRequest(1L, RoomType.SINGLE, "title");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/room")
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }

    @DisplayName("유저가 ACTIVE 상태가 아니라면 방 생성에 실패한다.")
    @ParameterizedTest
    @EnumSource(value = UserStatus.class, mode = EnumSource.Mode.EXCLUDE, names = "ACTIVE")
    void canNotCreateRoomWithNonActiveUser(UserStatus nonActiveStatus) {
        User user = new User(1L, "name1", "email1@email.com", nonActiveStatus);
        User savedUser = userRepository.save(user);
        RoomCreateRequest request = new RoomCreateRequest(savedUser.getId(), RoomType.SINGLE, "title");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/room")
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }

    @DisplayName("유저가 이미 참여한 방이 있다면 방 생성에 실패한다.")
    @Test
    void canNotCreateRoomWithAlreadyParticipatedUser() {
        User user = new User(1L, "name1", "email1@email.com", UserStatus.ACTIVE);
        User savedUser = userRepository.save(user);
        Room dummy = new Room("room1", savedUser.getId(), RoomType.SINGLE);
        Room savedRoom = roomRepository.save(dummy);
        UserRoom userRoom = new UserRoom(user, dummy, Team.RED);
        userRoomRepository.save(userRoom);

        RoomCreateRequest request = new RoomCreateRequest(savedUser.getId(), RoomType.SINGLE, "title");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/room")
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }

    @DisplayName("방에 참여할 수 있다")
    @Test
    void attendRoom() {
        User user1 = new User(1L, "name1", "email1@email.com", UserStatus.ACTIVE);
        User user2 = new User(2L, "name2", "email2@email.com", UserStatus.ACTIVE);
        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);
        Room dummy = new Room("room1", savedUser1.getId(), RoomType.SINGLE);
        Room savedRoom = roomRepository.save(dummy);
        UserRoom userRoom = new UserRoom(savedUser1, dummy, Team.RED);
        userRoomRepository.save(userRoom);

        RoomAttendRequest request = new RoomAttendRequest(savedUser2.getId());

        ApiResponse response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .pathParam("roomId", savedRoom.getId())
                .when().post("/room/attention/{roomId}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(ApiResponse.class);

        boolean exists = userRoomRepository.findByUserId(savedUser2.getId()).isPresent();

        assertAll(
                () -> assertThat(response.code()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.message()).isEqualTo(ResponseMessage.SUCCESS.getValue()),
                () -> assertThat(exists).isTrue()
        );
    }

    @DisplayName("유저가 ACTIVE 하지 않다면 방에 참여할 수 없다")
    @ParameterizedTest
    @EnumSource(value = UserStatus.class, mode = EnumSource.Mode.EXCLUDE, names = "ACTIVE")
    void canNotAttendRoomWhenUserIsNotActive(UserStatus nonActiveStatus) {
        User user1 = new User(1L, "name1", "email1@email.com", UserStatus.ACTIVE);
        User nonActiveUser = new User(2L, "name2", "email2@email.com", nonActiveStatus);
        User savedUser1 = userRepository.save(user1);
        User savedNonActiveUser = userRepository.save(nonActiveUser);
        Room dummy = new Room("room1", savedUser1.getId(), RoomType.SINGLE);
        Room savedRoom = roomRepository.save(dummy);
        UserRoom userRoom = new UserRoom(savedUser1, dummy, Team.RED);
        userRoomRepository.save(userRoom);

        RoomAttendRequest request = new RoomAttendRequest(savedNonActiveUser.getId());

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .pathParam("roomId", savedRoom.getId())
                .when().post("/room/attention/{roomId}")
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }


    @DisplayName("방장이 나갈 경우, 모든 회원을 퇴장시킨다")
    @Test
    void exitAllUserWhenHostExit() {
        User host = new User(1L, "name1", "email1@email.com", UserStatus.ACTIVE);
        User user = new User(2L, "name2", "email2@email.com", UserStatus.ACTIVE);
        User savedHost = userRepository.save(host);
        User savedUser = userRepository.save(user);

        Room room = new Room("title", savedHost.getId(), RoomType.SINGLE);
        Room savedRoom = roomRepository.save(room);

        UserRoom userRoom1 = new UserRoom(savedHost, room, Team.RED);
        UserRoom userRoom2 = new UserRoom(savedUser, room, Team.BLUE);
        userRoomRepository.save(userRoom1);
        userRoomRepository.save(userRoom2);

        RoomExitRequest request = new RoomExitRequest(savedHost.getId());

        ApiResponse response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .pathParam("roomId", savedRoom.getId())
                .when().post("/room/out/{roomId}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(ApiResponse.class);

        List<UserRoom> allRoomUsers = userRoomRepository.findAllByRoomId(savedRoom.getId());

        assertAll(
                () -> assertThat(response.code()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.message()).isEqualTo(ResponseMessage.SUCCESS.getValue()),
                () -> assertThat(allRoomUsers).isEmpty()
        );
    }

    @DisplayName("방을 나갈 수 있다")
    @Test
    void exitUser() {
        User host = new User(1L, "name1", "email1@email.com", UserStatus.ACTIVE);
        User user = new User(2L, "name2", "email2@email.com", UserStatus.ACTIVE);
        User savedHost = userRepository.save(host);
        User savedUser = userRepository.save(user);

        Room room = new Room("title", savedHost.getId(), RoomType.SINGLE);
        Room savedRoom = roomRepository.save(room);

        UserRoom userRoom1 = new UserRoom(savedHost, room, Team.RED);
        UserRoom userRoom2 = new UserRoom(savedUser, room, Team.BLUE);
        userRoomRepository.save(userRoom1);
        userRoomRepository.save(userRoom2);

        RoomExitRequest request = new RoomExitRequest(savedUser.getId());

        ApiResponse response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .pathParam("roomId", savedRoom.getId())
                .when().post("/room/out/{roomId}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(ApiResponse.class);

        List<UserRoom> allRoomUsers = userRoomRepository.findAllByRoomId(savedRoom.getId());

        assertAll(
                () -> assertThat(response.code()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.message()).isEqualTo(ResponseMessage.SUCCESS.getValue()),
                () -> assertThat(allRoomUsers).hasSize(1)
        );
    }

    @DisplayName("방이 이미 시작한 상태라면 나갈 수 없다")
    @ParameterizedTest
    @EnumSource(value = RoomStatus.class, mode = EnumSource.Mode.EXCLUDE, names = "WAIT")
    void canNotExitWhenRoomAlreadyStart(RoomStatus alreadyStartStatus) {
        User user1 = new User(1L, "name1", "email1@email.com", UserStatus.ACTIVE);
        User savedUser1 = userRepository.save(user1);

        Room room = new Room(null, "title", user1.getId(), RoomType.SINGLE, alreadyStartStatus);
        Room savedRoom = roomRepository.save(room);

        UserRoom userRoom1 = new UserRoom(savedUser1, room, Team.RED);
        userRoomRepository.save(userRoom1);

        RoomExitRequest request = new RoomExitRequest(savedUser1.getId());

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .pathParam("roomId", savedRoom.getId())
                .when().post("/room/out/{roomId}")
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }

    @DisplayName("존재하지 않는 방에 대한 나가기 요청을 할 수 없다")
    @Test
    void canNotExitNotExistingRoom() {
        User user1 = new User(1L, "name1", "email1@email.com", UserStatus.ACTIVE);
        User savedUser1 = userRepository.save(user1);

        RoomExitRequest request = new RoomExitRequest(savedUser1.getId());

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .pathParam("roomId", 1000L)
                .when().post("/room/out/{roomId}")
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }

    @DisplayName("게임을 시작할 수 있다")
    @Test
    void startRoom() {
        User host = new User(1L, "name1", "email1@email.com", UserStatus.ACTIVE);
        User user = new User(2L, "name2", "email2@email.com", UserStatus.ACTIVE);
        User savedHost = userRepository.save(host);
        User savedUser = userRepository.save(user);

        Room room = new Room("title", savedHost.getId(), RoomType.SINGLE);
        Room savedRoom = roomRepository.save(room);

        UserRoom userRoom1 = new UserRoom(savedHost, room, Team.RED);
        UserRoom userRoom2 = new UserRoom(savedUser, room, Team.BLUE);
        userRoomRepository.save(userRoom1);
        userRoomRepository.save(userRoom2);

        RoomStartRequest request = new RoomStartRequest(savedHost.getId());

        ApiResponse response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .pathParam("roomId", savedRoom.getId())
                .when().put("/room/start/{roomId}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(ApiResponse.class);

        Room startedRoom = roomRepository.findById(savedRoom.getId()).get();

        assertAll(
                () -> assertThat(response.code()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.message()).isEqualTo(ResponseMessage.SUCCESS.getValue()),
                () -> assertThat(startedRoom.getStatus()).isEqualTo(RoomStatus.PROGRESS)

        );
    }

    @DisplayName("방장이 아니면 게임을 시작할 수 없다")
    @Test
    void canNotStartRoomWhenUserIsNotHost() {
        User host = new User(1L, "name1", "email1@email.com", UserStatus.ACTIVE);
        User user = new User(2L, "name2", "email2@email.com", UserStatus.ACTIVE);
        User savedHost = userRepository.save(host);
        User savedUser = userRepository.save(user);

        Room room = new Room("title", savedHost.getId(), RoomType.SINGLE);
        Room savedRoom = roomRepository.save(room);

        UserRoom userRoom1 = new UserRoom(savedHost, room, Team.RED);
        UserRoom userRoom2 = new UserRoom(savedUser, room, Team.BLUE);
        userRoomRepository.save(userRoom1);
        userRoomRepository.save(userRoom2);

        RoomStartRequest request = new RoomStartRequest(savedUser.getId());

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .pathParam("roomId", savedRoom.getId())
                .when().put("/room/start/{roomId}")
                .then()
                .statusCode(HttpStatus.CREATED.value());

    }

    @DisplayName("인원이 모두 차지 않았다면 게임을 시작할 수 없다")
    @Test
    void canNotStartRoomWhenRoomIsNotFull() {
        User host = new User(1L, "name1", "email1@email.com", UserStatus.ACTIVE);
        User savedHost = userRepository.save(host);

        Room room = new Room("title", savedHost.getId(), RoomType.SINGLE);
        Room savedRoom = roomRepository.save(room);

        UserRoom userRoom1 = new UserRoom(savedHost, room, Team.RED);
        userRoomRepository.save(userRoom1);

        RoomStartRequest request = new RoomStartRequest(savedHost.getId());

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .pathParam("roomId", savedRoom.getId())
                .when().put("/room/start/{roomId}")
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }

    @DisplayName("이미 시작한 방은 시작할 수 없다")
    @ParameterizedTest
    @EnumSource(value = RoomStatus.class, mode = EnumSource.Mode.EXCLUDE, names = "WAIT")
    void canNotStartRoomWhenRoomIsNotWait(RoomStatus notWaitStatus) {
        User host = new User(1L, "name1", "email1@email.com", UserStatus.ACTIVE);
        User user = new User(2L, "name2", "email2@email.com", UserStatus.ACTIVE);
        User savedHost = userRepository.save(host);
        User savedUser = userRepository.save(user);

        Room room = new Room(null, "title", savedHost.getId(), RoomType.SINGLE, notWaitStatus);
        Room savedRoom = roomRepository.save(room);

        UserRoom userRoom1 = new UserRoom(savedHost, room, Team.RED);
        UserRoom userRoom2 = new UserRoom(savedUser, room, Team.BLUE);
        userRoomRepository.save(userRoom1);
        userRoomRepository.save(userRoom2);

        RoomStartRequest request = new RoomStartRequest(savedHost.getId());

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .pathParam("roomId", savedRoom.getId())
                .when().put("/room/start/{roomId}")
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }

    @DisplayName("존재하지 않는 방을 시작할 수 없다")
    @Test
    void canNotStartRoomWhenNotExistingRoom() {
        User host = new User(1L, "name1", "email1@email.com", UserStatus.ACTIVE);
        User savedHost = userRepository.save(host);

        RoomStartRequest request = new RoomStartRequest(savedHost.getId());

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .pathParam("roomId", 100L)
                .when().put("/room/start/{roomId}")
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }
}
