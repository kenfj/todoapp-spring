package com.example.demo.todos.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

class PageSortParamsTest {

    @Test
    void totoPageable_ok() {

        var pageSortParams = new PageSortParams(
                Optional.of(1),
                Optional.of(3),
                Optional.of(List.of("title")),
                Optional.of(List.of(Direction.DESC)));

        var expected = PageRequest.of(0, 3, Direction.DESC, "title");
        var actual = pageSortParams.toPageable();

        assertEquals(expected, actual);
    }

    @Test
    void toSort_ok() {

        var pageSortParams = new PageSortParams(
                Optional.empty(),
                Optional.empty(),
                Optional.of(List.of("title", "completed")),
                Optional.of(List.of(Direction.DESC)));

        var expectedSort = Sort.by(
                Order.desc("title"),
                Order.asc("completed"));
        var expected = PageRequest.of(0, 10, expectedSort);

        var actual = pageSortParams.toPageable();

        assertEquals(expected, actual);
    }

    @Test
    void toOrderBySql_2cols_ok() {

        var pageSortParams = new PageSortParams(
                Optional.empty(),
                Optional.empty(),
                Optional.of(List.of("title", "completed")),
                Optional.of(List.of(Direction.DESC)));

        var expected = " ORDER BY title DESC, completed ASC OFFSET 0 ROWS FETCH NEXT 10 ROWS ONLY";
        var actual = pageSortParams.toOrderBySql();

        assertEquals(expected, actual);
    }

    @Test
    void toOrderBySql_default_ok() {

        var pageSortParams = new PageSortParams(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty());

        var expected = " OFFSET 0 ROWS FETCH NEXT 10 ROWS ONLY";
        var actual = pageSortParams.toOrderBySql();

        assertEquals(expected, actual);
    }
}
