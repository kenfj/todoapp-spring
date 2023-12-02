package com.example.demo.configs.posts;

import static org.springframework.http.HttpStatusCode.valueOf;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import com.example.demo.posts.repositories.PostRepository;

import reactor.core.publisher.Mono;

@Configuration
public class WebClientConfig {

    @Value("${app.custom.base-url}")
    private String baseUrl;

    @Bean
    PostRepository postRepository() {

        // overwrite the default WebClientResponseException in defaultStatusHandler
        // 404 Not Found from GET https://jsonplaceholder.typicode.com/posts/99999999

        var client = WebClient
                .builder()
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, resp -> {
                    if (resp.statusCode().isSameCodeAs(valueOf(404))) {
                        return Mono.just(new NoSuchElementException("No value present"));
                    } else {
                        return Mono.just(new IllegalArgumentException("Invalid parameter"));
                    }
                })
                .defaultStatusHandler(HttpStatusCode::is5xxServerError,
                        resp -> Mono.just(new RuntimeException(resp.statusCode().toString())))
                .baseUrl(baseUrl)
                .build();

        var proxyFactory = HttpServiceProxyFactory
                .builder(WebClientAdapter.forClient(client))
                .build();

        return proxyFactory
                .createClient(PostRepository.class);
    }
}
