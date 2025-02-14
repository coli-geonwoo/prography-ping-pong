package com.prography.ping_pong.dto.request.user;

public record UserInitializeRequest(
        int seed,
        int quantity
) {

}
