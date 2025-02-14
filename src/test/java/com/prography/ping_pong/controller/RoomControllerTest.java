package com.prography.ping_pong.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.prography.ping_pong.common.BaseControllerTest;
import com.prography.ping_pong.domain.room.Room;
import com.prography.ping_pong.domain.room.RoomType;
import com.prography.ping_pong.domain.user.User;
import com.prography.ping_pong.domain.user.UserStatus;
import com.prography.ping_pong.dto.response.ApiResponse;
import com.prography.ping_pong.repository.RoomRepository;
import com.prography.ping_pong.repository.UserRepository;
import com.prography.ping_pong.view.ResponseMessage;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
}
