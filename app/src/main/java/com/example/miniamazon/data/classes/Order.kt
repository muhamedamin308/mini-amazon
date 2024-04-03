package com.example.miniamazon.data.classes

data class Order(
    val orderStatus: String,
    val totalPrice: Float,
    val cartProducts: List<Cart>,
    val address: Address
)