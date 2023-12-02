package com.example.demo.todos.repositories;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import com.example.demo.todos.models.Todo;

public interface TodoRepository extends ListCrudRepository<Todo, Long> {

    List<Todo> findByTitleLike(@Param("title") String title);

    final String FIND_BY_SQL = """
                SELECT *
                FROM todo
                WHERE title like :title
            """;

    // same as findByTitleLike but by raw sql for demo
    @Query(FIND_BY_SQL)
    List<Todo> findBySql(@Param("title") String title);
}
