package com.example.demo.posts.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import reactor.test.StepVerifier;

// Flux Mono sample test using StepVerifier
// http://www.kswaughs.com/2020/06/spring-webflux-junit-test-example.html
// http://www.kswaughs.com/2020/06/spring-webflux-mono-junit-test-example.html

@Disabled("for manual test")
@SpringBootTest
class PostRepositoryTest {

    @Autowired
    private PostRepository repository;

    @Test
    void findAll_ok() {

        var fluxPosts = repository.findAll();

        var expectedTitle = "sunt aut facere repellat provident occaecati excepturi optio reprehenderit";
        var expectedBody = "quia et suscipit\n" + //
                "suscipit recusandae consequuntur expedita et cum\n" + //
                "reprehenderit molestiae ut ut quas totam\n" + //
                "nostrum rerum est autem sunt rem eveniet architecto";

        StepVerifier.create(fluxPosts)
                // asserting on first item only.
                .assertNext(post -> {
                    assertEquals(expectedTitle, post.title());
                    assertEquals(expectedBody, post.body());
                })
                // verifying the count of rest of items.
                .expectNextCount(99)
                .verifyComplete();
    }

    @Test
    void findById_ok() {

        var monoPost = repository.findById(2L);

        var expected = "qui est esse";

        StepVerifier.create(monoPost)
                .assertNext(post -> {
                    assertEquals(expected, post.title());
                })
                .verifyComplete();
    }

    @Test
    void findById_err() {

        var monoPost = repository.findById(99999999L);

        var expected = "No value present";

        StepVerifier.create(monoPost)
                .expectErrorSatisfies(thr -> {
                    assertTrue(thr instanceof NoSuchElementException);
                    assertEquals(expected, thr.getMessage());
                })
                .verify();
    }
}
