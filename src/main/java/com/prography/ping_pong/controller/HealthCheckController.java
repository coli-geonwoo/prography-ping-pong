package com.prography.ping_pong.controller;

import com.prography.ping_pong.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/health")
    public ResponseEntity<ApiResponse> healthCheck() {
        ApiResponse response = ApiResponse.ok();
        return ResponseEntity.ok(response);
    }
}
