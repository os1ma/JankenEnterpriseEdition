package com.example.janken.application.service.health;

import com.example.janken.application.query.health.HealthQueryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class HealthApplicationService {

    private HealthQueryService healthQueryService;

    public boolean isHealthy() {
        return healthQueryService.isHealthy();
    }

}
