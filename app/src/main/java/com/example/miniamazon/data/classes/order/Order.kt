package com.example.miniamazon.data.classes.order

import android.os.Parcelable
import com.example.miniamazon.data.classes.Address
import com.example.miniamazon.data.classes.Cart
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random.Default.nextLong
@Parcelize
data class Order(
    val orderStatus: String = "",
    val totalPrice: Float = 0f,
    val cartProducts: List<Cart> = emptyList(),
    val address: Address = Address(),
    val orderDate: String = SimpleDateFormat("yyyy-MM-DD", Locale.ENGLISH).format(Date()),
    val orderId: Long = nextLong(0, 100_000_000_000) + totalPrice.toLong()
): Parcelable