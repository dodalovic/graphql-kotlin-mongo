package com.odalovic.graphql

sealed class Outcome<out T : Any>

data class Error(val message: String, val cause: Exception? = null) : Outcome<Nothing>()
data class Success<out T : Any>(val value: T) : Outcome<T>()

val <T> T.exhaustive
    get() = this
