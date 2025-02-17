package com.prography.pingpong.controller.swagger.annotation;

import com.prography.pingpong.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Health Check API")
public interface HealthCheckSwagger {

    @Operation(
            summary = "로그 목록 조회",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "헬스 체크 성공",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class))
                    )
            }
    )
    @ErrorCode500
    ResponseEntity<ApiResponse> healthCheck();
}
