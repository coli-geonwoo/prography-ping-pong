package com.prography.pingpong.controller.swagger.annotation;

import com.prography.pingpong.dto.request.user.UserInitializeRequest;
import com.prography.pingpong.dto.response.ApiBodyResponse;
import com.prography.pingpong.dto.response.ApiResponse;
import com.prography.pingpong.dto.response.user.UserPageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "User API")
public interface UserControllerSwagger {

    @Operation(
            summary = "초기화 API",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "유저 초기화 성공",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class))
                    )
            }
    )
    @ErrorCode500
    ApiResponse initialize(UserInitializeRequest request);

    @Operation(
            summary = "유저 전체 조회",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "유저 정보 조회 성공",
                            content = @Content(schema = @Schema(implementation = ApiBodyResponse.class))
                    )
            }
    )
    @ErrorCode500
    ApiBodyResponse<UserPageResponse> findUsers(int size, int page);
}
