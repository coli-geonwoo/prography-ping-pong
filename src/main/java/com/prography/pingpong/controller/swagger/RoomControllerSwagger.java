package com.prography.pingpong.controller.swagger;

import com.prography.pingpong.controller.swagger.SwaggerResponse.SwaggerRoomDetailResponse;
import com.prography.pingpong.controller.swagger.SwaggerResponse.SwaggerRoomPageResponse;
import com.prography.pingpong.controller.swagger.annotation.ErrorCode201;
import com.prography.pingpong.controller.swagger.annotation.ErrorCode500;
import com.prography.pingpong.dto.request.room.RoomAttendRequest;
import com.prography.pingpong.dto.request.room.RoomCreateRequest;
import com.prography.pingpong.dto.request.room.RoomExitRequest;
import com.prography.pingpong.dto.request.room.RoomStartRequest;
import com.prography.pingpong.dto.response.ApiBodyResponse;
import com.prography.pingpong.dto.response.ApiResponse;
import com.prography.pingpong.dto.response.room.RoomDetailResponse;
import com.prography.pingpong.dto.response.room.RoomPageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

public interface RoomControllerSwagger {

    @Operation(
            summary = "방 생성",
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = RoomCreateRequest.class))),
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
            parameters = {@Parameter(name = "roomId", description = "참여할 방 아이디")},
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = RoomAttendRequest.class))),
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
            parameters = {@Parameter(name = "roomId", description = "조회할 방 아이디")},
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "방 상세 조회 성공",
                            content = @Content(schema = @Schema(oneOf = SwaggerRoomDetailResponse.class))
                    )
            }
    )
    @ErrorCode500
    ApiBodyResponse<RoomDetailResponse> findRoom(long roomId);

    @Operation(
            summary = "방 전체 조회",
            parameters = {
                    @Parameter(name = "size", description = "조회할 페이징 사이즈"),
                    @Parameter(name = "page", description = "조회 페이지 번호")
            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "방 정보 조회 성공",
                            content = @Content(schema = @Schema(implementation = SwaggerRoomPageResponse.class))
                    )
            }
    )
    @ErrorCode500
    ApiBodyResponse<RoomPageResponse> findAllRooms(int size, int page);

    @Operation(
            summary = "게임 시작",
            parameters = {@Parameter(name = "roomId", description = "시작할 방 아이디")},
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = RoomStartRequest.class))),
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
            parameters = {@Parameter(name = "roomId", description = "퇴장할 방 아이디")},
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = RoomExitRequest.class))),
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
