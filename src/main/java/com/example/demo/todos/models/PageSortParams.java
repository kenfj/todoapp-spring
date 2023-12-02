package com.example.demo.todos.models;

import static com.example.demo.utils.StringUtils.camelToSnake;
import static java.lang.String.join;
import static java.util.Optional.empty;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

public record PageSortParams(
        Optional<Integer> _page,
        Optional<Integer> _limit,
        Optional<List<String>> _sort,
        Optional<List<Direction>> _order) {

    private int page() {
        return _page.map(x -> x - 1).orElse(0);
    }

    private int size() {
        return _limit.orElse(10);
    }

    private List<String> sortColumns() {
        if (!_sort.isPresent())
            return Collections.emptyList();

        return _sort.get();
    }

    private List<Direction> sortDirections() {
        var sortColumnsSize = sortColumns().size();

        if (!_order.isPresent())
            return Collections.nCopies(sortColumnsSize, Sort.DEFAULT_DIRECTION);

        var range = IntStream.range(0, sortColumnsSize).boxed();

        return range.map(i -> i < _order.get().size()
                ? _order.get().get(i)
                : Sort.DEFAULT_DIRECTION)
                .toList();
    }

    private Sort sort() {
        if (sortColumns().isEmpty())
            return Sort.unsorted();

        var range = IntStream.range(0, sortColumns().size()).boxed();

        var orders = range.map(i -> new Sort.Order(
                sortDirections().get(i),
                sortColumns().get(i)))
                .toList();

        return Sort.by(orders);
    }

    public Pageable toPageable() {
        return PageRequest.of(page(), size(), sort());
    }

    public static PageSortParams emptyPageSortParams() {
        return new PageSortParams(empty(), empty(), empty(), empty());
    }

    public String toOrderBySql() {
        var pageable = toPageable();

        var orderBys = pageable.getSort().map(x -> camelToSnake(x.getProperty()) + " " + x.getDirection());
        var orderBy = orderBys.isEmpty() ? "" : " ORDER BY " + join(", ", orderBys);

        var paging = " OFFSET " + pageable.getOffset() + " ROWS"
                + " FETCH NEXT " + pageable.getPageSize() + " ROWS ONLY";

        return orderBy + paging;
    }
}
