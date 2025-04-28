package com.uzh.ase.dailygrind.postservice.post.service;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class UserServiceClient {

    private final WebClient webClient;

    public UserServiceClient() {
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:8080")
                .build();
    }

    public Mono<String[]> getFriends() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String token = ((JwtAuthenticationToken) authentication).getToken().getTokenValue();

        return webClient.get()
                .uri("/users/me/followers/ids")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(String[].class);
    }

}
