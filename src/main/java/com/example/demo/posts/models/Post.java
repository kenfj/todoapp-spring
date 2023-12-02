package com.example.demo.posts.models;

public record Post(
        Long id,
        Long userId,
        String title,
        String body) {
}
