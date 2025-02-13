package com.prography.ping_pong.view;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseMessage {
    SUCCESS("API 요청이 성공했습니다."),
    CLIENT_ERROR("불가능한 요청입니다."),
    SERVER_ERROR("에러가 발생했습니다.")
    ;

    private final String message;
}
