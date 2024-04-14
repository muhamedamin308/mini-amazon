package com.example.miniamazon.data.helper

fun Float?.getProductPrice(price: Float): Float? {
    if (this == null) return null
    val remaining = 1f - this
    return remaining * price
}