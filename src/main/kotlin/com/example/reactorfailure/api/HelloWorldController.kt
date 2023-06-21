package com.example.reactorfailure.api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/hello-world")
class HelloWorldController {
    @GetMapping()
    suspend fun get(): String =
        "Hello world!"
}