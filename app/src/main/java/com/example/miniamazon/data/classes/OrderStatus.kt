package com.example.miniamazon.data.classes

sealed class OrderStatus(val status: String) {
    data object Ordered: OrderStatus("Ordered")
    data object Delivered: OrderStatus("Delivered")
    data object Confirmed: OrderStatus("Confirmed")
    data object Canceled: OrderStatus("Canceled")
    data object Shipped: OrderStatus("Shipped")
    data object Returned: OrderStatus("Returned")
}