package com.prography.pingpong.controller;

import com.prography.pingpong.controller.swagger.HealthCheckControllerSwagger;
import com.prography.pingpong.dto.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController implements HealthCheckControllerSwagger {

    @GetMapping("/health")
    public ApiResponse healthCheck() {
        return ApiResponse.ok();
    }
}
