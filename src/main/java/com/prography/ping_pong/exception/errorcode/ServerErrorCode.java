package com.prography.ping_pong.exception.errorcode;

import com.prography.ping_pong.view.ResponseMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ServerErrorCode implements ErrorCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ResponseMessage.SERVER_ERROR),
    ;

    private final HttpStatus status;
    private final ResponseMessage message;
}
