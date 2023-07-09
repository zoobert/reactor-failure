# Introduction
This repo demonstrates a problem when Project Reactor's automatic context propagation is enabled and both Spring Security and Micrometer tracing are enabled.
The result is the following warnings from Micrometer followed by an error:

```shell
2023-06-21T16:01:12.686-07:00  WARN 91795 --- [ctor-http-nio-2] i.m.o.c.ObservationThreadLocalAccessor   : Observation <{name=null(null), error=null, context=name='null', contextualName='null', error='null', lowCardinalityKeyValues=[], highCardinalityKeyValues=[], map=[], parentObservation={name=null(null), error=null, context=name='null', contextualName='null', error='null', lowCardinalityKeyValues=[], highCardinalityKeyValues=[], map=[], parentObservation={name=null(null), error=null, context=name='null', contextualName='null', error='null', lowCardinalityKeyValues=[], highCardinalityKeyValues=[], map=[], parentObservation=null}}}> to which we're restoring is not the same as the one set as this scope's parent observation <{name=null(null), error=null, context=name='null', contextualName='null', error='null', lowCardinalityKeyValues=[], highCardinalityKeyValues=[], map=[], parentObservation={name=null(null), error=null, context=name='null', contextualName='null', error='null', lowCardinalityKeyValues=[], highCardinalityKeyValues=[], map=[], parentObservation=null}}> . Most likely a manually created Observation has a scope opened that was never closed. This may lead to thread polluting and memory leaks
2023-06-21T16:01:12.686-07:00  WARN 91795 --- [ctor-http-nio-2] i.m.o.c.ObservationThreadLocalAccessor   : Observation <{name=null(null), error=null, context=name='null', contextualName='null', error='null', lowCardinalityKeyValues=[], highCardinalityKeyValues=[], map=[], parentObservation={name=null(null), error=null, context=name='null', contextualName='null', error='null', lowCardinalityKeyValues=[], highCardinalityKeyValues=[], map=[], parentObservation=null}}> to which we're restoring is not the same as the one set as this scope's parent observation <{name=null(null), error=null, context=name='null', contextualName='null', error='null', lowCardinalityKeyValues=[], highCardinalityKeyValues=[], map=[], parentObservation=null}> . Most likely a manually created Observation has a scope opened that was never closed. This may lead to thread polluting and memory leaks
2023-06-21T16:01:12.692-07:00  WARN 91795 --- [ctor-http-nio-2] i.m.o.c.ObservationThreadLocalAccessor   : Observation <{name=null(null), error=null, context=name='null', contextualName='null', error='null', lowCardinalityKeyValues=[], highCardinalityKeyValues=[], map=[], parentObservation=null}> to which we're restoring is not the same as the one set as this scope's parent observation <null> . Most likely a manually created Observation has a scope opened that was never closed. This may lead to thread polluting and memory leaks
2023-06-21T16:01:12.692-07:00  WARN 91795 --- [ctor-http-nio-2] i.m.o.c.ObservationThreadLocalAccessor   : There is no current scope in thread local. This situation should not happen
2023-06-21T16:01:12.692-07:00  WARN 91795 --- [ctor-http-nio-2] i.m.o.c.ObservationThreadLocalAccessor   : There is no current scope in thread local. This situation should not happen
2023-06-21T16:01:12.700-07:00 ERROR 91795 --- [ctor-http-nio-2] a.w.r.e.AbstractErrorWebExceptionHandler : [5cc59b73-1]  500 Server Error for HTTP GET "/v1/hello-world"
```

This problem was first encountered with the release of Spring Boot 3.1.1 which resolved some issues where observation scopes were piling up 
([#3831](https://github.com/micrometer-metrics/micrometer/pull/3831)). It only seems to happen on secured endpoints while tracing is also enabled. If either
of these are disabled, the problem does not occur.

## Usage
1. **Start the Server**— the server is a simple WebFlux application with a single GET endpoint at http://localhost:8002/v1/hello-world
   ```shell
   $> ./gradlew bootRun
   ```
2. **Hit the endpoint**— 
    ```shell
    $> curl http://localhost:8002/v1/hello-world/keys
    ```
   
You will see the call above succeed or fail depending upon whether or not you call `Hooks.enabledAutomaticContextPropogation()` in the main application. 
```kotlin
fun main(args: Array<String>) {
    Hooks.enableAutomaticContextPropagation()

    runApplication<ReactorFailureApplication>(*args)
}
```