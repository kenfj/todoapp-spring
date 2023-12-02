package com.example.demo.todos.models;

import static com.example.demo.enums.QueryType.CREATE;
import static com.example.demo.enums.QueryType.DELETE;
import static com.example.demo.enums.QueryType.UPDATE;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;

import com.example.demo.enums.QueryType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value = { "createdAt", "updatedAt" }, allowGetters = true)
public record TodoHistory(
        @Id Long id,
        QueryType queryType,
        @CreatedDate Instant loggedAt,
        Long todoId,
        String title,
        Boolean completed) {

    public static TodoHistory ofSave(Todo todo) {

        var queryType = todo.createdAt() != null
                && todo.createdAt().equals(todo.updatedAt())
                        ? CREATE
                        : UPDATE;

        return new TodoHistory(null, queryType, null,
                todo.id(), todo.title(), todo.completed());
    }

    public static TodoHistory ofDelete(Long todoId) {

        var queryType = DELETE;

        return new TodoHistory(null, queryType, null,
                todoId, null, null);
    }

    // used as empty filter to select all
    public static TodoHistory ofNull() {
        return new TodoHistory(null, null, null,
                null, null, null);
    }
}
