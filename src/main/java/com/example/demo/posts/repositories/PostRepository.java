package com.example.demo.posts.repositories;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

import com.example.demo.posts.models.Post;

import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// https://jsonplaceholder.typicode.com/posts

@HttpExchange("/posts")
public interface PostRepository {

    @GetExchange
    Flux<Post> findAll();

    @GetExchange("/{id}")
    Mono<Post> findById(@PathVariable Long id);

    @PostExchange
    Post create(@Valid @RequestBody Post post);

    @PutExchange("/{id}")
    Post update(@PathVariable Long id, @Valid @RequestBody Post post);

    @DeleteExchange("/{id}")
    ResponseEntity<Void> delete(@PathVariable Long id);
}
