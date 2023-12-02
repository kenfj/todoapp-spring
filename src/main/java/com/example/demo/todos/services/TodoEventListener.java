package com.example.demo.todos.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.relational.core.mapping.event.AbstractRelationalEventListener;
import org.springframework.data.relational.core.mapping.event.AfterDeleteEvent;
import org.springframework.data.relational.core.mapping.event.AfterSaveEvent;
import org.springframework.stereotype.Component;

import com.example.demo.todos.models.Todo;
import com.example.demo.todos.models.TodoHistory;
import com.example.demo.todos.repositories.TodoHistoryRepository;

// https://docs.spring.io/spring-data/jdbc/docs/current/reference/html/#jdbc.events

@Component
public class TodoEventListener extends AbstractRelationalEventListener<Todo> {

    private final Logger logger = LogManager.getLogger(TodoEventListener.class);
    private final TodoHistoryRepository repository;

    public TodoEventListener(TodoHistoryRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void onAfterSave(AfterSaveEvent<Todo> event) {

        var todo = event.getEntity();

        if (todo == null) {
            logger.warn("onAfterSave: todo is null");
            return;
        }

        logger.info("onAfterSave: {}", todo);

        var todoHistory = TodoHistory.ofSave(todo);

        repository.save(todoHistory);
    }

    @Override
    protected void onAfterDelete(AfterDeleteEvent<Todo> event) {

        var todoId = (Long) event.getId().getValue();

        logger.info("onAfterDelete: {}", todoId);

        var todoHistory = TodoHistory.ofDelete(todoId);

        repository.save(todoHistory);
    }
}
