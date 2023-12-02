package com.example.demo.posts.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.example.demo.posts.models.Post;

// WebTestClient sample test
// https://docs.spring.io/spring-framework/reference/testing/webtestclient.html

@Disabled("for manual test")
@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {

    @Autowired
    private WebTestClient client;

    // verify post id is 2
    private String expectedTitle = "qui est esse";
    private String expectedBody = """
            est rerum tempore vitae\n\
            sequi sint nihil reprehenderit dolor beatae ea dolores neque\n\
            fugiat blanditiis voluptate porro vel nihil molestiae ut reiciendis\n\
            qui aperiam non debitis possimus qui neque nisi nulla\
            """;

    @Test
    void findAll_ok() throws Exception {

        client.get()
                .uri("/posts")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Post.class)
                .consumeWith(resPosts -> {
                    var posts = resPosts.getResponseBody();
                    assertNotNull(posts);

                    var post = posts.get(1);
                    assertEquals(expectedTitle, post.title());
                    assertEquals(expectedBody, post.body());
                });
    }

    @Test
    void findById_ok() throws Exception {

        client.get()
                .uri("/posts/2")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Post.class)
                .consumeWith(resPost -> {
                    var post = resPost.getResponseBody();
                    assertNotNull(post);

                    assertEquals(expectedTitle, post.title());
                    assertEquals(expectedBody, post.body());
                });
    }

    @Test
    void findById_err() {

        var expectedTitle = "Not Found";
        var expectStatus = 404;
        var expectedDetail = "No value present";
        var expectedInstance = "/posts/99999999";

        client.get()
                .uri("/posts/99999999")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.title").isEqualTo(expectedTitle)
                .jsonPath("$.status").isEqualTo(expectStatus)
                .jsonPath("$.detail").isEqualTo(expectedDetail)
                .jsonPath("$.instance").isEqualTo(expectedInstance);
    }
}
