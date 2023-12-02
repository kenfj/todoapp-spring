package com.example.demo.todos.services;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.example.demo.todos.models.Todo;
import com.example.demo.todos.repositories.TodoRepository;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;

// Validating In Service Layer
// https://naturalprogrammer.teachable.com/courses/332639/lectures/5402564

@Service
@Validated
public class TodoService {

    private final TodoRepository repository;
    private final Validator validator;

    public TodoService(TodoRepository repository, Validator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    public List<Todo> findAll() {
        return repository.findAll();
    }

    public Optional<Todo> findById(Long id) {
        return repository.findById(id);
    }

    public Todo create(@Valid Todo todo) {
        if (todo.id() != null)
            throw new IllegalArgumentException("Invalid create id: " + todo.id());

        return repository.save(todo);
    }

    public Todo update(@Valid Todo todo) {

        var originTodo = repository.findById(todo.id())
                .orElseThrow(() -> new NoSuchElementException("Invalid update id: " + todo.id()));

        // fix CreatedDate set to null on update
        // https://stackoverflow.com/questions/73051733
        var updatingTodo = todo.withCreatedAt(originTodo.createdAt());

        return repository.save(updatingTodo);
    }

    @Transactional
    public Todo patch(Long id, Map<String, Object> fields) throws ReflectiveOperationException {

        if (fields.containsKey("id"))
            throw new IllegalArgumentException("Invalid fields key: id");

        var todo = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Invalid patch id: " + id));

        var patchedTodo = todo.patch(fields);

        var violations = validator.validate(patchedTodo);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        return repository.save(patchedTodo);
    }

    public void deleteById(Long id) {
        if (!repository.existsById(id))
            throw new NoSuchElementException("Invalid delete id: " + id);

        repository.deleteById(id);
    }
}
