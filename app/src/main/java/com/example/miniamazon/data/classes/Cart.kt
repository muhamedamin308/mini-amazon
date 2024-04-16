package com.example.miniamazon.data.classes

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Cart(
    val quantity: Int,
    val selectedColor: Int? = null,
    val selectedSize: String? = null,
    val products: Product
) : Parcelable {
    constructor() : this(1, null, null, Product())
}