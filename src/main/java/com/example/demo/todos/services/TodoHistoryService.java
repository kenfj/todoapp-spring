package com.example.demo.todos.services;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import com.example.demo.todos.models.FilterParams;
import com.example.demo.todos.models.MapParams;
import com.example.demo.todos.models.PageSortParams;
import com.example.demo.todos.models.TodoHistory;
import com.example.demo.todos.repositories.ISqlRepository;
import com.example.demo.todos.repositories.TodoHistoryRepository;

@Service
public class TodoHistoryService {

    private final Logger logger = LogManager.getLogger(TodoHistoryService.class);
    private final TodoHistoryRepository repository;
    private final ISqlRepository<TodoHistory> sqlRepository;

    public TodoHistoryService(TodoHistoryRepository repository, ISqlRepository<TodoHistory> sqlRepository) {
        this.repository = repository;
        this.sqlRepository = sqlRepository;
    }

    public List<TodoHistory> findAll() {
        return repository.findAll();
    }

    public Page<TodoHistory> findAll(
            PageSortParams pageSortParams,
            FilterParams filterParams,
            MapParams mapParams) {

        logger.info("findAll pageSortParams: {}, filterParams: {}", pageSortParams, filterParams);

        if (mapParams.requireSqlQuery()) {
            return findBySql(pageSortParams, mapParams);
        } else {
            return findByExample(pageSortParams, filterParams);
        }
    }

    private Page<TodoHistory> findBySql(PageSortParams pageSortParams, MapParams mapParams) {

        var pageable = pageSortParams.toPageable();
        logger.info("findAll Pageable: {}", pageable);

        var where = mapParams.toWhere();
        var sqlParams = mapParams.toSqlParams();
        var orderBy = pageSortParams.toOrderBySql();

        var findSql = "SELECT * FROM todo_history WHERE " + where + orderBy;
        var todos = sqlRepository.findAllBySql(findSql, sqlParams);

        var countSql = "SELECT COUNT(*) FROM todo_history WHERE " + where;
        long count = sqlRepository.countBySql(countSql, sqlParams);

        return PageableExecutionUtils.getPage(todos, pageable, () -> count);
    }

    private Page<TodoHistory> findByExample(PageSortParams pageSortParams, FilterParams filterParams) {

        var pageable = pageSortParams.toPageable();
        logger.info("findAll Pageable: {}", pageable);

        var probe = filterParams.toProbe();
        logger.info("findAll Probe: {}", probe);

        var matcher = ExampleMatcher.matching()
                .withMatcher("title", m -> m.contains().ignoreCase());

        var pages = repository.findAll(Example.of(probe, matcher), pageable);
        var count = repository.count(Example.of(probe, matcher));

        return PageableExecutionUtils.getPage(pages.getContent(), pageable, () -> count);
    }

    public Optional<TodoHistory> findById(Long id) {
        return repository.findById(id);
    }
}
