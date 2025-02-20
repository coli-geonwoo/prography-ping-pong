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
        Room savedRoom = roomGenerator.generate(savedUser1, RoomType.SINGLE, RoomStatus.WAIT);

        Team team = Team.RED;
        UserRoom savedUserRoom1 = userRoomGenerator.generate(savedUser1, savedRoom, team);

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
        Room alreadyStartedRoom = roomGenerator.generate(savedUser1, RoomType.SINGLE, notWaitStatus);
        UserRoom savedUserRoom1 = userRoomGenerator.generate(savedUser1, alreadyStartedRoom, Team.RED);

        TeamChangeRequest request = new TeamChangeRequest(savedUser1.getId());

        RestAssured.given()
                .body(request)
                .contentType(ContentType.JSON)
                .pathParam("roomId", alreadyStartedRoom.getId())
                .when().put("/team/{roomId}")
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }

    @DisplayName("변경하려는 팀 인원이 모두 차있다면 팀을 변경할 수 없다")
    @Test
    void canNotChangeTeamWhenOppositeTeamIsFull() {
        User savedUser1 = userGenerator.generate(1L, UserStatus.ACTIVE);
        User savedUser2 = userGenerator.generate(2L, UserStatus.ACTIVE);
        Room savedRoom = roomGenerator.generate(savedUser1, RoomType.SINGLE, RoomStatus.WAIT);

        UserRoom savedUserRoom1 = userRoomGenerator.generate(savedUser1, savedRoom, Team.RED);
        UserRoom savedUserRoom2 = userRoomGenerator.generate(savedUser2, savedRoom, Team.BLUE);

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
