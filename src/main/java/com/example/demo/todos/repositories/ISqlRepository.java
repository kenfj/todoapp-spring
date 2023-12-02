package com.example.demo.todos.repositories;

import java.util.List;

import org.springframework.jdbc.core.namedparam.SqlParameterSource;

public interface ISqlRepository<T> {

    List<T> findAllBySql(String findSql, SqlParameterSource params);

    Long countBySql(String countSql, SqlParameterSource params);

}
