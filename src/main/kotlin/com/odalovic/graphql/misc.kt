package com.odalovic.graphql

sealed class Outcome<out T : Any>

data class Error(val message: String, val cause: Exception? = null) : Outcome<Nothing>()
data class Success<out T : Any>(val value: T) : Outcome<T>()

val <T> T.exhaustive
    get() = this

fun main() {
    when (val outcome = getOutcome()) {
        is Error -> println("There was an error calling the API: ${outcome.message}")
        is Success -> println(outcome.value)
    }.exhaustive
}

fun getOutcome(): Outcome<String> {
    if (Math.random() > 0.2)
        return Success("Dusan")
    return Error("What happened?", RuntimeException("Kaboom!"))
}