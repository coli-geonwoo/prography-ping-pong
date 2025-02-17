package com.prography.pingpong.prography;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import com.prography.pingpong.common.BaseControllerTest;
import com.prography.pingpong.dto.request.user.UserInitializeRequest;
import com.prography.pingpong.dto.response.ApiResponse;
import com.prography.pingpong.view.ResponseMessage;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.http.HttpStatus;

public class PublicScenarioTest extends BaseControllerTest {

    @DisplayName("공개 시나리오 테스트")
    @TestFactory
    Stream<DynamicTest> scenarioTest() {
        return Stream.of(
                dynamicTest("1. 헬스 체크 시 정상응답을 반환한다", () -> {
                    ApiResponse response = RestAssured.given()
                            .contentType(ContentType.JSON)
                            .when().get("/health")
                            .then()
                            .statusCode(HttpStatus.OK.value())
                            .extract()
                            .as(ApiResponse.class);

                    assertAll(
                            () -> assertThat(response.code()).isEqualTo(HttpStatus.OK.value()),
                            () -> assertThat(response.message()).isEqualTo(ResponseMessage.SUCCESS.getValue())
                    );
                }),
                dynamicTest("2. 유저 전체를 조회한다.", () -> {
                    RestAssured.given()
                            .contentType(ContentType.JSON)
                            .queryParam("size", 1)
                            .queryParam("page", 1)
                            .when().get("/user")
                            .then()
                            .statusCode(HttpStatus.OK.value());
                }),
                dynamicTest("3. 유저를 초기화한다.", () -> {
                    UserInitializeRequest request = new UserInitializeRequest(123, 10);

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
                }),
                dynamicTest("4. 유저 전체를 조회한다", () -> {
                    RestAssured.given()
                            .contentType(ContentType.JSON)
                            .queryParam("size", 1)
                            .queryParam("page", 1)
                            .when().get("/user")
                            .then()
                            .statusCode(HttpStatus.OK.value());
                })
        );
    }
}
