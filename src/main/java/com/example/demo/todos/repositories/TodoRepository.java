package com.example.demo.todos.repositories;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.example.demo.todos.models.Todo;

public interface TodoRepository extends CrudRepository<Todo, Long> {

    public final String FIND_BY_TITLE_LIKE_SQL = """
                SELECT *
                FROM todo
                WHERE title like :title
            """;

    @Query(FIND_BY_TITLE_LIKE_SQL)
    List<Todo> findByTitleLike(@Param("title") String title);
}
