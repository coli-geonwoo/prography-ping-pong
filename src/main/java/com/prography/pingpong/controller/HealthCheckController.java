package com.prography.pingpong.controller;

import com.prography.pingpong.controller.swagger.annotation.HealthCheckSwagger;
import com.prography.pingpong.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController implements HealthCheckSwagger {

    @GetMapping("/health")
    public ResponseEntity<ApiResponse> healthCheck() {
        ApiResponse response = ApiResponse.ok();
        return ResponseEntity.ok(response);
    }
}
