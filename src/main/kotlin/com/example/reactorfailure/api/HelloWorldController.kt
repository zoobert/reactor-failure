package com.example.reactorfailure.api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBodyOrNull

@RestController
@RequestMapping("/v1/hello-world")
class HelloWorldController {
    private val webClient = WebClient.create()

    @GetMapping()
    suspend fun get(): String =
        "Hello world!"

    @GetMapping("/keys")
    suspend fun keys(): String? =
        this.webClient.get()
            .uri("https://dev-ymp8imynkijzas0o.us.auth0.com/.well-known/jwks.json")
            .retrieve()
            .awaitBodyOrNull<String>()
}