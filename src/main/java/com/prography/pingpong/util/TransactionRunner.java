package com.prography.pingpong.util;

import com.prography.pingpong.exception.custom.PingPongServerException;
import com.prography.pingpong.exception.errorcode.ServerErrorCode;
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
