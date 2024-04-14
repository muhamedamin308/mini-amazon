package com.example.miniamazon.data.classes

sealed class OrderStatus(val status: String) {
    data object Ordered: OrderStatus("Ordered")
    data object Delivered: OrderStatus("Delivered")
    data object Confirmed: OrderStatus("Confirmed")
    data object Canceled: OrderStatus("Canceled")
    data object Shipped: OrderStatus("Shipped")
    data object Returned: OrderStatus("Returned")
}

object OrderController {
    fun getOrderStatus(status: String): OrderStatus =
        when (status) {
            "Ordered" -> OrderStatus.Ordered
            "Delivered" -> OrderStatus.Delivered
            "Confirmed" -> OrderStatus.Confirmed
            "Canceled" -> OrderStatus.Canceled
            "Shipped" -> OrderStatus.Shipped
            else -> OrderStatus.Returned
        }
}