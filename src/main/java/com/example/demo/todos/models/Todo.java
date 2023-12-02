package com.example.demo.todos.models;

import static com.example.demo.utils.RecordUtil.patchRecord;

import java.time.Instant;
import java.util.Map;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.InsertOnlyProperty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// c.f. todos in https://jsonplaceholder.typicode.com/

// Spring Data JDBC Auditing
// https://docs.spring.io/spring-data/jdbc/docs/current/reference/html/#auditing

@JsonIgnoreProperties(value = { "createdAt", "updatedAt" }, allowGetters = true)
public record Todo(
        @Id Long id,
        @NotBlank @Size(min = 2, max = 30) String title,
        Boolean completed,
        @CreatedDate @InsertOnlyProperty Instant createdAt,
        @LastModifiedDate Instant updatedAt) {

    public Todo(Long id, String title, Boolean completed) {
        this(id, title, completed, null, null);
    }

    // used in test
    public static Todo of(Long id, String title) {
        return new Todo(id, title, false, null, null);
    }

    // used in update
    public Todo withCreatedAt(Instant createdAt) {
        return new Todo(id, title, completed, createdAt, null);
    }

    public Todo patch(Map<String, Object> fields) throws ReflectiveOperationException {
        return patchRecord(Todo.class, this, fields);
    }

    // fake validation for demo purpose
    // Note: validation method must be private
    @AssertTrue(message = "Completed todo title must be more than 3 chars")
    private boolean isValidDemo() {

        if (Boolean.FALSE.equals(completed))
            return true;

        return title.length() >= 3;
    }
}
