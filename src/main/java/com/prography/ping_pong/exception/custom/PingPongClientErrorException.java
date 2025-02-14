package com.prography.ping_pong.exception.custom;

import com.prography.ping_pong.exception.errorcode.ClientErrorCode;

public class PingPongClientErrorException extends PingPongException {

    public PingPongClientErrorException(ClientErrorCode clientErrorCode) {
        super(clientErrorCode.getMessage(), clientErrorCode.getStatus());
    }
}
