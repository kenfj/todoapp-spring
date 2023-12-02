package com.example.demo.todos.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.domain.PageRequest;

import com.example.demo.todos.models.TodoHistory;

@DataJdbcTest
class TodoHistoryRepositoryTest {

    @Autowired
    private TodoHistoryRepository repository;

    @Test
    void findAll_ok() {

        var histories = repository.findAll();

        assertEquals(6L, histories.size());
        assertEquals("foo", histories.get(0).title());
        assertEquals("bar", histories.get(1).title());
    }

    @Test
    void findAll_pageable_ok() {

        var filter = TodoHistory.ofNull();
        var pageable = PageRequest.of(0, 10);
        var matcher = ExampleMatcher.matching()
                .withIgnoreCase()
                .withStringMatcher(StringMatcher.CONTAINING);

        var historiesPage = repository.findAll(Example.of(filter, matcher), pageable);
        var histories = IterableUtil.toArray(historiesPage, TodoHistory.class);

        assertEquals(6L, histories.length);
        assertEquals("foo", histories[0].title());
        assertEquals("bar", histories[1].title());
    }
}
