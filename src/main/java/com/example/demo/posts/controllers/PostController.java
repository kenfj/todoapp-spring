package com.example.demo.posts.controllers;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.posts.models.Post;
import com.example.demo.posts.repositories.PostRepository;

import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostRepository repository;

    public PostController(PostRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public Flux<Post> findAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Post> findById(@PathVariable Long id) {
        return repository.findById(id);
    }

    @PostMapping
    public Post create(@Valid @RequestBody Post post) {
        return repository.create(post);
    }

    @PutMapping("/{id}")
    public Post update(@PathVariable Long id, @Valid @RequestBody Post post) {
        return repository.update(id, post);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.delete(id);
    }
}
