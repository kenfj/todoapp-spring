package com.example.demo.configs;

import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// deserialize-enum-ignoring-case-in-spring-boot-controller
// https://stackoverflow.com/questions/50231233

@Configuration
public class EnumMappingConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        ApplicationConversionService.configure(registry);
    }
}
