package com.example.miniamazon.util

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<R>(data: R) : Resource<R>(data)
    class Error<E>(message: String) : Resource<E>(message = message)
    class Loading<Q> : Resource<Q>()
    class UnSpecified<H> : Resource<H>()
}