package com.example.demo.todos.services;

import static com.example.demo.enums.QueryType.CREATE;
import static com.example.demo.enums.QueryType.UPDATE;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.util.LinkedMultiValueMap;

import com.example.demo.todos.models.FilterParams;
import com.example.demo.todos.models.MapParams;
import com.example.demo.todos.models.PageSortParams;
import com.example.demo.todos.models.TodoHistory;

@SpringBootTest
class TodoHistoryServiceTest {

    @Autowired
    private TodoHistoryService service;

    @Test
    void findAll_ok() {

        var todos = service.findAll();

        assertEquals(6, todos.size());
    }

    @Test
    void findAll_findByExample_ok() throws ReflectiveOperationException {

        var pageSortParams = new PageSortParams(
                of(1),
                of(2),
                of(List.of("queryType")),
                of(List.of(Direction.DESC)));

        var filterParams = new FilterParams(
                empty(),
                empty(),
                empty(),
                empty(),
                of("baz"),
                empty());

        var multiValueMap = new LinkedMultiValueMap<String, String>(Map.of());
        var mapParams = new MapParams(multiValueMap);

        var hists = service.findAll(pageSortParams, filterParams, mapParams);

        var histories = new ArrayList<TodoHistory>();
        hists.iterator().forEachRemaining(histories::add);

        assertEquals(2, histories.size());
        assertEquals("baz", histories.get(0).title());
        assertEquals(UPDATE, histories.get(0).queryType());
        assertEquals("baz", histories.get(1).title());
        assertEquals(CREATE, histories.get(1).queryType());
    }

    @Test
    void findAll_findBySql_ok() throws ReflectiveOperationException {

        var pageSortParams = new PageSortParams(
                of(1),
                of(2),
                of(List.of("todoId")),
                of(List.of(Direction.DESC)));

        var filterParams = new FilterParams(
                empty(),
                empty(),
                empty(),
                empty(),
                of("baz"),
                empty());

        var initMap = Map.of("id_lte", List.of("3"));
        var multiValueMap = new LinkedMultiValueMap<String, String>(initMap);
        var mapParams = new MapParams(multiValueMap);

        var hists = service.findAll(pageSortParams, filterParams, mapParams);

        var histories = new ArrayList<TodoHistory>();
        hists.iterator().forEachRemaining(histories::add);

        assertEquals(2, histories.size());
        assertEquals("baz", histories.get(0).title());
        assertEquals(CREATE, histories.get(0).queryType());
        assertEquals("bar", histories.get(1).title());
        assertEquals(CREATE, histories.get(1).queryType());
    }
}
