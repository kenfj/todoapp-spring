package com.example.demo.todos.repositories;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.ListQueryByExampleExecutor;

import com.example.demo.todos.models.TodoHistory;

// Query by Example for paging sorting and filtering
// https://docs.spring.io/spring-data/jdbc/docs/current/reference/html/#query-by-example

public interface TodoHistoryRepository
        extends ListCrudRepository<TodoHistory, Long>,
        ListQueryByExampleExecutor<TodoHistory> {
}
