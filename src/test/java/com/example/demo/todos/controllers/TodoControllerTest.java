package com.example.demo.todos.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.todos.models.Todo;

// https://spring.io/guides/gs/testing-web/

@SpringBootTest
@DirtiesContext
class TodoControllerTest {

    @Autowired
    private TodoController controller;

    @Test
    public void contextLoads() throws Exception {
        assertThat(controller).isNotNull();
    }

    @Test
    void findAll_ok() {

        var todos = controller.findAll();

        assertEquals(3, todos.spliterator().getExactSizeIfKnown());
    }

    @Test
    void findById_ok() {

        var todo = controller.findById(3L);

        assertEquals("Baz", todo.title());
        assertEquals(true, todo.completed());
    }

    @Test
    void insert_ok() {

        var newTodo = Todo.of(null, "Foo");
        var savedTodo = controller.create(newTodo);

        assertEquals(4L, savedTodo.id());
        assertEquals("Foo", savedTodo.title());
    }

    @Test
    @Transactional
    void update_ok() {

        var todo = new Todo(3L, "Hoge", true);

        var updated = controller.update(3L, todo);

        assertEquals(3L, updated.id());
        assertEquals("Hoge", updated.title());
        assertEquals(true, updated.completed());
    }
}
