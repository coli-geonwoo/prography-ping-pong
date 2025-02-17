package com.prography.pingpong.exception.errorcode;

import com.prography.pingpong.view.ResponseMessage;
import org.springframework.http.HttpStatus;

public interface ErrorCode {

    HttpStatus getStatus();

    ResponseMessage getMessage();
}
