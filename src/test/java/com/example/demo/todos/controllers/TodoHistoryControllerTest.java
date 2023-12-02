package com.example.demo.todos.controllers;

import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.data.domain.Sort.Direction.DESC;

import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.LinkedMultiValueMap;

import com.example.demo.todos.models.FilterParams;
import com.example.demo.todos.models.PageSortParams;

@SpringBootTest
class TodoHistoryControllerTest {

    @Autowired
    private TodoHistoryController controller;

    @Test
    void contextLoads() throws Exception {
        assertThat(controller).isNotNull();
    }

    @Test
    void findAll_ok() throws ReflectiveOperationException {

        var pageSortParams = PageSortParams.emptyPageSortParams();
        var filterParams = FilterParams.emptyFilterParams();
        var multiValueMap = new LinkedMultiValueMap<String, String>(Map.of());

        var todos = controller.findAll(pageSortParams, filterParams, multiValueMap);

        assertEquals(6, todos.spliterator().getExactSizeIfKnown());
    }

    @Test
    void findAll_filter() throws ReflectiveOperationException {

        var pageSortParams = new PageSortParams(
                of(1),
                of(3),
                of(List.of("title")),
                of(List.of(DESC)));
        var filterParams = FilterParams.emptyFilterParams();
        var multiValueMap = new LinkedMultiValueMap<String, String>(Map.of(
                "_page", List.of("1"),
                "_sort", List.of("title"),
                "_order", List.of("desc")));

        var todos = controller.findAll(pageSortParams, filterParams, multiValueMap);

        var todosCount = StreamSupport.stream(todos.spliterator(), false).count();

        assertEquals(3, todosCount);
    }

    @Test
    void findById_ok() throws ReflectiveOperationException {

        var todo = controller.findById(1L);

        assertEquals("foo", todo.title());
    }
}
