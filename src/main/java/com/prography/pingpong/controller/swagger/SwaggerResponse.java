package com.prography.pingpong.controller.swagger;

import com.prography.pingpong.dto.response.ApiBodyResponse;
import com.prography.pingpong.dto.response.room.RoomDetailResponse;
import com.prography.pingpong.dto.response.room.RoomPageResponse;
import com.prography.pingpong.dto.response.user.UserPageResponse;

public class SwaggerResponse {

    public class SwaggerUserPageResponse extends ApiBodyResponse<UserPageResponse> {
        public UserPageResponse result;
    }

    public class SwaggerRoomDetailResponse extends ApiBodyResponse<RoomDetailResponse>{
        public RoomDetailResponse result;
    }

    public class SwaggerRoomPageResponse extends ApiBodyResponse<RoomPageResponse> {
        public RoomPageResponse result;
    }
}
