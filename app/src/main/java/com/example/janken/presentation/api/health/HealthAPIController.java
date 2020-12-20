package com.example.janken.presentation.api.health;

import com.example.janken.application.service.health.HealthApplicationService;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
@AllArgsConstructor
public class HealthAPIController {

    private HealthApplicationService service;

    @GetMapping
    public ResponseEntity<HealthResponseBody> get() {
        val isHealthy = service.isHealthy();

        val status = isHealthy ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;
        val responseBody = new HealthResponseBody(status.value());
        return ResponseEntity.ok(responseBody);
    }

}
