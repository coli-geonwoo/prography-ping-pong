package com.prography.pingpong.controller.swagger.annotation;

import com.prography.pingpong.dto.request.room.TeamChangeRequest;
import com.prography.pingpong.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "User Room API")
public interface UserRoomControllerSwagger {

    @Operation(
            summary = "팀 변경",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "팀 변경 성공",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class))
                    )
            }
    )
    @ErrorCode201(description = "변경을 시도한 유저가 해당 방에 참여하지 않았을 때")
    @ErrorCode201(description = "변경을 시도한 팀의 정원이 가득찼을 때")
    @ErrorCode201(description = "존재하지 않는 id에 대한 요청일 때")
    @ErrorCode201(description = "참여하고자 하는 방이 대기상태가 아닐 때")
    @ErrorCode500
    ApiResponse changeTeam(TeamChangeRequest request, long roomId);
}
