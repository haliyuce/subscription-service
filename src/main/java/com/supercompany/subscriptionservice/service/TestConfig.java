package com.supercompany.subscriptionservice.service;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

@TestConfiguration
public class TestConfig {

    @Bean
    public Clock clock() {
        final LocalDateTime NOW = LocalDateTime.of(2021, 1, 20, 12, 00);
        return Clock.fixed(NOW.toInstant(ZoneOffset.UTC), ZoneId.systemDefault());
    }
}
