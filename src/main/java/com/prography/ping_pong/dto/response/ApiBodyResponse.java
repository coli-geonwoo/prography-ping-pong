package com.prography.ping_pong.dto.response;

import com.prography.ping_pong.view.ResponseMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class ApiBodyResponse<T> {

    private final Integer code;
    private final String message;
    private final T result;

    public static <T> ApiBodyResponse<T> ok(T body) {
        return new ApiBodyResponse<>(
                HttpStatus.OK.value(),
                ResponseMessage.SUCCESS.getValue(),
                body
        );
    }
}
