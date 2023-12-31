package com.example.demo;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.ProblemDetail.forStatusAndDetail;

import java.util.NoSuchElementException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.example.demo.todos.models.FieldError;

import jakarta.validation.ConstraintViolationException;

// RFC7807 Problem Details using ResponseEntityExceptionHandler
// equivalent to spring.mvc.problemdetails.enabled=true

// custom messages.properties: Spring Boot 3.0 で入った RFC7807 サポートを色々試す
// https://qiita.com/koji-cw/items/422140bd7752e4a82baf

// setup message.properties
// https://docs.spring.io/spring-framework/docs/6.0.0/reference/html/web.html#mvc-ann-rest-exceptions-i18n

@RestControllerAdvice
public class AppControllerAdvice extends ResponseEntityExceptionHandler {

    // handle Spring validation exception
    @Override
    @Nullable
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        // set the message format in messages.properties
        var message = ex.getMessage();
        var code = ex.getDetailMessageCode();
        var args = ex.getDetailMessageArguments();

        var pd = createProblemDetail(ex, status, message, code, args, request);

        var errors = ex.getFieldErrors().stream()
                .map(e -> new FieldError(
                        e.getField(),
                        e.getCode(),
                        e.getDefaultMessage()))
                .toList();

        pd.setProperty("fieldErrors", errors);

        return handleExceptionInternal(ex, pd, headers, status, request);
    }

    // handle jakarta validation exception
    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolationException(ConstraintViolationException ex) {

        var pd = forStatusAndDetail(BAD_REQUEST, ex.getMessage());

        var errors = ex.getConstraintViolations().stream()
                .map(v -> new FieldError(
                        v.getPropertyPath().toString(),
                        v.getMessageTemplate(),
                        v.getMessage()))
                .toList();

        pd.setProperty("fieldErrors", errors);

        return pd;
    }

    // return details of JSON parse error not just Failed to read request
    @Override
    @Nullable
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        var message = ex.getMessage();
        logger.error("HttpMessageNotReadableException: " + message);

        // https://github.com/spring-projects/spring-framework/issues/29384
        var code = ex.getClass().getName();
        var args = new Object[] { ex.getMessage(), ex.getLocalizedMessage() };

        var defaultDetail = "Failed to read request: " + message;
        var detail = createProblemDetail(ex, status, defaultDetail, code, args, request);

        return handleExceptionInternal(ex, detail, headers, status, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException e) {
        return forStatusAndDetail(BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ProblemDetail handleNoSuchElementException(NoSuchElementException e) {
        return forStatusAndDetail(NOT_FOUND, e.getMessage());
    }

    // final exception handler which was not caught by specific handlers above
    @ExceptionHandler(RuntimeException.class)
    public ProblemDetail handleRuntimeException(RuntimeException e) {
        logger.error(e.getMessage(), e);
        return forStatusAndDetail(INTERNAL_SERVER_ERROR, e.getMessage());
    }
}
