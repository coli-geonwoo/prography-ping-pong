package com.prography.ping_pong.controller;

import com.prography.ping_pong.dto.response.ApiResponse;
import com.prography.ping_pong.view.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/health")
    public ResponseEntity<ApiResponse> healthCheck() {
        ApiResponse response = new ApiResponse(HttpStatus.OK.value(), ResponseMessage.SUCCESS.getMessage());
        return ResponseEntity.ok(response);
    }
}
