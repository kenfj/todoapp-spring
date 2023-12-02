package com.example.demo.todos.models;

// Validating In Service Layer
// https://naturalprogrammer.teachable.com/courses/332639/lectures/5402564

public record FieldError(
        String field,
        String code,
        String message) {
}
