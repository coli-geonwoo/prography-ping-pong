package com.prography.pingpong.controller.swagger.annotation;

import com.prography.pingpong.dto.request.room.RoomAttendRequest;
import com.prography.pingpong.dto.request.room.RoomCreateRequest;
import com.prography.pingpong.dto.request.room.RoomExitRequest;
import com.prography.pingpong.dto.request.room.RoomStartRequest;
import com.prography.pingpong.dto.response.ApiBodyResponse;
import com.prography.pingpong.dto.response.ApiResponse;
import com.prography.pingpong.dto.response.room.RoomDetailResponse;
import com.prography.pingpong.dto.response.room.RoomPageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface RoomControllerSwagger {

    @Operation(
            summary = "방 생성",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "방 생성 성공",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class))
                    )
            }
    )
    @ErrorCode201(description = "생성하려는 유저가 ACTIVE가 아닐 때")
    @ErrorCode201(description = "생성하려는 유저가 참여하고 있는 방이 있을 때")
    @ErrorCode500
    ApiResponse createRoom(RoomCreateRequest request);

    @Operation(
            summary = "방 참여",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "방 참여 성공",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class))
                    )
            }
    )
    @ErrorCode201(description = "생성하려는 유저가 ACTIVE가 아닐 때")
    @ErrorCode201(description = "생성하려는 유저가 참여하고 있는 방이 있을 때")
    @ErrorCode201(description = "참여하고자 하는 방이 가득찼을 때")
    @ErrorCode201(description = "참여하고자 하는 방이 대기상태가 아닐 때")
    @ErrorCode500
    ApiResponse attendRoom(long roomId, RoomAttendRequest request);

    @Operation(
            summary = "방 상세 조회",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "방 상세 조회 성공",
                            content = @Content(schema = @Schema(oneOf = RoomDetailResponse.class))
                    )
            }
    )
    @ErrorCode500
    ApiBodyResponse<RoomDetailResponse> findRoom(long roomId);

    @Operation(
            summary = "방 전체 조회",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "방 정보 조회 성공",
                            content = @Content(schema = @Schema(oneOf = RoomPageResponse.class))
                    )
            }
    )
    @ErrorCode500
    ApiBodyResponse<RoomPageResponse> findAllRooms(int size, int page);

    @Operation(
            summary = "게임 시작",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "게임 시작 성공",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class))
                    )
            }
    )
    @ErrorCode201(description = "시작하려는 유저가 방장이 아닐 때")
    @ErrorCode201(description = "존재하지 않은 방에 대한 요청일 때")
    @ErrorCode201(description = "시작하려는 방이 가득차지 않았을 때")
    @ErrorCode201(description = "참여하고자 하는 방이 대기상태가 아닐 때")
    @ErrorCode500
    ApiResponse startRoom(RoomStartRequest request, long roomId);

    @Operation(
            summary = "방 나가기",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "방 나가기 성공",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class))
                    )
            }
    )
    @ErrorCode201(description = "참여하지 않은 방을 나가려 할 때")
    @ErrorCode201(description = "대기가 아닌 방을 나가려 할 때")
    @ErrorCode201(description = "존재하지 않는 방에 대한 요청일 때")
    @ErrorCode500
    ApiResponse exitRoom(RoomExitRequest request, long roomId);
}
