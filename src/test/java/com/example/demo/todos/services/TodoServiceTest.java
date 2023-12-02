package com.example.demo.todos.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.example.demo.todos.models.Todo;

import jakarta.validation.ConstraintViolationException;

@SpringBootTest
@DirtiesContext
class TodoServiceTest {

    @Autowired
    private TodoService service;

    @Test
    void contextLoads() throws Exception {
        assertThat(service).isNotNull();
    }

    @Test
    void findAll_ok() {

        var todos = service.findAll();

        assertEquals(3, todos.size());
    }

    @Test
    void findById_ok() {

        Optional<Todo> maybeTodo = service.findById(3L);

        assertTrue(maybeTodo.isPresent());
        maybeTodo.ifPresent(todo -> assertEquals("Baz", todo.title()));
        maybeTodo.ifPresent(todo -> assertEquals(true, todo.completed()));
    }

    @Test
    void findById_not_found() {

        Optional<Todo> maybeTodo = service.findById(99999999L);

        assertFalse(maybeTodo.isPresent());
    }

    @Test
    void create_ok() {

        var newTodo = Todo.of(null, "Hoge");

        var savedTodo = service.create(newTodo);

        var foundTodo = service.findById(savedTodo.id()).get();
        assertEquals(foundTodo.id(), savedTodo.id());
        assertEquals("Hoge", savedTodo.title());
    }

    @Test
    void create_err() {

        var newTodo = Todo.of(123L, "Hoge");

        var ex = assertThrows(IllegalArgumentException.class, () -> {
            service.create(newTodo);
        });

        var expected = "Invalid create id: 123";
        assertEquals(expected, ex.getMessage());
    }

    @Test
    void update_ok() {

        var todo = service.findById(1L).get();
        var modifiedTodo = new Todo(todo.id(), todo.title(), !todo.completed());

        var savedTodo = service.update(modifiedTodo);

        assertEquals(1L, savedTodo.id());
        assertEquals("Foo", savedTodo.title());
        assertEquals(true, savedTodo.completed());
    }

    @Test
    void update_err() {

        var todo = Todo.of(123L, "Hoge");

        var ex = assertThrows(NoSuchElementException.class, () -> {
            service.update(todo);
        });

        var expected = "Invalid update id: 123";
        assertEquals(expected, ex.getMessage());
    }

    @Test
    void patch_ok() throws ReflectiveOperationException, MethodArgumentNotValidException {

        var map = Map.of(
                "title", (Object) "Fuga",
                "completed", (Object) true);

        var savedTodo = service.patch(1L, map);

        assertEquals(1L, savedTodo.id());
        assertEquals("Fuga", savedTodo.title());
        assertEquals(true, savedTodo.completed());
    }

    @Test
    void patch_err() {

        var map = Map.of("title", (Object) "Fuga");

        var ex = assertThrows(NoSuchElementException.class, () -> {
            service.patch(123L, map);
        });

        var expected = "Invalid patch id: 123";
        assertEquals(expected, ex.getMessage());
    }

    @Test
    void patch_validation_err() {

        var map = Map.of("title", (Object) "a");

        var ex = assertThrows(ConstraintViolationException.class, () -> {
            service.patch(2L, map);
        });

        var expected = "title: size must be between 2 and 30";
        assertEquals(expected, ex.getMessage());
    }

    @Test
    void delete_ok() {

        var newTodo = Todo.of(null, "Hoge");
        var savedTodo = service.create(newTodo);

        service.deleteById(savedTodo.id());

        var todo = service.findById(savedTodo.id());
        assertFalse(todo.isPresent());
    }

    @Test
    void delete_err() {

        var ex = assertThrows(NoSuchElementException.class, () -> {
            service.deleteById(123L);
        });

        var expected = "Invalid delete id: 123";
        assertEquals(expected, ex.getMessage());
    }
}
