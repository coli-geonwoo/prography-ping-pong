package com.prography.pingpong.exception.custom;

import com.prography.pingpong.view.ResponseMessage;
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
