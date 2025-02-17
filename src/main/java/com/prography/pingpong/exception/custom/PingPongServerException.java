package com.prography.pingpong.exception.custom;


import com.prography.pingpong.exception.errorcode.ServerErrorCode;

public class PingPongServerException extends PingPongException {

    public PingPongServerException(ServerErrorCode serverErrorCode) {
        super(serverErrorCode.getMessage(), serverErrorCode.getStatus());
    }
}
