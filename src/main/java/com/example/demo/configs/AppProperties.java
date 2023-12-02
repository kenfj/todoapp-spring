package com.example.demo.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app.custom")
public record AppProperties(
        String baseUrl) {
}
