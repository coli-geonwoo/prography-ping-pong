package com.prography.ping_pong.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.prography.ping_pong.common.BaseControllerTest;
import com.prography.ping_pong.dto.request.user.UserInitializeRequest;
import com.prography.ping_pong.dto.response.ApiResponse;
import com.prography.ping_pong.repository.UserRepository;
import com.prography.ping_pong.view.ResponseMessage;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class UserControllerTest extends BaseControllerTest {

    @Autowired
    private UserRepository userRepository;


    @DisplayName("유저를 초기화한다")
    @Test
    void initialize() {
        UserInitializeRequest request = new UserInitializeRequest(1,10);

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
}
