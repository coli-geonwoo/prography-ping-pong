package com.prography.pingpong.exception.custom;

import com.prography.pingpong.exception.errorcode.ClientErrorCode;

public class PingPongClientErrorException extends PingPongException {

    public PingPongClientErrorException(ClientErrorCode clientErrorCode) {
        super(clientErrorCode.getMessage(), clientErrorCode.getStatus());
    }
}
