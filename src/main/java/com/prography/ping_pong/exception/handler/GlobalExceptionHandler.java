package com.prography.ping_pong.exception.handler;

import com.prography.ping_pong.exception.ErrorResponse;
import com.prography.ping_pong.exception.custom.PingPongClientErrorException;
import com.prography.ping_pong.exception.custom.PingPongServerException;
import com.prography.ping_pong.exception.errorcode.ErrorCode;
import com.prography.ping_pong.exception.errorcode.ServerErrorCode;
import com.prography.ping_pong.view.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PingPongClientErrorException.class)
    public ResponseEntity<ErrorResponse> handleClientException(PingPongClientErrorException exception) {
        log.warn("message: {}", exception.getMessage());
        return toResponse(exception.getHttpStatus(), exception.getMessage());
    }

    @ExceptionHandler(PingPongServerException.class)
    public ResponseEntity<ErrorResponse> handleServerException(PingPongServerException exception) {
        log.warn("message: {}", exception.getMessage());
        return toResponse(exception.getHttpStatus(), exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {
        log.error("exception: {}", exception);
        return toResponse(ServerErrorCode.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> toResponse(ErrorCode errorCode) {
        ResponseMessage responseMessage = errorCode.getMessage();
        return toResponse(errorCode.getStatus(), responseMessage.getValue());
    }

    private ResponseEntity<ErrorResponse> toResponse(HttpStatus httpStatus, String message) {
        ErrorResponse errorResponse = new ErrorResponse(httpStatus.value(), message);
        return ResponseEntity.status(httpStatus)
                .body(errorResponse);
    }
}
