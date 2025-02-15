package com.prography.ping_pong.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.prography.ping_pong.common.BaseControllerTest;
import com.prography.ping_pong.domain.Team;
import com.prography.ping_pong.domain.UserRoom;
import com.prography.ping_pong.domain.room.Room;
import com.prography.ping_pong.domain.room.RoomType;
import com.prography.ping_pong.domain.user.User;
import com.prography.ping_pong.domain.user.UserStatus;
import com.prography.ping_pong.dto.request.room.RoomCreateRequest;
import com.prography.ping_pong.dto.response.ApiResponse;
import com.prography.ping_pong.view.ResponseMessage;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
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

        Room dummy = new Room("room1", savedUser, RoomType.SINGLE);
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

        Room dummy1 = new Room("room1", savedUser1, RoomType.SINGLE);
        Room dummy2 = new Room("room2", savedUser2, RoomType.SINGLE);
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
        Room dummy = new Room("room1", savedUser, RoomType.SINGLE);
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
}
