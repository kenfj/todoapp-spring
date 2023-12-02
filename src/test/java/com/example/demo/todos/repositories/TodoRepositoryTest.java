package com.example.demo.todos.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

@DataJdbcTest
class TodoRepositoryTest {

    @Autowired
    private TodoRepository repository;

    // Note: repository interface will not be in the coverage report
    @Test
    void findByTitleLike_ok() {

        var todos = repository.findByTitleLike("%a%");

        assertEquals(2L, todos.size());
        assertEquals("Bar", todos.get(0).title());
        assertEquals("Baz", todos.get(1).title());
    }

    @Test
    void findBySql_ok() {

        var todos = repository.findBySql("%a%");

        assertEquals(2L, todos.size());
        assertEquals("Bar", todos.get(0).title());
        assertEquals("Baz", todos.get(1).title());
    }
}
