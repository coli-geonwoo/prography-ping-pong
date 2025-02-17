package com.prography.pingpong.controller;

import com.prography.pingpong.controller.swagger.UserControllerSwagger;
import com.prography.pingpong.dto.request.user.UserInitializeRequest;
import com.prography.pingpong.dto.response.ApiBodyResponse;
import com.prography.pingpong.dto.response.ApiResponse;
import com.prography.pingpong.dto.response.user.UserPageResponse;
import com.prography.pingpong.service.user.UserFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController implements UserControllerSwagger {

    private final UserFacadeService userFacadeService;

    @PostMapping("/init")
    public ApiResponse initialize(@RequestBody UserInitializeRequest request) {
        return userFacadeService.initialize(request);
    }

    @GetMapping("/user")
    public ApiBodyResponse<UserPageResponse> findUsers(
            @RequestParam(name = "size") int size,
            @RequestParam(name = "page") int page
    ) {
        Pageable pageable = PageRequest.of(page, size);
        UserPageResponse pageResponse = userFacadeService.findAll(pageable);
        return ApiBodyResponse.ok(pageResponse);
    }
}
