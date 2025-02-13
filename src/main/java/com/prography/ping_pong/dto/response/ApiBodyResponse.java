package com.prography.ping_pong.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApiBodyResponse<T> {

    private final Integer code;
    private final String message;
    private final T result;
}
