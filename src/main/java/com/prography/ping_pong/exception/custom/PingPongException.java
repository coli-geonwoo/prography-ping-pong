package com.prography.ping_pong.exception.custom;

import com.prography.ping_pong.view.ResponseMessage;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class PingPongException extends RuntimeException {

    private final HttpStatus httpStatus;

    protected PingPongException(ResponseMessage errorMessage, HttpStatus httpStatus) {
        super(errorMessage.getValue());
        this.httpStatus = httpStatus;
    }
}
