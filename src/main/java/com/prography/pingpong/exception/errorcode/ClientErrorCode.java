package com.prography.pingpong.exception.errorcode;

import com.prography.pingpong.view.ResponseMessage;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ClientErrorCode implements ErrorCode {

    INVALID_REQUEST(HttpStatus.CREATED, ResponseMessage.CLIENT_ERROR),
    ;

    private final HttpStatus status;
    private final ResponseMessage message;

    ClientErrorCode(HttpStatus status, ResponseMessage message) {
        this.status = status;
        this.message = message;
    }
}
