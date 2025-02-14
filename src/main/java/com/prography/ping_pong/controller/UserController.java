package com.prography.ping_pong.controller;

import com.prography.ping_pong.dto.request.user.UserInitializeRequest;
import com.prography.ping_pong.dto.response.ApiResponse;
import com.prography.ping_pong.service.user.client.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/init")
    public ResponseEntity<ApiResponse> initialize(UserInitializeRequest request) {
        ApiResponse response = userService.initialize(request);
        return ResponseEntity.ok(response);
    }
}
