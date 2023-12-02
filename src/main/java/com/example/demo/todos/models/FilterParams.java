package com.example.demo.todos.models;

import static java.util.Optional.empty;

import java.time.Instant;
import java.util.Optional;

import com.example.demo.enums.QueryType;

public record FilterParams(
        Optional<Long> id,
        Optional<QueryType> queryType,
        Optional<Instant> loggedAt,
        Optional<Long> todoId,
        Optional<String> title,
        Optional<Boolean> completed) {

    public TodoHistory toProbe() {
        return new TodoHistory(
                id.orElse(null),
                queryType.orElse(null),
                loggedAt.orElse(null),
                todoId.orElse(null),
                title.orElse(null),
                completed.orElse(null));
    }

    public static FilterParams emptyFilterParams() {
        return new FilterParams(
                empty(),
                empty(),
                empty(),
                empty(),
                empty(),
                empty());
    }
}
