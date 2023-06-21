package com.example.reactorfailure

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import reactor.core.publisher.Hooks

@SpringBootApplication
class ReactorFailureApplication

fun main(args: Array<String>) {
    Hooks.enableAutomaticContextPropagation()

    runApplication<ReactorFailureApplication>(*args)
}
