package com.prography.ping_pong.exception.errorcode;

import com.prography.ping_pong.view.ResponseMessage;
import org.springframework.http.HttpStatus;

public interface ErrorCode {

    HttpStatus getStatus();

    ResponseMessage getMessage();
}
