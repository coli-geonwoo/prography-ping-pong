package com.prography.ping_pong.exception.custom;


import com.prography.ping_pong.exception.errorcode.ServerErrorCode;

public class PingPongServerException extends PingPongException {

    public PingPongServerException(ServerErrorCode serverErrorCode) {
        super(serverErrorCode.getMessage(), serverErrorCode.getStatus());
    }
}
