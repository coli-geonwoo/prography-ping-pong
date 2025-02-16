package com.prography.ping_pong.util;

import com.prography.ping_pong.exception.custom.PingPongServerException;
import com.prography.ping_pong.exception.errorcode.ServerErrorCode;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionRunner {

    @Transactional
    public void runWithTransaction(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            throw new PingPongServerException(ServerErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
