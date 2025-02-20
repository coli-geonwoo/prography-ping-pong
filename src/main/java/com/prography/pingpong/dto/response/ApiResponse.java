package com.prography.pingpong.dto.response;

import com.prography.pingpong.view.ResponseMessage;
import org.springframework.http.HttpStatus;

public record ApiResponse(
        long code,
        String message
) {

    public static ApiResponse ok() {
        return new ApiResponse(HttpStatus.OK.value(), ResponseMessage.SUCCESS.getValue());
    }
}
