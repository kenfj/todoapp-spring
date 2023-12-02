package com.example.demo.todos.repositories;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.example.demo.todos.models.TodoHistory;

@Repository
public class TodoHistorySqlRepository implements ISqlRepository<TodoHistory> {

    private final Logger logger = LogManager.getLogger(TodoHistorySqlRepository.class);
    private final NamedParameterJdbcTemplate db;

    public TodoHistorySqlRepository(NamedParameterJdbcTemplate db) {
        this.db = db;
    }

    @Override
    public List<TodoHistory> findAllBySql(String findSql, SqlParameterSource params) {

        logger.info("findAllBySql: {} {}", findSql, params);

        var rowMapper = new DataClassRowMapper<>(TodoHistory.class);
        return db.query(findSql, params, rowMapper);
    }

    @Override
    public Long countBySql(String countSql, SqlParameterSource params) {

        logger.info("countBySql: {} {}", countSql, params);

        return db.queryForObject(countSql, params, Long.class);
    }

}
