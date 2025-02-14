package com.prography.ping_pong.controller;

import com.prography.ping_pong.dto.request.user.UserInitializeRequest;
import com.prography.ping_pong.dto.response.ApiBodyResponse;
import com.prography.ping_pong.dto.response.ApiResponse;
import com.prography.ping_pong.dto.response.user.UserPageResponse;
import com.prography.ping_pong.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/init")
    public ResponseEntity<ApiResponse> initialize(@RequestBody UserInitializeRequest request) {
        ApiResponse response = userService.initialize(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user")
    public ResponseEntity<ApiBodyResponse<UserPageResponse>> findUsers(
            @RequestParam(name = "size") int size,
            @RequestParam(name = "page") int page
    ) {
        Pageable pageable = PageRequest.of(page, size);
        UserPageResponse pageResponse = userService.findAll(pageable);
        ApiBodyResponse<UserPageResponse> response = ApiBodyResponse.ok(pageResponse);
        return ResponseEntity.ok(response);
    }
}
