package com.prography.pingpong.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.prography.pingpong.common.BaseControllerTest;
import com.prography.pingpong.domain.user.UserStatus;
import com.prography.pingpong.dto.request.user.UserInitializeRequest;
import com.prography.pingpong.dto.response.ApiResponse;
import com.prography.pingpong.view.ResponseMessage;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class UserControllerTest extends BaseControllerTest {

    @DisplayName("유저를 초기화한다")
    @Test
    void initialize() {
        UserInitializeRequest request = new UserInitializeRequest(1, 10);

        ApiResponse response = RestAssured.given()
                .body(request)
                .contentType(ContentType.JSON)
                .when().post("/init")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(ApiResponse.class);

        long userCount = userRepository.count();

        assertAll(
                () -> assertThat(response.code()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.message()).isEqualTo(ResponseMessage.SUCCESS.getValue()),
                () -> assertThat(userCount).isEqualTo(request.quantity())
        );
    }

    @DisplayName("유저 페이지를 조회한다")
    @Test
    void findUsers() {
        userGenerator.generate(1L, UserStatus.ACTIVE);
        userGenerator.generate(2L, UserStatus.ACTIVE);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .queryParam("size", 1)
                .queryParam("page", 1)
                .when().get("/user")
                .then()
                .statusCode(HttpStatus.OK.value());
    }
}
