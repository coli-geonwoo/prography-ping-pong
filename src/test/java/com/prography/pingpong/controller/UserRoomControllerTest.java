package com.prography.pingpong.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.prography.pingpong.common.BaseControllerTest;
import com.prography.pingpong.domain.room.Room;
import com.prography.pingpong.domain.room.RoomStatus;
import com.prography.pingpong.domain.room.RoomType;
import com.prography.pingpong.domain.user.User;
import com.prography.pingpong.domain.user.UserStatus;
import com.prography.pingpong.domain.userroom.Team;
import com.prography.pingpong.domain.userroom.UserRoom;
import com.prography.pingpong.dto.request.room.TeamChangeRequest;
import com.prography.pingpong.dto.response.ApiResponse;
import com.prography.pingpong.view.ResponseMessage;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;
import org.springframework.http.HttpStatus;

class UserRoomControllerTest extends BaseControllerTest {

    @DisplayName("팀을 변경할 수 있다")
    @Test
    void changeTeam() {
        User savedUser1 = userGenerator.generate(1L, UserStatus.ACTIVE);

        Room dummy = new Room("room1", savedUser1.getId(), RoomType.SINGLE);
        Room savedRoom = roomRepository.save(dummy);

        Team team = Team.RED;
        UserRoom userRoom1 = new UserRoom(savedUser1, dummy, team);
        UserRoom savedUserRoom1 = userRoomRepository.save(userRoom1);

        TeamChangeRequest request = new TeamChangeRequest(savedUser1.getId());

        ApiResponse response = RestAssured.given()
                .body(request)
                .contentType(ContentType.JSON)
                .pathParam("roomId", savedRoom.getId())
                .when().put("/team/{roomId}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(ApiResponse.class);

        UserRoom userRoom = userRoomRepository.findByUserIdAndRoomId(savedUser1.getId(), savedRoom.getId()).get();
        assertAll(
                () -> assertThat(response.code()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.message()).isEqualTo(ResponseMessage.SUCCESS.getValue()),
                () -> assertThat(userRoom.getTeam()).isEqualTo(team.opposite())
        );
    }

    @DisplayName("이미 방이 시작되었다면 팀을 변경할 수 없다")
    @ParameterizedTest
    @EnumSource(value = RoomStatus.class, mode = Mode.EXCLUDE, names = "WAIT")
    void canNotChangeTeamWhenRoomStatusIsNotWait(RoomStatus notWaitStatus) {
        User savedUser1 = userGenerator.generate(1L, UserStatus.ACTIVE);

        Room dummy = new Room(null, "room1", savedUser1.getId(), RoomType.SINGLE, notWaitStatus);
        Room savedRoom = roomRepository.save(dummy);

        UserRoom userRoom1 = new UserRoom(savedUser1, dummy, Team.RED);
        UserRoom savedUserRoom1 = userRoomRepository.save(userRoom1);

        TeamChangeRequest request = new TeamChangeRequest(savedUser1.getId());

        RestAssured.given()
                .body(request)
                .contentType(ContentType.JSON)
                .pathParam("roomId", savedRoom.getId())
                .when().put("/team/{roomId}")
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }

    @DisplayName("변경하려는 팀 인원이 모두 차있다면 팀을 변경할 수 없다")
    @Test
    void canNotChangeTeamWhenOppositeTeamIsFull() {
        User savedUser1 = userGenerator.generate(1L, UserStatus.ACTIVE);
        User savedUser2 = userGenerator.generate(2L, UserStatus.ACTIVE);

        Room dummy = new Room("room1", savedUser1.getId(), RoomType.SINGLE);
        Room savedRoom = roomRepository.save(dummy);

        UserRoom userRoom1 = new UserRoom(savedUser1, dummy, Team.RED);
        UserRoom userRoom2 = new UserRoom(savedUser2, dummy, Team.BLUE);
        UserRoom savedUserRoom1 = userRoomRepository.save(userRoom1);
        UserRoom savedUserRoom2 = userRoomRepository.save(userRoom2);

        TeamChangeRequest request = new TeamChangeRequest(savedUser1.getId());

        RestAssured.given()
                .body(request)
                .contentType(ContentType.JSON)
                .pathParam("roomId", savedRoom.getId())
                .when().put("/team/{roomId}")
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }
}
