package com.prography.pingpong.controller.swagger.annotation;

import com.prography.pingpong.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Health Check API")
public interface HealthCheckControllerSwagger {

    @Operation(
            summary = "헬스 체크",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "헬스 체크 성공",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class))
                    )
            }
    )
    @ErrorCode500
    ApiResponse healthCheck();
}
