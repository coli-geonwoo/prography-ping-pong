package com.prography.ping_pong.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.prography.ping_pong.common.BaseControllerTest;
import com.prography.ping_pong.dto.response.ApiResponse;
import com.prography.ping_pong.view.ResponseMessage;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class HealthCheckControllerTest extends BaseControllerTest {

    @DisplayName("헬스 체크 시 200을 반환한다")
    @Test
    void healthCheck() {
        ApiResponse response = RestAssured.given()
                .contentType(ContentType.JSON)
                .when().get("/health")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(ApiResponse.class);

        assertAll(
                () -> assertThat(response.code()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.message()).isEqualTo(ResponseMessage.SUCCESS.getMessage())
        );
    }
}
