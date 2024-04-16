package com.example.miniamazon.util

sealed class Status<T>(
    val data: T? = null, val message: String? = null
) {
    class Success<R>(data: R) : Status<R>(data)
    class Error<E>(message: String) : Status<E>(message = message)
    class Loading<Q> : Status<Q>()
    class UnSpecified<H> : Status<H>()
}