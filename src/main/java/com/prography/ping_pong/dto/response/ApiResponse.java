package com.prography.ping_pong.dto.response;

import com.prography.ping_pong.view.ResponseMessage;
import org.springframework.http.HttpStatus;

public record ApiResponse(
        Integer code,
        String message
) {

    public static ApiResponse ok() {
        return new ApiResponse(HttpStatus.OK.value(), ResponseMessage.SUCCESS.getValue());
    }
}
