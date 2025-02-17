package com.prography.pingpong.dto.request.user;

public record UserInitializeRequest(
        int seed,
        int quantity
) {

}
