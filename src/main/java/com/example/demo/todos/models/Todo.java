package com.example.demo.todos.models;

import static com.example.demo.utils.RecordUtil.patchRecord;

import java.util.Map;

import org.springframework.data.annotation.Id;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// c.f. todos in https://jsonplaceholder.typicode.com/

public record Todo(
        @Id Long id,
        @NotBlank @Size(min = 2, max = 10) String title,
        Boolean completed) {

    public static Todo of(Long id, String title) {
        return new Todo(id, title, false);
    }

    public Todo patch(Map<String, Object> fields) throws ReflectiveOperationException {
        return patchRecord(Todo.class, this, fields);
    }
}
