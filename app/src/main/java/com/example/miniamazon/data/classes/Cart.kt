package com.example.miniamazon.data.classes

data class Cart (
    val quantity: Int,
    val selectedColor: Int? = null,
    val selectedSize: String? = null,
    val products: Product
) {
    constructor(): this(1, null, null, Product())
}